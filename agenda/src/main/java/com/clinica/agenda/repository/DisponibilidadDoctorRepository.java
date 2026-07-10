package com.clinica.agenda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinica.agenda.entities.DisponibilidadDoctor;
import com.clinica.agenda.enums.DiaSemana;
import java.util.List;

public interface DisponibilidadDoctorRepository
        extends JpaRepository<DisponibilidadDoctor, Long> {

        List<DisponibilidadDoctor> findByDoctor_Id(Long doctorId);

        List<DisponibilidadDoctor> findByDoctor_IdAndDiaSemana(
                Long doctorId,
                DiaSemana diaSemana
        );


}