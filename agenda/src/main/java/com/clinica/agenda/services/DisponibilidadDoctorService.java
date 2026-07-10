package com.clinica.agenda.services;

import com.clinica.agenda.entities.DisponibilidadDoctor;
import java.util.List;

public interface DisponibilidadDoctorService {

        List<DisponibilidadDoctor> listarTodos();

        DisponibilidadDoctor buscarPorId(Long id);

        DisponibilidadDoctor guardar(
                DisponibilidadDoctor disponibilidadDoctor);

        DisponibilidadDoctor actualizar(
                Long id,
                DisponibilidadDoctor disponibilidadDoctor);

        void eliminar(Long id);

        List<DisponibilidadDoctor> buscarPorDoctor(
                Long doctorId);
        }