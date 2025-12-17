package com.abidin.hospital.repository;

import com.abidin.hospital.entity.Appointment;
import com.abidin.hospital.entity.Doctor;
import com.abidin.hospital.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorAndAppointmentDate(Doctor doctor, LocalDate date);

    List<Appointment> findByPatient(Patient patient);

    List<Appointment> findByAppointmentDate(LocalDate date);
}
