package com.clinica.notificaciones.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.clinica.notificaciones.dto.NotificacionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NotificacionConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public NotificacionConsumer(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "notificaciones")
    public void recibirMensaje(String mensaje) {
        try {
            NotificacionDTO notificacion = objectMapper.readValue(mensaje, NotificacionDTO.class);
            emailService.enviarNotificacionCita(notificacion);
        } catch (Exception e) {
            System.err.println("Error al procesar mensaje de notificación: " + e.getMessage());
        }
    }
}
