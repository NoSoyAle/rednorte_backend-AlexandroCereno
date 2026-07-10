package com.clinica.agenda.services;

import com.clinica.agenda.entities.Cita;
import com.clinica.agenda.entities.DisponibilidadDoctor;
import com.clinica.agenda.repository.CitaRpository;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.web.reactive.function.client.WebClient;
import com.clinica.agenda.entities.dto.PacienteDTO;
import com.clinica.agenda.entities.dto.CitaDetDTO;
import com.clinica.agenda.entities.dto.PacienteAtendidoDTO;
import com.clinica.agenda.enums.DiaSemana;
import com.clinica.agenda.enums.EstadoCita;
import com.clinica.agenda.repository.DisponibilidadDoctorRepository;
import com.clinica.agenda.service.RabbitMQProducer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CitaServiceImplement implements CitaService {

        @Autowired
        private CitaRpository CitaRepo;

        @Autowired
        private DisponibilidadDoctorRepository disponibilidadDoctorRepository;

        @Autowired
        private WebClient pacienteWebClient;

        @Autowired
        private RabbitMQProducer rabbitMQProducer;

        @Override
        public List <Cita> listarTodos(){
                return CitaRepo.findAll();}

        @Override
        public Cita buscarPorId(Long id){
                return CitaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));}

        @Override
        public Cita guardar(Cita cita){
        PacienteDTO paciente = pacienteWebClient
                .get()
                .uri("/" + cita.getPacienteId())
                .retrieve()
                .bodyToMono(PacienteDTO.class)
                .block();

        if (paciente == null) {
                throw new RuntimeException("Paciente no encontrado");
        }

        Cita citaGuardada = CitaRepo.save(cita);

        // Publicar notificación de cita creada
        try {
                Map<String, String> notificacion = new HashMap<>();
                notificacion.put("tipo", "CITA_CREADA");
                notificacion.put("pacienteEmail", paciente.getEmail());
                notificacion.put("pacienteNombre", paciente.getNombre());
                notificacion.put("pacienteApellido", paciente.getApellido());
                notificacion.put("doctorNombre", cita.getDoctor().getNombre());
                notificacion.put("doctorApellido", cita.getDoctor().getApellido() != null ? cita.getDoctor().getApellido() : "");
                notificacion.put("doctorCorreo", cita.getDoctor().getCorreo());
                notificacion.put("fecha", cita.getFecha().toString());
                notificacion.put("horaInicio", cita.getHoraInicio().toString());
                notificacion.put("horaFin", cita.getHoraFin() != null ? cita.getHoraFin().toString() : "");
                notificacion.put("estado", cita.getEstado().name());
                rabbitMQProducer.enviarNotificacion(notificacion);
        } catch (Exception e) {
                System.err.println("Error al enviar notificación: " + e.getMessage());
        }

        return citaGuardada;
        }

        @Override
        public List<CitaDetDTO> obtenerPorFecha(
                Long doctorId,
                LocalDate fecha) {

        List<Cita> citas =
                CitaRepo.findByDoctor_IdAndFecha(
                                doctorId,
                                fecha);

        return citas.stream()
                .map(cita -> {PacienteDTO paciente =pacienteWebClient
                                        .get()
                                        .uri("/{id}",
                                                cita.getPacienteId())
                                        .retrieve()
                                        .bodyToMono(
                                                PacienteDTO.class)
                                        .block();
                        CitaDetDTO dto =new CitaDetDTO();
                        dto.setId(cita.getId());
                        dto.setPacienteId(cita.getPacienteId());
                        dto.setNombrePaciente(
                                paciente.getNombre()
                                + " "
                                + paciente.getApellido());
                        dto.setRutPaciente(paciente.getRut());
                        dto.setFecha(cita.getFecha());
                        dto.setHoraInicio(cita.getHoraInicio());
                        dto.setHoraFin(cita.getHoraFin());
                        dto.setEstado(cita.getEstado());
                        return dto;
                })
                .toList();}

        @Override
        public List<PacienteAtendidoDTO> obtenerPacientesAtendidos(
                Long doctorId) {
        List<Cita> citas =CitaRepo.findByDoctor_Id(doctorId);
        return citas.stream()
                .filter(c ->
                        c.getEstado() ==
                        EstadoCita.REALIZADA)
                .map(cita -> {PacienteDTO paciente =pacienteWebClient
                                .get()
                                .uri("/{id}",
                                        cita.getPacienteId())
                                .retrieve()
                                .bodyToMono(
                                        PacienteDTO.class)
                                .block();
                return new PacienteAtendidoDTO(
                        paciente.getId(),
                        paciente.getNombre(),
                        paciente.getApellido(),
                        paciente.getRut(),
                        paciente.getEmail(),
                        paciente.getTelefono()
                        );
                })
        .distinct()
        .toList();
        }



        @Override
        public Cita actualizar(Long id, Cita cita){
                Cita existente = buscarPorId(id);
                existente.setEstado(cita.getEstado());
                existente.setDoctor(cita.getDoctor());

                Cita citaActualizada = CitaRepo.save(existente);

                // Publicar notificación de cita actualizada
                try {
                        Map<String, String> notificacion = new HashMap<>();
                        notificacion.put("tipo", "CITA_ACTUALIZADA");
                        notificacion.put("pacienteEmail", "");
                        notificacion.put("pacienteNombre", "");
                        notificacion.put("pacienteApellido", "");
                        notificacion.put("doctorNombre", citaActualizada.getDoctor().getNombre());
                        notificacion.put("doctorApellido", citaActualizada.getDoctor().getApellido() != null ? citaActualizada.getDoctor().getApellido() : "");
                        notificacion.put("doctorCorreo", citaActualizada.getDoctor().getCorreo());
                        notificacion.put("fecha", citaActualizada.getFecha().toString());
                        notificacion.put("horaInicio", citaActualizada.getHoraInicio().toString());
                        notificacion.put("horaFin", citaActualizada.getHoraFin() != null ? citaActualizada.getHoraFin().toString() : "");
                        notificacion.put("estado", citaActualizada.getEstado().name());
                        rabbitMQProducer.enviarNotificacion(notificacion);
                } catch (Exception e) {
                        System.err.println("Error al enviar notificación: " + e.getMessage());
                }

                return citaActualizada;
        }

        @Override
        public void eliminar(Long id){CitaRepo.deleteById(id);}

        @Override
        public List<Cita> obtenerCitasDoctor(Long doctorId) {
                return CitaRepo.findByDoctor_Id(doctorId);}

        @Override
        public List<Cita> obtenerPorEstado(EstadoCita estado) {
                return CitaRepo.findByEstado(estado);}

        @Override
        public List<LocalTime> obtenerHorariosDisponibles(
                Long doctorId,
                LocalDate fecha) {

                DiaSemana diaSemana =
                        convertirDiaSemana(fecha);

                DisponibilidadDoctor disponibilidad =
                disponibilidadDoctorRepository
                        .findByDoctor_IdAndDiaSemana(
                                doctorId,
                                diaSemana)
                        .stream()
                        .findFirst()
                        .orElse(null);
                        if (disponibilidad == null) {
                                return new ArrayList<>();}

                List<Cita> citas =
                        CitaRepo.findByDoctor_IdAndFecha(
                                doctorId,
                                fecha);

                Set<LocalTime> horasOcupadas =
                        citas.stream()
                                .filter(c ->
                                        c.getEstado() != EstadoCita.CANCELADA)
                                .map(Cita::getHoraInicio)
                                .collect(Collectors.toSet());

                List<LocalTime> disponibles =
                        new ArrayList<>();

                LocalTime horaActual =
                        disponibilidad.getHoraInicio();

                while (horaActual.isBefore(
                        disponibilidad.getHoraFin())) {

                        if (!horasOcupadas.contains(horaActual)) {

                        disponibles.add(horaActual);

                        }

                        horaActual =
                                horaActual.plusMinutes(
                                        disponibilidad.getDuracionMinutos());
                }

                return disponibles;
        }

        private DiaSemana convertirDiaSemana(
        LocalDate fecha) {
                switch (fecha.getDayOfWeek()) {

                        case MONDAY:
                        return DiaSemana.LUNES;

                        case TUESDAY:
                        return DiaSemana.MARTES;

                        case WEDNESDAY:
                        return DiaSemana.MIERCOLES;

                        case THURSDAY:
                        return DiaSemana.JUEVES;

                        case FRIDAY:
                        return DiaSemana.VIERNES;

                        case SATURDAY:
                        return DiaSemana.SABADO;

                        case SUNDAY:
                        return DiaSemana.DOMINGO;

                        default:
                        throw new RuntimeException(
                                "Día inválido");
                }
        }
}
        