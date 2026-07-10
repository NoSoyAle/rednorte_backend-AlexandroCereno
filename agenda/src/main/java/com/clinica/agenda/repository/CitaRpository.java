package com.clinica.agenda.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinica.agenda.entities.Cita;
import com.clinica.agenda.enums.EstadoCita;


import java.time.LocalDate;
import java.util.List;

public interface CitaRpository extends JpaRepository <Cita, Long> {
    List<Cita> findByDoctor_Id(Long doctorId);   

    List<Cita> findByDoctor_IdAndFecha(
        Long doctorId,
        LocalDate fecha);

    List<Cita> findByEstado(EstadoCita estado);
}
