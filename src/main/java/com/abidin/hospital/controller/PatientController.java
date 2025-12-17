package com.abidin.hospital.controller;

import com.abidin.hospital.entity.Patient;
import com.abidin.hospital.repository.PatientRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@CrossOrigin
public class PatientController {

    private final PatientRepository patientRepository;

    @GetMapping
    public List<Patient> getPatients(@RequestParam(value = "q", required = false) String query) {
        if (query == null || query.isBlank()) {
            return patientRepository.findAll();
        }
        return patientRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query);
    }

    @GetMapping("/{id}")
    public Patient getPatient(@PathVariable Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    @PostMapping
    public Patient createPatient(@Valid @RequestBody Patient patient) {
        patient.setId(null);
        return patientRepository.save(patient);
    }

    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable Long id, @Valid @RequestBody Patient request) {
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        existing.setNationalId(request.getNationalId());
        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setBirthDate(request.getBirthDate());
        existing.setGender(request.getGender());
        existing.setPhone(request.getPhone());
        existing.setEmail(request.getEmail());
        existing.setAddress(request.getAddress());
        existing.setEmergencyContact(request.getEmergencyContact());

        return patientRepository.save(existing);
    }
}
