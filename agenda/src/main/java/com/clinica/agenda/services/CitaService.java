package com.clinica.agenda.services;

import com.clinica.agenda.entities.Cita;
import com.clinica.agenda.entities.dto.CitaDetDTO;
import com.clinica.agenda.entities.dto.PacienteAtendidoDTO;
import com.clinica.agenda.enums.EstadoCita;

import java.time.LocalDate;
import java.util.List;

public interface CitaService {
        List<Cita> listarTodos();

        Cita buscarPorId(Long id);

        Cita guardar(Cita cita);

        Cita actualizar(Long id, Cita cita);

        void eliminar(Long id);

        List<Cita> obtenerCitasDoctor(
        Long doctorId);

        List<java.time.LocalTime> obtenerHorariosDisponibles
        (Long doctorId,
                java.time.LocalDate fecha);

        List<CitaDetDTO> obtenerPorFecha(
                Long doctorId,
                LocalDate fecha);        

        List<PacienteAtendidoDTO> obtenerPacientesAtendidos(
        Long doctorId);

        List<Cita> obtenerPorEstado(EstadoCita estado);
}
