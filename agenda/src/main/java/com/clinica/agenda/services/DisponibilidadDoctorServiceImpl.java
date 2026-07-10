package com.clinica.agenda.services;

import com.clinica.agenda.entities.DisponibilidadDoctor;
import com.clinica.agenda.repository.DisponibilidadDoctorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;



@Service
public class DisponibilidadDoctorServiceImpl
        implements DisponibilidadDoctorService {

        @Autowired
        private DisponibilidadDoctorRepository repository;

        @Override
        public List<DisponibilidadDoctor> listarTodos() {
                return repository.findAll();
        }

        @Override
        public DisponibilidadDoctor buscarPorId(Long id) {

                return repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Disponibilidad no encontrada"));
        }

        @Override
        public DisponibilidadDoctor guardar(
                DisponibilidadDoctor disponibilidadDoctor) {

                return repository.save(disponibilidadDoctor);
        }

        @Override
        public DisponibilidadDoctor actualizar(
                Long id,
                DisponibilidadDoctor disponibilidadDoctor) {

                DisponibilidadDoctor existente =
                        buscarPorId(id);

                existente.setDoctor(
                        disponibilidadDoctor.getDoctor());

                existente.setDiaSemana(
                        disponibilidadDoctor.getDiaSemana());

                existente.setHoraInicio(
                        disponibilidadDoctor.getHoraInicio());

                existente.setHoraFin(
                        disponibilidadDoctor.getHoraFin());

                existente.setDuracionMinutos(
                        disponibilidadDoctor.getDuracionMinutos());

                existente.setActivo(
                        disponibilidadDoctor.getActivo());

                return repository.save(existente);
        }

        @Override
        public void eliminar(Long id) {

                DisponibilidadDoctor disponibilidad =
                        buscarPorId(id);

                repository.delete(disponibilidad);
        }

        @Override
        public List<DisponibilidadDoctor> buscarPorDoctor(
                Long doctorId) {

                return repository.findByDoctor_Id(doctorId);
        }
}