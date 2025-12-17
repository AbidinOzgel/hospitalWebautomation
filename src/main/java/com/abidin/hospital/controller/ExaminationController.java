package com.abidin.hospital.controller;

import com.abidin.hospital.entity.*;
import com.abidin.hospital.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/examinations")
@RequiredArgsConstructor
@CrossOrigin
public class ExaminationController {

    private final ExaminationRepository examinationRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @GetMapping
    public List<Examination> getExaminations(
            @RequestParam(value = "patientId", required = false) Long patientId,
            @RequestParam(value = "doctorId", required = false) Long doctorId
    ) {
        if (patientId != null) {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
            return examinationRepository.findByPatient(patient);
        }
        if (doctorId != null) {
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));
            return examinationRepository.findByDoctor(doctor);
        }
        return examinationRepository.findAll();
    }

    @GetMapping("/{id}")
    public Examination getExamination(@PathVariable Long id) {
        return examinationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Examination not found"));
    }

    @PostMapping
    public Examination createExamination(@RequestBody ExaminationRequest request) {
        Appointment appointment = appointmentRepository.findById(request.appointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Examination exam = Examination.builder()
                .appointment(appointment)
                .patient(patient)
                .doctor(doctor)
                .complaint(request.complaint())
                .anamnesis(request.anamnesis())
                .physicalExam(request.physicalExam())
                .diagnosis(request.diagnosis())
                .recommendations(request.recommendations())
                .build();

        // createdAt @PrePersist ile set edilecek
        return examinationRepository.save(exam);
    }

    @PutMapping("/{id}")
    public Examination updateExamination(@PathVariable Long id, @RequestBody ExaminationRequest request) {
        Examination existing = examinationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Examination not found"));

        Appointment appointment = appointmentRepository.findById(request.appointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        existing.setAppointment(appointment);
        existing.setPatient(patient);
        existing.setDoctor(doctor);
        existing.setComplaint(request.complaint());
        existing.setAnamnesis(request.anamnesis());
        existing.setPhysicalExam(request.physicalExam());
        existing.setDiagnosis(request.diagnosis());
        existing.setRecommendations(request.recommendations());

        return examinationRepository.save(existing);
    }

    public record ExaminationRequest(
            Long appointmentId,
            Long patientId,
            Long doctorId,
            String complaint,
            String anamnesis,
            String physicalExam,
            String diagnosis,
            String recommendations
    ) {}
}
