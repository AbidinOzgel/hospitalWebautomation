package com.abidin.hospital.controller;

import com.abidin.hospital.entity.*;
import com.abidin.hospital.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/lab")
@RequiredArgsConstructor
@CrossOrigin
public class LabController {

    private final LabTestRepository labTestRepository;
    private final LabRequestRepository labRequestRepository;
    private final LabRequestItemRepository labRequestItemRepository;
    private final ExaminationRepository examinationRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    // ---------- TEST TANIMLARI ----------

    @GetMapping("/tests")
    public List<LabTest> getTests() {
        return labTestRepository.findAll();
    }

    @PostMapping("/tests")
    public LabTest createTest(@RequestBody LabTest test) {
        test.setId(null);
        if (test.getActive() == null) {
            test.setActive(true);
        }
        return labTestRepository.save(test);
    }

    @PutMapping("/tests/{id}")
    public LabTest updateTest(@PathVariable Long id, @RequestBody LabTest request) {
        LabTest existing = labTestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LabTest not found"));

        existing.setCode(request.getCode());
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setUnit(request.getUnit());
        existing.setRefRange(request.getRefRange());
        existing.setActive(request.getActive() != null ? request.getActive() : existing.getActive());

        return labTestRepository.save(existing);
    }

    // ---------- İSTEM OLUŞTURMA ----------

    @PostMapping("/requests")
    public LabRequestDetail createRequest(@RequestBody LabRequestCreateRequest request) {
        Examination exam = examinationRepository.findById(request.examinationId())
                .orElseThrow(() -> new RuntimeException("Examination not found"));

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        LabRequest labRequest = LabRequest.builder()
                .examination(exam)
                .patient(patient)
                .doctor(doctor)
                .status("REQUESTED")
                .build();

        labRequest = labRequestRepository.save(labRequest);

        for (Long testId : request.testIds()) {
            LabTest test = labTestRepository.findById(testId)
                    .orElseThrow(() -> new RuntimeException("LabTest not found: " + testId));

            LabRequestItem item = LabRequestItem.builder()
                    .labRequest(labRequest)
                    .labTest(test)
                    .status("REQUESTED")
                    .build();

            labRequestItemRepository.save(item);
        }

        List<LabRequestItem> items = labRequestItemRepository.findByLabRequest(labRequest);

        return new LabRequestDetail(labRequest, items);
    }

    // ---------- İSTEM LİSTELEME ----------

    @GetMapping("/requests")
    public List<LabRequest> getRequests(@RequestParam(value = "status", required = false) String status) {
        if (status != null) {
            return labRequestRepository.findByStatus(status);
        }
        return labRequestRepository.findAll();
    }

    @GetMapping("/requests/{id}")
    public LabRequestDetail getRequest(@PathVariable Long id) {
        LabRequest request = labRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LabRequest not found"));
        List<LabRequestItem> items = labRequestItemRepository.findByLabRequest(request);
        return new LabRequestDetail(request, items);
    }

    // ---------- SONUÇ GİRİŞİ ----------

    @PatchMapping("/request-items/{itemId}/result")
    public LabRequestItem updateItemResult(@PathVariable Long itemId,
                                           @RequestBody LabResultUpdateRequest req) {
        LabRequestItem item = labRequestItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("LabRequestItem not found"));

        item.setResultValue(req.resultValue());
        item.setResultUnit(req.resultUnit());
        item.setRefRange(req.refRange());
        item.setResultNotes(req.resultNotes());
        item.setStatus("RESULTED");
        item.setResultedAt(LocalDateTime.now());

        LabRequestItem saved = labRequestItemRepository.save(item);

        // Tüm item’lar RESULTED ise ana request’i COMPLETED yap
        LabRequest parent = saved.getLabRequest();
        List<LabRequestItem> allItems = labRequestItemRepository.findByLabRequest(parent);

        boolean allResulted = allItems.stream()
                .allMatch(i -> "RESULTED".equalsIgnoreCase(i.getStatus()));

        if (allResulted) {
            parent.setStatus("COMPLETED");
            parent.setCompletedAt(LocalDateTime.now());
            labRequestRepository.save(parent);
        }

        return saved;
    }

    // ---------- DTO’lar ----------

    public record LabRequestCreateRequest(
            Long examinationId,
            Long patientId,
            Long doctorId,
            List<Long> testIds
    ) {}

    public record LabRequestDetail(
            LabRequest labRequest,
            List<LabRequestItem> items
    ) {}

    public record LabResultUpdateRequest(
            String resultValue,
            String resultUnit,
            String refRange,
            String resultNotes
    ) {}
}
