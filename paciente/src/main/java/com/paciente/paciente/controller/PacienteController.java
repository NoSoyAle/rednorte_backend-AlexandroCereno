package com.paciente.paciente.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paciente.paciente.Model.Paciente;
import com.paciente.paciente.service.PacienteService;

@RestController 
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping
    public List<Paciente> listar(){
        return pacienteService.findAll();
    }

    @GetMapping("/{id}")
    public Paciente obtener(@PathVariable Long id) { 
        return pacienteService.findById(id); 
    }

    @PostMapping
    public Paciente crear(@RequestBody Paciente paciente) { 
        return pacienteService.save(paciente); 
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        pacienteService.deletebyId(id);
    }

    @GetMapping("/rut/{rut}")
    public Paciente buscarPorRut(
            @PathVariable String rut) {

        return pacienteService
                .buscarPorRut(rut);
    }

    @PutMapping("/{id}")
    public Paciente actualizar(
            @PathVariable Long id,
            @RequestBody Paciente paciente) {

        return pacienteService
                .actualizar(id, paciente);
    }
}
