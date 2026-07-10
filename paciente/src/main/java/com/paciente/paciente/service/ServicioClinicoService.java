package com.paciente.paciente.service;

import java.util.List;

import com.paciente.paciente.Model.ServicioClinico;

public interface ServicioClinicoService {

    List<ServicioClinico> findAll();

    ServicioClinico findById(Long id);

    ServicioClinico save(ServicioClinico servicio);

    ServicioClinico update(Long id, ServicioClinico servicio);

    void deleteById(Long id);
}
