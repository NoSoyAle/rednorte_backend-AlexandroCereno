package com.clinica.agenda.entities.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;



import com.clinica.agenda.enums.EstadoCita;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class CitaDetDTO {
    private Long id;

    private Long pacienteId;

    private String nombrePaciente;

    private String rutPaciente;

    private LocalDate fecha;

    private LocalTime horaInicio;

    private LocalTime horaFin;

    private EstadoCita estado;


}
