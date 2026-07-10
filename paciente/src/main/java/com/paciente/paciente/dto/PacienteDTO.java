package com.paciente.paciente.dto;

import lombok.Data;

@Data
public class PacienteDTO {
    private String rut;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
}
