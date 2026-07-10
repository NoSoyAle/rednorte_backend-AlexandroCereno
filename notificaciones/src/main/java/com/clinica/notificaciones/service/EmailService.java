package com.clinica.notificaciones.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.clinica.notificaciones.dto.NotificacionDTO;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarNotificacionCita(NotificacionDTO notificacion) {
        String asunto = generarAsunto(notificacion);
        String cuerpo = generarCuerpo(notificacion);

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(notificacion.getPacienteEmail());
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);

        mailSender.send(mensaje);
    }

    private String generarAsunto(NotificacionDTO notificacion) {
        switch (notificacion.getTipo()) {
            case "CITA_CREADA":
                return "Cita médica confirmada";
            case "CITA_ACTUALIZADA":
                return "Actualización de su cita médica";
            case "CITA_CANCELADA":
                return "Cita médica cancelada";
            default:
                return "Notificación de cita médica";
        }
    }

    private String generarCuerpo(NotificacionDTO notificacion) {
        StringBuilder sb = new StringBuilder();
        sb.append("Estimado/a ").append(notificacion.getPacienteNombre())
          .append(" ").append(notificacion.getPacienteApellido()).append(",\n\n");

        switch (notificacion.getTipo()) {
            case "CITA_CREADA":
                sb.append("Su cita médica ha sido confirmada exitosamente.\n\n");
                break;
            case "CITA_ACTUALIZADA":
                sb.append("Su cita médica ha sido actualizada.\n\n");
                break;
            case "CITA_CANCELADA":
                sb.append("Su cita médica ha sido cancelada.\n\n");
                break;
            default:
                sb.append("Detalles de su cita médica:\n\n");
        }

        sb.append("Detalles de la cita:\n");
        sb.append("- Médico: Dr. ").append(notificacion.getDoctorNombre()).append(" ").append(notificacion.getDoctorApellido()).append("\n");
        sb.append("- Fecha: ").append(notificacion.getFecha()).append("\n");
        sb.append("- Horario: ").append(notificacion.getHoraInicio()).append(" - ").append(notificacion.getHoraFin()).append("\n");
        sb.append("- Estado: ").append(notificacion.getEstado()).append("\n\n");

        sb.append("Si tiene alguna consulta, por favor contacte a su centro médico.\n");
        sb.append("Saludos cordiales,\nRed Norte Salud");

        return sb.toString();
    }
}
