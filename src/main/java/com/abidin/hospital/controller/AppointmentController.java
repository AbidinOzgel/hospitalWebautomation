package com.abidin.hospital.controller;

import com.abidin.hospital.entity.Appointment;
import com.abidin.hospital.entity.Department;
import com.abidin.hospital.entity.Doctor;
import com.abidin.hospital.entity.Patient;
import com.abidin.hospital.repository.AppointmentRepository;
import com.abidin.hospital.repository.DepartmentRepository;
import com.abidin.hospital.repository.DoctorRepository;
import com.abidin.hospital.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;

    @GetMapping
    public List<Appointment> getAppointments(
            @RequestParam(value = "date", required = false) String dateStr
    ) {
        if (dateStr != null) {
            LocalDate date = LocalDate.parse(dateStr);
            return appointmentRepository.findByAppointmentDate(date);
        }
        return appointmentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Appointment getAppointment(@PathVariable Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    @PostMapping
    public Appointment createAppointment(@RequestBody AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Department department = null;
        if (request.departmentId() != null) {
            department = departmentRepository.findById(request.departmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .department(department)
                .appointmentDate(request.appointmentDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .status(request.status())
                .notes(request.notes())
                .build();

        return appointmentRepository.save(appointment);
    }

    @PutMapping("/{id}")
    public Appointment updateAppointment(@PathVariable Long id, @RequestBody AppointmentRequest request) {
        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Department department = null;
        if (request.departmentId() != null) {
            department = departmentRepository.findById(request.departmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
        }

        existing.setPatient(patient);
        existing.setDoctor(doctor);
        existing.setDepartment(department);
        existing.setAppointmentDate(request.appointmentDate());
        existing.setStartTime(request.startTime());
        existing.setEndTime(request.endTime());
        existing.setStatus(request.status());
        existing.setNotes(request.notes());

        return appointmentRepository.save(existing);
    }

    // Basit request DTO (inner record)
    public record AppointmentRequest(
            Long patientId,
            Long doctorId,
            Long departmentId,
            LocalDate appointmentDate,
            java.time.LocalTime startTime,
            java.time.LocalTime endTime,
            String status,
            String notes
    ) {}
}
