package com.abidin.hospital.controller;

import com.abidin.hospital.entity.*;
import com.abidin.hospital.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
@CrossOrigin
public class PrescriptionController {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final ExaminationRepository examinationRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @GetMapping
    public List<Prescription> getPrescriptions(
            @RequestParam(value = "patientId", required = false) Long patientId
    ) {
        if (patientId != null) {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
            return prescriptionRepository.findByPatient(patient);
        }
        return prescriptionRepository.findAll();
    }

    @GetMapping("/{id}")
    public PrescriptionDetailResponse getPrescription(@PathVariable Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        List<PrescriptionItem> items = prescriptionItemRepository.findByPrescription(prescription);

        return new PrescriptionDetailResponse(prescription, items);
    }

    @PostMapping
    public PrescriptionDetailResponse createPrescription(@RequestBody PrescriptionRequest request) {
        Examination exam = examinationRepository.findById(request.examinationId())
                .orElseThrow(() -> new RuntimeException("Examination not found"));

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Prescription prescription = Prescription.builder()
                .examination(exam)
                .patient(patient)
                .doctor(doctor)
                .build();

        prescription = prescriptionRepository.save(prescription);

        for (PrescriptionItemRequest itemReq : request.items()) {
            PrescriptionItem item = PrescriptionItem.builder()
                    .prescription(prescription)
                    .drugName(itemReq.drugName())
                    .dose(itemReq.dose())
                    .frequency(itemReq.frequency())
                    .duration(itemReq.duration())
                    .notes(itemReq.notes())
                    .build();
            prescriptionItemRepository.save(item);
        }

        List<PrescriptionItem> items = prescriptionItemRepository.findByPrescription(prescription);
        return new PrescriptionDetailResponse(prescription, items);
    }

    public record PrescriptionItemRequest(
            String drugName,
            String dose,
            String frequency,
            String duration,
            String notes
    ) {}

    public record PrescriptionRequest(
            Long examinationId,
            Long patientId,
            Long doctorId,
            List<PrescriptionItemRequest> items
    ) {}

    public record PrescriptionDetailResponse(
            Prescription prescription,
            List<PrescriptionItem> items
    ) {}
}
