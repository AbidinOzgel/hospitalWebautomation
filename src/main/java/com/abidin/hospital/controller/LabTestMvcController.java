package com.abidin.hospital.controller;

import com.abidin.hospital.entity.LabTest;
import com.abidin.hospital.repository.LabTestRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lab/tests")
public class LabTestMvcController extends BaseController {

    private final LabTestRepository labTestRepository;

    @GetMapping
    public String list(Model model, HttpSession session) {
        setCommonModel(model, session, "labtests", "Lab Testleri");

        List<LabTest> tests = labTestRepository.findAll();
        model.addAttribute("tests", tests);
        model.addAttribute("contentTemplate", "lab/tests");
        return "layout";
    }

    @GetMapping("/new")
    public String newForm(Model model, HttpSession session) {
        setCommonModel(model, session, "labtests", "Yeni Lab Testi");

        model.addAttribute("labTest", new LabTest());
        model.addAttribute("contentTemplate", "lab/test-form");
        return "layout";
    }

    @PostMapping
    public String create(@ModelAttribute LabTest labTest, HttpSession session) {
        labTest.setId(null);
        if (labTest.getActive() == null) labTest.setActive(true);
        labTestRepository.save(labTest);
        session.setAttribute("flashMessage", "Lab testi oluşturuldu.");
        return "redirect:/lab/tests";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        setCommonModel(model, session, "labtests", "Lab Testi Düzenle");

        LabTest test = labTestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LabTest not found"));

        model.addAttribute("labTest", test);
        model.addAttribute("contentTemplate", "lab/test-form");
        return "layout";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute LabTest form,
                         HttpSession session) {

        LabTest existing = labTestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LabTest not found"));

        existing.setCode(form.getCode());
        existing.setName(form.getName());
        existing.setDescription(form.getDescription());
        existing.setUnit(form.getUnit());
        existing.setRefRange(form.getRefRange());
        existing.setActive(form.getActive() != null ? form.getActive() : existing.getActive());

        labTestRepository.save(existing);
        session.setAttribute("flashMessage", "Lab testi güncellendi.");
        return "redirect:/lab/tests";
    }
}
