package com.clinica.agenda.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clinica.agenda.entities.Especialidad;

public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {

}