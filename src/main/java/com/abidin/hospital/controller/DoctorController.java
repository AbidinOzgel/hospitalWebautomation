package com.abidin.hospital.controller;

import com.abidin.hospital.entity.Department;
import com.abidin.hospital.entity.Doctor;
import com.abidin.hospital.entity.User;
import com.abidin.hospital.repository.DepartmentRepository;
import com.abidin.hospital.repository.DoctorRepository;
import com.abidin.hospital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@CrossOrigin
public class DoctorController {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    @GetMapping
    public List<Doctor> getDoctors(@RequestParam(value = "departmentId", required = false) Long departmentId) {
        if (departmentId != null) {
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            return doctorRepository.findByDepartment(department);
        }
        return doctorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Doctor getDoctor(@PathVariable Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    @PostMapping
    public Doctor createDoctor(@RequestBody DoctorRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Department department = null;
        if (request.departmentId() != null) {
            department = departmentRepository.findById(request.departmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
        }

        Doctor doctor = Doctor.builder()
                .user(user)
                .department(department)
                .title(request.title())
                .licenseNo(request.licenseNo())
                .build();

        return doctorRepository.save(doctor);
    }

    @PutMapping("/{id}")
    public Doctor updateDoctor(@PathVariable Long id, @RequestBody DoctorRequest request) {
        Doctor existing = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (request.userId() != null) {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            existing.setUser(user);
        }

        if (request.departmentId() != null) {
            Department department = departmentRepository.findById(request.departmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            existing.setDepartment(department);
        }

        if (request.title() != null) existing.setTitle(request.title());
        if (request.licenseNo() != null) existing.setLicenseNo(request.licenseNo());

        return doctorRepository.save(existing);
    }

    public record DoctorRequest(
            Long userId,
            Long departmentId,
            String title,
            String licenseNo
    ) {}
}
