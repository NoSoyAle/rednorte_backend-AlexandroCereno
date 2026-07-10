package com.paciente.paciente.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "servicios_clinicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioClinico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private double precioBase;
    private String categoria; // Ej: "CONSULTA", "EXAMEN", "URGENCIA"
    private String modalidad; // Ej: "PRESENCIAL", "TELEMEDICINA"
    private boolean disponible;
}
