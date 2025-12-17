package com.abidin.hospital.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lab_request_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabRequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lab_request_id", nullable = false)
    private LabRequest labRequest;

    @ManyToOne
    @JoinColumn(name = "lab_test_id", nullable = false)
    private LabTest labTest;

    @Column(length = 20)
    private String status; // REQUESTED, RESULTED

    @Column(name = "result_value", length = 50)
    private String resultValue;

    @Column(name = "result_unit", length = 20)
    private String resultUnit;

    @Column(name = "ref_range", length = 50)
    private String refRange;

    @Column(name = "result_notes", columnDefinition = "TEXT")
    private String resultNotes;

    @Column(name = "resulted_at")
    private LocalDateTime resultedAt;
}
