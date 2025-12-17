package com.abidin.hospital.controller;

import com.abidin.hospital.entity.Appointment;
import com.abidin.hospital.entity.Department;
import com.abidin.hospital.entity.Doctor;
import com.abidin.hospital.entity.Patient;
import com.abidin.hospital.repository.AppointmentRepository;
import com.abidin.hospital.repository.DepartmentRepository;
import com.abidin.hospital.repository.DoctorRepository;
import com.abidin.hospital.repository.PatientRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentMvcController extends BaseController {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;

    @GetMapping
    public String list(@RequestParam(value = "date", required = false)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       Model model,
                       HttpSession session) {

        setCommonModel(model, session, "appointments", "Randevular");

        List<Appointment> appointments;
        if (date != null) {
            appointments = appointmentRepository.findByAppointmentDate(date);
        } else {
            appointments = appointmentRepository.findAll();
        }

        model.addAttribute("appointments", appointments);
        model.addAttribute("filterDate", date);
        model.addAttribute("contentTemplate", "appointments/list");
        return "layout";
    }

    @GetMapping("/new")
    public String newForm(Model model, HttpSession session) {
        setCommonModel(model, session, "appointments", "Yeni Randevu");
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("contentTemplate", "appointments/form");
        return "layout";
    }

    @PostMapping
    public String create(@RequestParam Long patientId,
                         @RequestParam Long doctorId,
                         @RequestParam(required = false) Long departmentId,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
                         @RequestParam String status,
                         @RequestParam(required = false) String notes,
                         HttpSession session) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Department department = null;
        if (departmentId != null) {
            department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));
        }

        Appointment app = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .department(department)
                .appointmentDate(appointmentDate)
                .startTime(startTime)
                .endTime(endTime)
                .status(status)
                .notes(notes)
                .build();

        appointmentRepository.save(app);
        session.setAttribute("flashMessage", "Randevu oluşturuldu.");
        return "redirect:/appointments";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        setCommonModel(model, session, "appointments", "Randevu Düzenle");

        Appointment app = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        model.addAttribute("appointment", app);
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("contentTemplate", "appointments/form");
        return "layout";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam Long patientId,
                         @RequestParam Long doctorId,
                         @RequestParam(required = false) Long departmentId,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
                         @RequestParam String status,
                         @RequestParam(required = false) String notes,
                         HttpSession session) {

        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Department department = null;
        if (departmentId != null) {
            department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));
        }

        existing.setPatient(patient);
        existing.setDoctor(doctor);
        existing.setDepartment(department);
        existing.setAppointmentDate(appointmentDate);
        existing.setStartTime(startTime);
        existing.setEndTime(endTime);
        existing.setStatus(status);
        existing.setNotes(notes);

        appointmentRepository.save(existing);
        session.setAttribute("flashMessage", "Randevu güncellendi.");
        return "redirect:/appointments";
    }
}
