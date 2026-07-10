package com.clinica.agenda.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.clinica.agenda.repository.EspecialidadRepository;
import com.clinica.agenda.entities.Especialidad;
import java.util.List;
import java.util.Optional;

@Service
public class EspecialidadServicesImplement implements EspecialidadService {

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Override
    public List<Especialidad> listarEspecialidades() {
        return especialidadRepository.findAll();
    }  

    @Override
    public Especialidad crearEspecialidad(Especialidad especialidad) {
        return especialidadRepository.save(especialidad);
    }

    @Override
    public void eliminarEspecialidad(Long id) {
        especialidadRepository.deleteById(id);
    }

    @Override
    public Optional<Especialidad> buscarEspecialidad(Long id) {
        return especialidadRepository.findById(id);
    }

    @Override
    public Especialidad actualizarEspecialidad(Long id, Especialidad especialidad) {
        return especialidadRepository.findById(id)
                .map(existingEspecialidad -> {
                    existingEspecialidad.setNombreEsp(especialidad.getNombreEsp());
                    return especialidadRepository.save(existingEspecialidad);
                })
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada con id: " + id));
    }

}
