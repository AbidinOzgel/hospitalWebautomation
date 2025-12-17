package com.abidin.hospital.controller;

import com.abidin.hospital.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin
public class DashboardController {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final ExaminationRepository examinationRepository;
    private final LabRequestRepository labRequestRepository;

    @GetMapping("/overview")
    public OverviewResponse getOverview() {
        LocalDate today = LocalDate.now();

        long totalPatients = patientRepository.count();
        long todayAppointments = appointmentRepository.findByAppointmentDate(today).size();
        long totalExaminations = examinationRepository.count();
        long totalLabRequests = labRequestRepository.count();

        return new OverviewResponse(
                totalPatients,
                todayAppointments,
                totalExaminations,
                totalLabRequests
        );
    }

    public record OverviewResponse(
            long totalPatients,
            long todayAppointments,
            long totalExaminations,
            long totalLabRequests
    ) {}
}
