package com.clinica.admin_service.repository;

import com.clinica.admin_service.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByRut(String rut);
    Optional<Usuario> findByNombre(String nombre);
    Optional<Usuario> findByEmail(String email);
}
