package com.clinica.notificaciones.dto;

import lombok.Data;

@Data
public class NotificacionDTO {
    private String tipo;
    private String pacienteEmail;
    private String pacienteNombre;
    private String pacienteApellido;
    private String doctorNombre;
    private String doctorApellido;
    private String doctorCorreo;
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String estado;
}
