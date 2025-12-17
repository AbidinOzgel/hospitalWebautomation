package com.abidin.hospital.controller;

import com.abidin.hospital.entity.*;
import com.abidin.hospital.repository.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/prescriptions")
public class PrescriptionMvcController extends BaseController {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final ExaminationRepository examinationRepository;

    @GetMapping
    public String list(Model model, HttpSession session) {
        setCommonModel(model, session, "examinations", "Reçeteler");

        List<Prescription> prescriptions = prescriptionRepository.findAll();
        Map<Long, Long> itemCounts = new HashMap<>();
        for (Prescription p : prescriptions) {
            long c = prescriptionItemRepository.findByPrescription(p).size();
            itemCounts.put(p.getId(), c);
        }

        model.addAttribute("prescriptions", prescriptions);
        model.addAttribute("itemCounts", itemCounts);
        model.addAttribute("contentTemplate", "prescriptions/list");
        return "layout";
    }

    @GetMapping("/new")
    public String newForm(Model model, HttpSession session) {
        setCommonModel(model, session, "examinations", "Yeni Reçete");

        model.addAttribute("examinations", examinationRepository.findAll());
        model.addAttribute("contentTemplate", "prescriptions/form");
        return "layout";
    }

    @PostMapping
    public String create(@RequestParam Long examinationId,
                         @RequestParam(required = false, name = "drugName") List<String> drugNames,
                         @RequestParam(required = false, name = "dose") List<String> doses,
                         @RequestParam(required = false, name = "frequency") List<String> frequencies,
                         @RequestParam(required = false, name = "duration") List<String> durations,
                         @RequestParam(required = false, name = "notes") List<String> notes,
                         HttpSession session) {

        Examination exam = examinationRepository.findById(examinationId)
                .orElseThrow(() -> new RuntimeException("Examination not found"));

        Prescription p = Prescription.builder()
                .examination(exam)
                .patient(exam.getPatient())
                .doctor(exam.getDoctor())
                .build();

        p = prescriptionRepository.save(p);

        if (drugNames != null) {
            int size = drugNames.size();
            for (int i = 0; i < size; i++) {
                String dn = drugNames.get(i);
                if (dn == null || dn.isBlank()) continue;

                String dDose = doses != null && i < doses.size() ? doses.get(i) : null;
                String freq = frequencies != null && i < frequencies.size() ? frequencies.get(i) : null;
                String dur = durations != null && i < durations.size() ? durations.get(i) : null;
                String note = notes != null && i < notes.size() ? notes.get(i) : null;

                PrescriptionItem item = PrescriptionItem.builder()
                        .prescription(p)
                        .drugName(dn)
                        .dose(dDose)
                        .frequency(freq)
                        .duration(dur)
                        .notes(note)
                        .build();

                prescriptionItemRepository.save(item);
            }
        }

        session.setAttribute("flashMessage", "Reçete oluşturuldu.");
        return "redirect:/prescriptions";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
        setCommonModel(model, session, "examinations", "Reçete Detayı");

        Prescription p = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));
        List<PrescriptionItem> items = prescriptionItemRepository.findByPrescription(p);

        model.addAttribute("prescription", p);
        model.addAttribute("items", items);
        model.addAttribute("contentTemplate", "prescriptions/detail");
        return "layout";
    }
}
