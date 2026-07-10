package com.clinica.agenda.entities;

import jakarta.persistence.*;
import java.time.LocalTime;
import com.clinica.agenda.enums.DiaSemana;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "disponibilidad_doctor")
public class DisponibilidadDoctor {

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    private DiaSemana diaSemana;

    private LocalTime horaInicio;

    private LocalTime horaFin;

    private Integer duracionMinutos;

    private Boolean activo;

    
} 