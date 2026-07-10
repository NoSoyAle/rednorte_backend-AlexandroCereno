package com.paciente.paciente.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paciente.paciente.Model.Paciente;
import com.paciente.paciente.repository.PacienteRepository;

@Service
public class PacienteServiceImpl implements PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Override
    public List <Paciente> findAll() {
        return pacienteRepository.findAll();
    }

    @Override
    public Paciente findById(Long id) {
        return pacienteRepository.findById(id).orElse(null);
    }

    @Override
    public Paciente save(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    @Override
    public void deletebyId(Long id) {
        pacienteRepository.deleteById(id);
    }

    @Override
    public Paciente buscarPorRut(String rut) {

        return pacienteRepository.findByRut(rut)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Paciente no encontrado"));
    }

    @Override
    public Paciente actualizar(
            Long id,
            Paciente paciente) {

        Paciente existente =
                pacienteRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Paciente no encontrado"));

        existente.setNombre(
                paciente.getNombre());

        existente.setApellido(
                paciente.getApellido());

        existente.setEmail(
                paciente.getEmail());

        existente.setTelefono(
                paciente.getTelefono());

        existente.setFechaNacimiento(
                paciente.getFechaNacimiento());

        existente.setDireccion(
                paciente.getDireccion());

        return pacienteRepository.save(
                existente);
    }

}
