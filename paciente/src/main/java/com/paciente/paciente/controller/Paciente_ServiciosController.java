package com.paciente.paciente.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.paciente.paciente.Model.ServicioClinico;
import com.paciente.paciente.dto.ServicioSimuladoDto;
import com.paciente.paciente.service.ServicioClinicoService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/paciente")
public class Paciente_ServiciosController {

    @Autowired
    private ServicioClinicoService servicioClinicoService;

    @GetMapping("/servicios")
    public ResponseEntity<List<ServicioSimuladoDto>> listarServiciosSimulados(
            @RequestParam(required = false, defaultValue = "PARTICULAR") String prevision,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String modalidad) {

        List<ServicioClinico> servicios = servicioClinicoService.findAll();

        List<ServicioSimuladoDto> filtradosYSimulados = servicios.stream()
                .filter(s -> categoria == null || s.getCategoria().equalsIgnoreCase(categoria))
                .filter(s -> modalidad == null || s.getModalidad().equalsIgnoreCase(modalidad))
                .map(s -> {
                    double copago = s.getPrecioBase();
                    String cobertura = "Particular (Sin Dscto)";

                    if ("FONASA".equalsIgnoreCase(prevision)) {
                        copago = s.getPrecioBase() * 0.20;
                        cobertura = "Tramos Fonasa (80% cubierto)";
                    } else if ("ISAPRE".equalsIgnoreCase(prevision)) {
                        copago = s.getPrecioBase() * 0.40;
                        cobertura = "Isapre Plan Preferencial (60% cubierto)";
                    }

                    return new ServicioSimuladoDto(
                            s.getId(), s.getNombre(), s.getCategoria(),
                            s.getModalidad(), s.getPrecioBase(), copago, cobertura
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(filtradosYSimulados);
    }

    @GetMapping("/servicios/{id}")
    public ResponseEntity<ServicioClinico> obtenerServicio(@PathVariable Long id) {
        ServicioClinico servicio = servicioClinicoService.findById(id);
        if (servicio == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(servicio);
    }

    @PostMapping("/servicios")
    public ResponseEntity<ServicioClinico> crearServicio(@RequestBody ServicioClinico servicio) {
        ServicioClinico creado = servicioClinicoService.save(servicio);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/servicios/{id}")
    public ResponseEntity<ServicioClinico> actualizarServicio(@PathVariable Long id, @RequestBody ServicioClinico servicio) {
        try {
            ServicioClinico actualizado = servicioClinicoService.update(id, servicio);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/servicios/{id}")
    public ResponseEntity<Void> eliminarServicio(@PathVariable Long id) {
        try {
            servicioClinicoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/atenciones/{id}/datos-comprobante")
    public ResponseEntity<Map<String, Object>> obtenerDatosComprobante(@PathVariable String id) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("idAtencion", id);
        datos.put("fecha", "11-06-2026");
        datos.put("doctor", "Dr. Francisco Orostica");
        datos.put("especialidad", "Medicina General RedNorte");
        datos.put("estadoAtencion", "Completada al 100%");
        datos.put("establecimiento", "Clínica RedNorte Central");
        return ResponseEntity.ok(datos);
    }
}