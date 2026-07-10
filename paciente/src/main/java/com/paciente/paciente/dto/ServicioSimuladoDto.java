package com.paciente.paciente.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioSimuladoDto {
    private Long id;
    private String nombre;
    private String categoria;
    private String modalidad;
    private double precioBase;
    private double precioCopagoEstimado; // El valor calculado final
    private String coberturaAplicada;   // Detalle textual (ej: "Fonasa - 80% dscto")
}