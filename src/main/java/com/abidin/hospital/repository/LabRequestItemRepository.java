package com.abidin.hospital.repository;

import com.abidin.hospital.entity.LabRequest;
import com.abidin.hospital.entity.LabRequestItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabRequestItemRepository extends JpaRepository<LabRequestItem, Long> {

    List<LabRequestItem> findByLabRequest(LabRequest labRequest);
}
