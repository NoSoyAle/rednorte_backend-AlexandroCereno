package com.paciente.paciente.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paciente.paciente.Model.ServicioClinico;

@Repository
public interface ServicioClinicoRepository extends JpaRepository<ServicioClinico, Long> {
    // Hereda automáticamente todos los métodos de búsqueda y guardado de Spring Data JPA
}