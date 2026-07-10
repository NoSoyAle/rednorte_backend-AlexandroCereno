package com.clinica.admin_service.service;

import com.clinica.admin_service.dto.RegistroDoctorRequest;
import com.clinica.admin_service.dto.UsuarioEstadoDTO;
import com.clinica.admin_service.model.Usuario;
import com.clinica.admin_service.dto.DashboardKpiDto;
import com.clinica.admin_service.dto.RegistroAdminDto;
import java.util.List;

public interface UsuarioService {
    Usuario guardarUsuario(Usuario usuario);
    Usuario registrarDoctor(RegistroDoctorRequest request);
    List<Usuario> listarUsuarios();
    Usuario buscarPorRut(String rut);
    void eliminarUsuario(Long id);
    DashboardKpiDto obtenerMetricasDashboard();
    Usuario actualizarEstadoUsuario(Long id, UsuarioEstadoDTO dto);
    RegistroAdminDto obtenerRegistroAdmin();
}