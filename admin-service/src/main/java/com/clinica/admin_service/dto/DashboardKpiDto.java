package com.clinica.admin_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardKpiDto {
private long totalUsuarios;
    private long totalAdministradores;
    private long totalDoctores;
    private Map<String, Long> usuariosPorRol; //grafico de distribucion
}
