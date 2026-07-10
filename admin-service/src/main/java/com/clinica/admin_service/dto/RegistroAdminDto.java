package com.clinica.admin_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroAdminDto {
    private List<CitaDto> citasCanceladas;
    private List<CitaDto> citasConfirmadas;
    private List<EspecialidadRankingDto> especialidadesMayorConfirmacion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CitaDto {
        private Long id;
        private String estado;
        private Long doctorId;
        private Long pacienteId;
        private Long especialidadId;
        private String nombreEspecialidad;
        private LocalDate fechaCita;
        private LocalTime horaCita;
        private String motivoCancelacion;
        private Boolean confirmada;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EspecialidadRankingDto {
        private Long especialidadId;
        private String nombreEspecialidad;
        private Long totalCitasConfirmadas;
    }
}
