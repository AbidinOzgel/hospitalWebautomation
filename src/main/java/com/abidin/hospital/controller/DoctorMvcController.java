package com.abidin.hospital.controller;

import com.abidin.hospital.entity.Department;
import com.abidin.hospital.entity.Doctor;
import com.abidin.hospital.entity.User;
import com.abidin.hospital.repository.DepartmentRepository;
import com.abidin.hospital.repository.DoctorRepository;
import com.abidin.hospital.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/doctors")
public class DoctorMvcController extends BaseController {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    @GetMapping
    public String list(@RequestParam(value = "departmentId", required = false) Long departmentId,
                       Model model,
                       HttpSession session) {

        setCommonModel(model, session, "doctors", "Doktorlar");

        List<Doctor> doctors;
        if (departmentId != null) {
            Department dept = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            doctors = doctorRepository.findByDepartment(dept);
        } else {
            doctors = doctorRepository.findAll();
        }

        model.addAttribute("doctors", doctors);
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("selectedDepartmentId", departmentId);
        model.addAttribute("contentTemplate", "doctors/list");
        return "layout";
    }

    @GetMapping("/new")
    public String newForm(Model model, HttpSession session) {
        setCommonModel(model, session, "doctors", "Yeni Doktor");

        model.addAttribute("doctor", new Doctor());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("contentTemplate", "doctors/form");
        return "layout";
    }

    @PostMapping
    public String create(@RequestParam Long userId,
                         @RequestParam(required = false) Long departmentId,
                         @RequestParam(required = false) String title,
                         @RequestParam(required = false) String licenseNo,
                         HttpSession session) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Department dept = null;
        if (departmentId != null) {
            dept = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));
        }

        Doctor doctor = Doctor.builder()
                .user(user)
                .department(dept)
                .title(title)
                .licenseNo(licenseNo)
                .build();

        doctorRepository.save(doctor);
        session.setAttribute("flashMessage", "Doktor kaydı oluşturuldu.");
        return "redirect:/doctors";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        setCommonModel(model, session, "doctors", "Doktor Düzenle");

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        model.addAttribute("doctor", doctor);
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("contentTemplate", "doctors/form");
        return "layout";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam Long userId,
                         @RequestParam(required = false) Long departmentId,
                         @RequestParam(required = false) String title,
                         @RequestParam(required = false) String licenseNo,
                         HttpSession session) {

        Doctor existing = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        existing.setUser(user);

        if (departmentId != null) {
            Department dept = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            existing.setDepartment(dept);
        } else {
            existing.setDepartment(null);
        }

        existing.setTitle(title);
        existing.setLicenseNo(licenseNo);

        doctorRepository.save(existing);
        session.setAttribute("flashMessage", "Doktor kaydı güncellendi.");
        return "redirect:/doctors";
    }
}
