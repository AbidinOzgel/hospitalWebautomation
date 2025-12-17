package com.abidin.hospital.controller;

import com.abidin.hospital.repository.AppointmentRepository;
import com.abidin.hospital.repository.ExaminationRepository;
import com.abidin.hospital.repository.LabRequestRepository;
import com.abidin.hospital.repository.PatientRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class DashboardMvcController extends BaseController {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final ExaminationRepository examinationRepository;
    private final LabRequestRepository labRequestRepository;

    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        setCommonModel(model, session, "dashboard", "Genel Bakış");

        LocalDate today = LocalDate.now();
        long totalPatients = patientRepository.count();
        long todayAppointments = appointmentRepository.findByAppointmentDate(today).size();
        long totalExaminations = examinationRepository.count();
        long totalLabRequests = labRequestRepository.count();

        model.addAttribute("totalPatients", totalPatients);
        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("totalExaminations", totalExaminations);
        model.addAttribute("totalLabRequests", totalLabRequests);

        model.addAttribute("contentTemplate", "dashboard");
        return "layout";
    }
}
