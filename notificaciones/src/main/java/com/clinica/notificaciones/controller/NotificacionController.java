package com.clinica.notificaciones.controller;

import com.clinica.notificaciones.dto.NotificacionDTO;
import com.clinica.notificaciones.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificacionController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/test")
    public ResponseEntity<?> testNotificacion(@RequestBody NotificacionDTO notificacion) {
        try {
            emailService.enviarNotificacionCita(notificacion);
            return ResponseEntity.ok(Map.of("mensaje", "Correo enviado exitosamente a " + notificacion.getPacienteEmail()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al enviar correo: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "notificaciones"));
    }
}
