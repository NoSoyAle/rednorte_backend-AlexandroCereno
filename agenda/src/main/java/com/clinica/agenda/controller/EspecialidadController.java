package com.clinica.agenda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.clinica.agenda.entities.Especialidad;

import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import com.clinica.agenda.services.EspecialidadService;

@RestController
@RequestMapping("/api/especialidad")
@CrossOrigin(origins = "http://localhost:5173")
public class EspecialidadController {
    @Autowired
    private EspecialidadService especialidadService;

    @GetMapping 
    public List<Especialidad> listarEspecialidades() {
        return especialidadService.listarEspecialidades();
    }

    @PostMapping
    public Especialidad guardarEspecialidad(@RequestBody Especialidad especialidad) {
        return especialidadService.crearEspecialidad(especialidad);
    }

    @GetMapping("/{id}")
    public Especialidad buscarEspecialidad(@PathVariable Long id) {
        return especialidadService.buscarEspecialidad(id)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada con id: " + id));
    }

    @PostMapping("/{id}")
    public Especialidad actualizarEspecialidad(
            @PathVariable Long id,
            @RequestBody Especialidad especialidad) {

        return especialidadService.actualizarEspecialidad(id, especialidad);
    };

    @DeleteMapping("/{id}")
    public void eliminarEspecialidad(@PathVariable Long id) {
        especialidadService.eliminarEspecialidad(id);
    }
}
