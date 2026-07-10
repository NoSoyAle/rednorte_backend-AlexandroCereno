package com.clinica.autenticacion.repository;

import com.clinica.autenticacion.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  Optional<Usuario> findByNombre(String nombre);
  Optional<Usuario> findFirstByNombre(String nombre);
}
