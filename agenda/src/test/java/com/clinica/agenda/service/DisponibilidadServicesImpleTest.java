package com.clinica.agenda.service;
import com.clinica.agenda.entities.DisponibilidadDoctor;
import com.clinica.agenda.enums.DiaSemana;
import com.clinica.agenda.repository.DisponibilidadDoctorRepository;
import com.clinica.agenda.services.DisponibilidadDoctorServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DisponibilidadServicesImpleTest {
    @Mock
    private DisponibilidadDoctorRepository repository;

    @InjectMocks
    private DisponibilidadDoctorServiceImpl disponibilidadService;

    private DisponibilidadDoctor disponibilidadTest;

    @BeforeEach
    void setUp() {
        disponibilidadTest = new DisponibilidadDoctor();
        disponibilidadTest.setId(1L);
        // Asumiendo que DiaSemana es el Enum que vimos en el servicio anterior
        disponibilidadTest.setDiaSemana(DiaSemana.LUNES); 
        disponibilidadTest.setHoraInicio(LocalTime.of(8, 0));
        disponibilidadTest.setHoraFin(LocalTime.of(14, 0));
        disponibilidadTest.setDuracionMinutos(30);
        disponibilidadTest.setActivo(true);
    }

    @Test
    @DisplayName("listarTodos - debe retornar todas las disponibilidades")
    void listarTodos_DebeRetornarTodasLasDisponibilidades() {
        when(repository.findAll()).thenReturn(Arrays.asList(disponibilidadTest));

        List<DisponibilidadDoctor> resultado = disponibilidadService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("buscarPorId - debe retornar disponibilidad si existe")
    void buscarPorId_DebeRetornarDisponibilidadSiExiste() {
        when(repository.findById(1L)).thenReturn(Optional.of(disponibilidadTest));

        DisponibilidadDoctor resultado = disponibilidadService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(DiaSemana.LUNES, resultado.getDiaSemana());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId - debe lanzar excepcion si no existe")
    void buscarPorId_DebeLanzarExcepcionSiNoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> disponibilidadService.buscarPorId(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Disponibilidad no encontrada");

        verify(repository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("guardar - debe persistir y retornar la disponibilidad")
    void guardar_DebeGuardarYRetornarDisponibilidad() {
        when(repository.save(disponibilidadTest)).thenReturn(disponibilidadTest);

        DisponibilidadDoctor resultado = disponibilidadService.guardar(disponibilidadTest);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(repository, times(1)).save(disponibilidadTest);
    }

    @Test
    @DisplayName("actualizar - debe modificar los campos y guardar la disponibilidad existente")
    void actualizar_DebeActualizarCamposYGuardar() {
        // Objeto con los nuevos datos que queremos aplicar
        DisponibilidadDoctor datosNuevos = new DisponibilidadDoctor();
        datosNuevos.setDiaSemana(DiaSemana.MARTES);
        datosNuevos.setHoraInicio(LocalTime.of(10, 0));
        datosNuevos.setHoraFin(LocalTime.of(18, 0));
        datosNuevos.setDuracionMinutos(45);
        datosNuevos.setActivo(false);

        // Simulamos que encuentra el original
        when(repository.findById(1L)).thenReturn(Optional.of(disponibilidadTest));
        // Simulamos que al guardar, retorna el mismo objeto que se le pasó (ya modificado)
        when(repository.save(any(DisponibilidadDoctor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DisponibilidadDoctor resultado = disponibilidadService.actualizar(1L, datosNuevos);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId()); // Verifica que mantiene el ID original
        assertEquals(DiaSemana.MARTES, resultado.getDiaSemana());
        assertEquals(LocalTime.of(10, 0), resultado.getHoraInicio());
        assertEquals(LocalTime.of(18, 0), resultado.getHoraFin());
        assertEquals(45, resultado.getDuracionMinutos());
        assertFalse(resultado.getActivo());
        
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(DisponibilidadDoctor.class));
    }

    @Test
    @DisplayName("actualizar - debe lanzar excepcion si la disponibilidad a editar no existe")
    void actualizar_DebeLanzarExcepcionSiNoExiste() {
        DisponibilidadDoctor datosNuevos = new DisponibilidadDoctor();
        
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> disponibilidadService.actualizar(999L, datosNuevos))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Disponibilidad no encontrada");

        // Verificamos que NUNCA intentó guardar nada si falló la búsqueda
        verify(repository, never()).save(any(DisponibilidadDoctor.class));
    }

    @Test
    @DisplayName("eliminar - debe buscar la entidad y eliminarla usando repository.delete")
    void eliminar_DebeBuscarYEliminarEntidad() {
        // En tu servicio, buscarPorId se llama primero, así que mockeamos el findById
        when(repository.findById(1L)).thenReturn(Optional.of(disponibilidadTest));
        doNothing().when(repository).delete(disponibilidadTest);

        disponibilidadService.eliminar(1L);

        verify(repository, times(1)).findById(1L);
        // Validamos que se usó delete(entidad) y no deleteById(id) tal como está en tu código
        verify(repository, times(1)).delete(disponibilidadTest); 
    }

    @Test
    @DisplayName("eliminar - debe lanzar excepcion si se intenta eliminar algo que no existe")
    void eliminar_DebeLanzarExcepcionSiNoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> disponibilidadService.eliminar(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Disponibilidad no encontrada");

        verify(repository, never()).delete(any(DisponibilidadDoctor.class));
    }

    @Test
    @DisplayName("buscarPorDoctor - debe retornar la lista de disponibilidades de un doctor especifico")
    void buscarPorDoctor_DebeRetornarListaPorDoctorId() {
        when(repository.findByDoctor_Id(50L)).thenReturn(Arrays.asList(disponibilidadTest));

        List<DisponibilidadDoctor> resultado = disponibilidadService.buscarPorDoctor(50L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(repository, times(1)).findByDoctor_Id(50L);
    }
}
    

