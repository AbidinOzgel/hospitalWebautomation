package com.abidin.hospital.controller;

import com.abidin.hospital.entity.Patient;
import com.abidin.hospital.repository.PatientRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/patients")
public class PatientMvcController extends BaseController {

    private final PatientRepository patientRepository;

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q,
                       Model model,
                       HttpSession session) {

        setCommonModel(model, session, "patients", "Hastalar");

        List<Patient> patients;
        if (q != null && !q.isBlank()) {
            patients = patientRepository
                    .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(q, q);
        } else {
            patients = patientRepository.findAll();
        }

        model.addAttribute("patients", patients);
        model.addAttribute("query", q);
        model.addAttribute("contentTemplate", "patients/list");
        return "layout";
    }

    @GetMapping("/new")
    public String createForm(Model model, HttpSession session) {
        setCommonModel(model, session, "patients", "Yeni Hasta");
        model.addAttribute("patient", new Patient());
        model.addAttribute("contentTemplate", "patients/form");
        return "layout";
    }

    @PostMapping
    public String create(@ModelAttribute Patient patient, HttpSession session, Model model) {
        patient.setId(null);
        patientRepository.save(patient);
        session.setAttribute("flashMessage", "Hasta kaydı oluşturuldu.");
        return "redirect:/patients";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        setCommonModel(model, session, "patients", "Hasta Düzenle");
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        model.addAttribute("patient", patient);
        model.addAttribute("contentTemplate", "patients/form");
        return "layout";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute Patient form,
                         HttpSession session) {

        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        existing.setNationalId(form.getNationalId());
        existing.setFirstName(form.getFirstName());
        existing.setLastName(form.getLastName());
        existing.setBirthDate(form.getBirthDate());
        existing.setGender(form.getGender());
        existing.setPhone(form.getPhone());
        existing.setEmail(form.getEmail());
        existing.setAddress(form.getAddress());
        existing.setEmergencyContact(form.getEmergencyContact());

        patientRepository.save(existing);
        session.setAttribute("flashMessage", "Hasta kaydı güncellendi.");
        return "redirect:/patients";
    }
}
