package com.abidin.hospital.controller;

import com.abidin.hospital.entity.Department;
import com.abidin.hospital.repository.DepartmentRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/departments")
public class DepartmentMvcController extends BaseController {

    private final DepartmentRepository departmentRepository;

    @GetMapping
    public String list(Model model, HttpSession session) {
        setCommonModel(model, session, "departments", "Poliklinikler");

        List<Department> departments = departmentRepository.findAll();
        model.addAttribute("departments", departments);
        model.addAttribute("contentTemplate", "departments/list");
        return "layout";
    }

    @GetMapping("/new")
    public String newForm(Model model, HttpSession session) {
        setCommonModel(model, session, "departments", "Yeni Poliklinik");
        model.addAttribute("department", new Department());
        model.addAttribute("contentTemplate", "departments/form");
        return "layout";
    }

    @PostMapping
    public String create(@ModelAttribute Department department, HttpSession session) {
        department.setId(null);
        if (department.getActive() == null) {
            department.setActive(true);
        }
        departmentRepository.save(department);
        session.setAttribute("flashMessage", "Poliklinik kaydı oluşturuldu.");
        return "redirect:/departments";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        setCommonModel(model, session, "departments", "Poliklinik Düzenle");
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        model.addAttribute("department", department);
        model.addAttribute("contentTemplate", "departments/form");
        return "layout";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Department form,
                         HttpSession session) {

        Department existing = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        existing.setName(form.getName());
        existing.setCode(form.getCode());
        existing.setDescription(form.getDescription());
        existing.setActive(form.getActive() != null ? form.getActive() : existing.getActive());

        departmentRepository.save(existing);
        session.setAttribute("flashMessage", "Poliklinik kaydı güncellendi.");
        return "redirect:/departments";
    }
}
