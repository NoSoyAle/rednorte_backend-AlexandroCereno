package com.clinica.agenda.service;
import com.clinica.agenda.entities.Especialidad;
import com.clinica.agenda.repository.EspecialidadRepository;
import com.clinica.agenda.services.EspecialidadServicesImplement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EspecialidadServiceImpleTests {
    @Mock
    private EspecialidadRepository especialidadRepository;

    @InjectMocks
    private EspecialidadServicesImplement especialidadService;

    private Especialidad especialidadTest;

    @BeforeEach
    void setUp() {
        especialidadTest = new Especialidad();
        especialidadTest.setId(1L);
        especialidadTest.setNombreEsp("Cardiología");
    }

    @Test
    @DisplayName("listarEspecialidades - debe retornar lista de especialidades")
    void listarEspecialidades_DebeRetornarLista() {
        when(especialidadRepository.findAll()).thenReturn(Arrays.asList(especialidadTest));

        List<Especialidad> resultado = especialidadService.listarEspecialidades();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(especialidadRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("crearEspecialidad - debe guardar y retornar la especialidad")
    void crearEspecialidad_DebeGuardarYRetornar() {
        when(especialidadRepository.save(especialidadTest)).thenReturn(especialidadTest);

        Especialidad resultado = especialidadService.crearEspecialidad(especialidadTest);

        assertNotNull(resultado);
        assertEquals("Cardiología", resultado.getNombreEsp());
        verify(especialidadRepository, times(1)).save(especialidadTest);
    }

    @Test
    @DisplayName("eliminarEspecialidad - debe ejecutar deleteById en el repositorio")
    void eliminarEspecialidad_DebeEjecutarDeleteById() {
        doNothing().when(especialidadRepository).deleteById(1L);

        especialidadService.eliminarEspecialidad(1L);

        verify(especialidadRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("buscarEspecialidad - debe retornar Optional con la especialidad")
    void buscarEspecialidad_DebeRetornarOptional() {
        when(especialidadRepository.findById(1L)).thenReturn(Optional.of(especialidadTest));

        Optional<Especialidad> resultado = especialidadService.buscarEspecialidad(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Cardiología", resultado.get().getNombreEsp());
    }

    @Test
    @DisplayName("actualizarEspecialidad - debe actualizar nombre si existe")
    void actualizarEspecialidad_DebeActualizarSiExiste() {
        Especialidad datosNuevos = new Especialidad();
        datosNuevos.setNombreEsp("Neurología");

        when(especialidadRepository.findById(1L)).thenReturn(Optional.of(especialidadTest));
        when(especialidadRepository.save(any(Especialidad.class))).thenAnswer(i -> i.getArgument(0));

        Especialidad resultado = especialidadService.actualizarEspecialidad(1L, datosNuevos);

        assertEquals("Neurología", resultado.getNombreEsp());
        verify(especialidadRepository, times(1)).save(any(Especialidad.class));
    }

    @Test
    @DisplayName("actualizarEspecialidad - debe lanzar RuntimeException si no existe")
    void actualizarEspecialidad_DebeLanzarExcepcionSiNoExiste() {
        when(especialidadRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> especialidadService.actualizarEspecialidad(99L, new Especialidad()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Especialidad no encontrada con id: 99");

        verify(especialidadRepository, never()).save(any(Especialidad.class));
    }
}
    

