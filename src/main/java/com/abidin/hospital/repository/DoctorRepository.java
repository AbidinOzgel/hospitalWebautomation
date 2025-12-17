package com.abidin.hospital.repository;

import com.abidin.hospital.entity.Doctor;
import com.abidin.hospital.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByDepartment(Department department);
}
