package com.paciente.paciente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paciente.paciente.Model.ServicioClinico;
import com.paciente.paciente.repository.ServicioClinicoRepository;

@Service
public class ServicioClinicoServiceImpl implements ServicioClinicoService {

    @Autowired
    private ServicioClinicoRepository servicioClinicoRepository;

    @Override
    public List<ServicioClinico> findAll() {
        return servicioClinicoRepository.findAll();
    }

    @Override
    public ServicioClinico findById(Long id) {
        return servicioClinicoRepository.findById(id).orElse(null);
    }

    @Override
    public ServicioClinico save(ServicioClinico servicio) {
        return servicioClinicoRepository.save(servicio);
    }

    @Override
    public ServicioClinico update(Long id, ServicioClinico servicio) {
        ServicioClinico existente = servicioClinicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio clinico no encontrado con id: " + id));
        existente.setNombre(servicio.getNombre());
        existente.setPrecioBase(servicio.getPrecioBase());
        existente.setCategoria(servicio.getCategoria());
        existente.setModalidad(servicio.getModalidad());
        existente.setDisponible(servicio.isDisponible());
        return servicioClinicoRepository.save(existente);
    }

    @Override
    public void deleteById(Long id) {
        if (!servicioClinicoRepository.existsById(id)) {
            throw new RuntimeException("Servicio clinico no encontrado con id: " + id);
        }
        servicioClinicoRepository.deleteById(id);
    }
}
