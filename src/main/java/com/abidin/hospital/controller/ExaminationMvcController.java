package com.abidin.hospital.controller;

import com.abidin.hospital.entity.Appointment;
import com.abidin.hospital.entity.Examination;
import com.abidin.hospital.repository.AppointmentRepository;
import com.abidin.hospital.repository.ExaminationRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/examinations")
public class ExaminationMvcController extends BaseController {

    private final ExaminationRepository examinationRepository;
    private final AppointmentRepository appointmentRepository;

    @GetMapping
    public String list(Model model, HttpSession session) {
        setCommonModel(model, session, "examinations", "Muayeneler");
        List<Examination> exams = examinationRepository.findAll();
        model.addAttribute("examinations", exams);
        model.addAttribute("contentTemplate", "examinations/list");
        return "layout";
    }

    @GetMapping("/new")
    public String newForm(Model model, HttpSession session) {
        setCommonModel(model, session, "examinations", "Yeni Muayene");

        model.addAttribute("appointments", appointmentRepository.findAll());
        model.addAttribute("contentTemplate", "examinations/form");
        return "layout";
    }

    @PostMapping
    public String create(@RequestParam Long appointmentId,
                         @RequestParam(required = false) String complaint,
                         @RequestParam(required = false) String anamnesis,
                         @RequestParam(required = false) String physicalExam,
                         @RequestParam(required = false) String diagnosis,
                         @RequestParam(required = false) String recommendations,
                         HttpSession session) {

        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Examination exam = Examination.builder()
                .appointment(appt)
                .patient(appt.getPatient())
                .doctor(appt.getDoctor())
                .complaint(complaint)
                .anamnesis(anamnesis)
                .physicalExam(physicalExam)
                .diagnosis(diagnosis)
                .recommendations(recommendations)
                .build();

        examinationRepository.save(exam);
        session.setAttribute("flashMessage", "Muayene kaydedildi.");
        return "redirect:/examinations";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        setCommonModel(model, session, "examinations", "Muayene Düzenle");

        Examination exam = examinationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Examination not found"));

        model.addAttribute("examination", exam);
        model.addAttribute("appointments", appointmentRepository.findAll());
        model.addAttribute("contentTemplate", "examinations/form");
        return "layout";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam Long appointmentId,
                         @RequestParam(required = false) String complaint,
                         @RequestParam(required = false) String anamnesis,
                         @RequestParam(required = false) String physicalExam,
                         @RequestParam(required = false) String diagnosis,
                         @RequestParam(required = false) String recommendations,
                         HttpSession session) {

        Examination existing = examinationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Examination not found"));

        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        existing.setAppointment(appt);
        existing.setPatient(appt.getPatient());
        existing.setDoctor(appt.getDoctor());
        existing.setComplaint(complaint);
        existing.setAnamnesis(anamnesis);
        existing.setPhysicalExam(physicalExam);
        existing.setDiagnosis(diagnosis);
        existing.setRecommendations(recommendations);

        examinationRepository.save(existing);
        session.setAttribute("flashMessage", "Muayene güncellendi.");
        return "redirect:/examinations";
    }
}
