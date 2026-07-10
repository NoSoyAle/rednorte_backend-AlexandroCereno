package com.clinica.agenda.entities.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PacienteAtendidoDTO {
    private Long pacienteId;
    private String nombre;
    private String apellido;
    private String rut;
    private String email;
    private String telefono;

}
