package com.clinica.admin_service.controller;

import com.clinica.admin_service.dto.DashboardKpiDto;
import com.clinica.admin_service.dto.RegistroAdminDto;
import com.clinica.admin_service.dto.RegistroDoctorRequest;
import com.clinica.admin_service.dto.UsuarioEstadoDTO;
import com.clinica.admin_service.model.Usuario;
import com.clinica.admin_service.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("admin-service funcionando correctamente");
    }

    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.guardarUsuario(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrarDoctor(@RequestBody RegistroDoctorRequest request) {
        Usuario nuevoDoctor = usuarioService.registrarDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDoctor);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping("/usuarios/{rut}")
    public ResponseEntity<Usuario> buscarPorRut(@PathVariable String rut) {
        return ResponseEntity.ok(usuarioService.buscarPorRut(rut));
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/usuarios/{id}/estado")
    public ResponseEntity<Usuario> actualizarEstadoUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioEstadoDTO dto) {
        Usuario usuarioActualizado = usuarioService.actualizarEstadoUsuario(id, dto);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @GetMapping("/dashboard/kpis")
    public ResponseEntity<DashboardKpiDto> obtenerKpis() {
        return ResponseEntity.ok(usuarioService.obtenerMetricasDashboard());
    }

    @GetMapping("/registro")
    public ResponseEntity<RegistroAdminDto> obtenerRegistroAdmin() {
        return ResponseEntity.ok(usuarioService.obtenerRegistroAdmin());
    }
}