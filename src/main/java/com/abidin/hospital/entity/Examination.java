package com.abidin.hospital.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "examinations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Examination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Hangi randevuya bağlı
    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(columnDefinition = "TEXT")
    private String complaint;    // Şikayet

    @Column(columnDefinition = "TEXT")
    private String anamnesis;    // Hikaye

    @Column(name = "physical_exam", columnDefinition = "TEXT")
    private String physicalExam; // Fizik muayene

    @Column(columnDefinition = "TEXT")
    private String diagnosis;    // Tanı

    @Column(columnDefinition = "TEXT")
    private String recommendations; // Öneriler

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
