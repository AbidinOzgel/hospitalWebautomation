package com.abidin.hospital.repository;

import com.abidin.hospital.entity.LabRequest;
import com.abidin.hospital.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabRequestRepository extends JpaRepository<LabRequest, Long> {

    List<LabRequest> findByPatient(Patient patient);

    List<LabRequest> findByStatus(String status);
}
