package com.abidin.hospital.repository;

import com.abidin.hospital.entity.Prescription;
import com.abidin.hospital.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByPatient(Patient patient);
}
