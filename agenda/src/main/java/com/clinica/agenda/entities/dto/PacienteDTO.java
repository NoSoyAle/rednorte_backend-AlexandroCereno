package com.clinica.agenda.entities.dto;
import java.time.LocalDate;

import lombok.Data;

@Data
public class PacienteDTO {
    
    private Long id;
    private String rut;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String direccion;
    
    public PacienteDTO() {
    }

    public PacienteDTO(Long id, String rut, String nombre, String apellido, String email, String telefono,
            LocalDate fechaNacimiento, String direccion) {
        this.id = id;
        this.rut = rut;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
    }

    

    
}
