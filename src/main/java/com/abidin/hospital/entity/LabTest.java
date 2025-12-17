package com.abidin.hospital.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lab_tests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name; // Hemogram, Glukoz vs.

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 20)
    private String unit; // mg/dL vs.

    @Column(name = "ref_range", length = 50)
    private String refRange;

    @Column(nullable = false)
    private Boolean active = true;
}
