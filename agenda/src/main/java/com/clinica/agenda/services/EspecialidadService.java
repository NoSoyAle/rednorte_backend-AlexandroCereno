package com.clinica.agenda.services;

import com.clinica.agenda.entities.Especialidad;
import java.util.List;
import java.util.Optional;  

public interface EspecialidadService {
    List<Especialidad> listarEspecialidades();

    Especialidad crearEspecialidad(Especialidad especialidad);
    
    Optional<Especialidad> buscarEspecialidad(Long id);
    //Porque la especialidad puede no existir, entonces se devuelve un Optional para manejar ese caso.

    void eliminarEspecialidad(Long id);
    Especialidad actualizarEspecialidad(Long id, Especialidad especialidad);



}
