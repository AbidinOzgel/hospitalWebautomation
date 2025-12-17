package com.abidin.hospital.controller;

import com.abidin.hospital.entity.*;
import com.abidin.hospital.repository.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lab/requests")
public class LabRequestMvcController extends BaseController {

    private final LabRequestRepository labRequestRepository;
    private final LabRequestItemRepository labRequestItemRepository;
    private final LabTestRepository labTestRepository;
    private final ExaminationRepository examinationRepository;

    @GetMapping
    public String list(@RequestParam(value = "status", required = false) String status,
                       Model model,
                       HttpSession session) {

        setCommonModel(model, session, "lab", "Lab İstekleri");

        List<LabRequest> requests;
        if (status != null && !status.isBlank()) {
            requests = labRequestRepository.findByStatus(status);
        } else {
            requests = labRequestRepository.findAll();
        }

        model.addAttribute("requests", requests);
        model.addAttribute("statusFilter", status);
        model.addAttribute("contentTemplate", "lab/requests");
        return "layout";
    }

    @GetMapping("/new")
    public String newForm(Model model, HttpSession session) {
        setCommonModel(model, session, "lab", "Yeni Lab İsteği");

        model.addAttribute("examinations", examinationRepository.findAll());
        model.addAttribute("tests", labTestRepository.findAll());
        model.addAttribute("contentTemplate", "lab/request-form");
        return "layout";
    }

    @PostMapping
    public String create(@RequestParam Long examinationId,
                         @RequestParam(name = "testIds", required = false) List<Long> testIds,
                         HttpSession session) {

        Examination ex = examinationRepository.findById(examinationId)
                .orElseThrow(() -> new RuntimeException("Examination not found"));

        LabRequest lr = LabRequest.builder()
                .examination(ex)
                .patient(ex.getPatient())
                .doctor(ex.getDoctor())
                .status("REQUESTED")
                .build();

        lr = labRequestRepository.save(lr);

        if (testIds != null) {
            for (Long testId : testIds) {
                LabTest test = labTestRepository.findById(testId)
                        .orElseThrow(() -> new RuntimeException("LabTest not found"));

                LabRequestItem item = LabRequestItem.builder()
                        .labRequest(lr)
                        .labTest(test)
                        .status("REQUESTED")
                        .build();
                labRequestItemRepository.save(item);
            }
        }

        session.setAttribute("flashMessage", "Lab isteği oluşturuldu.");
        return "redirect:/lab/requests";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
        setCommonModel(model, session, "lab", "Lab İsteği Detayı");

        LabRequest req = labRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LabRequest not found"));
        List<LabRequestItem> items = labRequestItemRepository.findByLabRequest(req);

        model.addAttribute("request", req);
        model.addAttribute("items", items);
        model.addAttribute("contentTemplate", "lab/request-detail");
        return "layout";
    }

    @PostMapping("/items/{itemId}/result")
    public String updateResult(@PathVariable Long itemId,
                               @RequestParam(required = false) String resultValue,
                               @RequestParam(required = false) String resultUnit,
                               @RequestParam(required = false) String refRange,
                               @RequestParam(required = false) String resultNotes,
                               HttpSession session) {

        LabRequestItem item = labRequestItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("LabRequestItem not found"));

        item.setResultValue(resultValue);
        item.setResultUnit(resultUnit);
        item.setRefRange(refRange);
        item.setResultNotes(resultNotes);
        item.setStatus("RESULTED");
        item.setResultedAt(LocalDateTime.now());

        LabRequestItem saved = labRequestItemRepository.save(item);

        LabRequest parent = saved.getLabRequest();
        List<LabRequestItem> all = labRequestItemRepository.findByLabRequest(parent);
        boolean allResulted = all.stream().allMatch(i -> "RESULTED".equalsIgnoreCase(i.getStatus()));
        if (allResulted) {
            parent.setStatus("COMPLETED");
            parent.setCompletedAt(LocalDateTime.now());
            labRequestRepository.save(parent);
        }

        session.setAttribute("flashMessage", "Sonuç kaydedildi.");
        return "redirect:/lab/requests/" + parent.getId();
    }
}
