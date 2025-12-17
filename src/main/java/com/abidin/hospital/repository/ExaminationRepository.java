package com.abidin.hospital.repository;

import com.abidin.hospital.entity.Examination;
import com.abidin.hospital.entity.Doctor;
import com.abidin.hospital.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExaminationRepository extends JpaRepository<Examination, Long> {

    List<Examination> findByPatient(Patient patient);

    List<Examination> findByDoctor(Doctor doctor);
}
