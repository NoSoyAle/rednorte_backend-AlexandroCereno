package com.clinica.agenda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.clinica.agenda.entities.Doctor;
import com.clinica.agenda.entities.Especialidad;
import com.clinica.agenda.repository.EspecialidadRepository;
import com.clinica.agenda.services.DoctorServices;


import java.util.List;  

@RestController
@RequestMapping("/api/doctor")
@CrossOrigin(origins = "http://localhost:5173")
public class DoctorController {
    @Autowired
    private DoctorServices doctorServices;
    @Autowired
    private EspecialidadRepository especialidadRepository;

    @GetMapping
    public List<Doctor> listarDoctores() {
        return doctorServices.listarDoctores();
    }

    
    @GetMapping("/{id}")
    public Doctor obtenerDoctor(@PathVariable Long id) {
        return doctorServices.buscarDoctor(id);
    }

    // Crear doctor o modificar doctor existente
    @PostMapping
    public Doctor guardarDoctor(@RequestBody Doctor doctor) {
        return doctorServices.crearDoctor(doctor);
    }

    // Eliminar doctor
    @DeleteMapping("/{id}")
    public void eliminarDoctor(@PathVariable Long id) {
        doctorServices.eliminarDoctor(id);
    }

    @PutMapping("/{id}")
    public Doctor actualizarDoctor(
            @PathVariable Long id,
            @RequestBody Doctor doctor) {

        return doctorServices.actualizarDoctor(id, doctor);
    }

    @GetMapping("/especialidad/{idEspecialidad}")
    public List<Doctor> obtenerPorEspecialidad(
            @PathVariable Long idEspecialidad) {

        return doctorServices.buscarPorEspecialidad(
                idEspecialidad);
    }

    @GetMapping("/{doctorId}/especialidades")
    public List<Especialidad> obtenerEspecialidadesDoctor(
            @PathVariable Long doctorId) {

        return doctorServices
                .obtenerEspecialidadesDoctor(
                        doctorId);
    }
    @GetMapping("/rut/{rut}")
    public Doctor buscarPorRut(
            @PathVariable String rut) {

        return doctorServices.buscarPorRut(rut);
    }
}

