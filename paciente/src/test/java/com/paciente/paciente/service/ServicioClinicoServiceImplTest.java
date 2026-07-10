package com.paciente.paciente.service;

import com.paciente.paciente.Model.ServicioClinico;
import com.paciente.paciente.repository.ServicioClinicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicioClinicoServiceImplTest {

    @Mock
    private ServicioClinicoRepository servicioClinicoRepository;

    @InjectMocks
    private ServicioClinicoServiceImpl servicioClinicoService;

    private ServicioClinico servicioTest;
    private ServicioClinico servicioTest2;

    @BeforeEach
    void setUp() {
        servicioTest = new ServicioClinico();
        servicioTest.setId(1L);
        servicioTest.setNombre("Consulta General");
        servicioTest.setPrecioBase(15000.0);
        servicioTest.setCategoria("CONSULTA");
        servicioTest.setModalidad("PRESENCIAL");
        servicioTest.setDisponible(true);

        servicioTest2 = new ServicioClinico();
        servicioTest2.setId(2L);
        servicioTest2.setNombre("Examen de Sangre");
        servicioTest2.setPrecioBase(25000.0);
        servicioTest2.setCategoria("EXAMEN");
        servicioTest2.setModalidad("PRESENCIAL");
        servicioTest2.setDisponible(true);
    }

    @Test
    @DisplayName("findAll - debe retornar todos los servicios clinicos")
    void findAll_DebeRetornarTodosLosServicios() {
        List<ServicioClinico> servicios = Arrays.asList(servicioTest, servicioTest2);
        when(servicioClinicoRepository.findAll()).thenReturn(servicios);

        List<ServicioClinico> resultado = servicioClinicoService.findAll();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertThat(resultado).containsExactly(servicioTest, servicioTest2);
        verify(servicioClinicoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll - debe retornar lista vacia si no hay servicios")
    void findAll_DebeRetornarListaVacia() {
        when(servicioClinicoRepository.findAll()).thenReturn(new ArrayList<>());

        List<ServicioClinico> resultado = servicioClinicoService.findAll();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("findById - debe retornar servicio si existe")
    void findById_DebeRetornarServicioSiExiste() {
        when(servicioClinicoRepository.findById(1L)).thenReturn(Optional.of(servicioTest));

        ServicioClinico resultado = servicioClinicoService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Consulta General", resultado.getNombre());
        assertEquals("CONSULTA", resultado.getCategoria());
        verify(servicioClinicoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById - debe retornar null si servicio no existe")
    void findById_DebeRetornarNullSiNoExiste() {
        when(servicioClinicoRepository.findById(999L)).thenReturn(Optional.empty());

        ServicioClinico resultado = servicioClinicoService.findById(999L);

        assertNull(resultado);
        verify(servicioClinicoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("save - debe guardar y retornar servicio")
    void save_DebeGuardarYRetornarServicio() {
        when(servicioClinicoRepository.save(servicioTest)).thenReturn(servicioTest);

        ServicioClinico resultado = servicioClinicoService.save(servicioTest);

        assertNotNull(resultado);
        assertEquals(servicioTest.getId(), resultado.getId());
        assertEquals("Consulta General", resultado.getNombre());
        verify(servicioClinicoRepository, times(1)).save(servicioTest);
    }

    @Test
    @DisplayName("save - debe guardar servicio con todos los campos")
    void save_DebeGuardarServicioConTodosLosCampos() {
        ServicioClinico nuevoServicio = new ServicioClinico();
        nuevoServicio.setNombre("Telemedicina");
        nuevoServicio.setPrecioBase(20000.0);
        nuevoServicio.setCategoria("CONSULTA");
        nuevoServicio.setModalidad("TELEMEDICINA");
        nuevoServicio.setDisponible(true);

        when(servicioClinicoRepository.save(nuevoServicio)).thenAnswer(invocation -> {
            ServicioClinico s = invocation.getArgument(0);
            s.setId(3L);
            return s;
        });

        ServicioClinico resultado = servicioClinicoService.save(nuevoServicio);

        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        assertEquals("Telemedicina", resultado.getNombre());
        assertEquals(20000.0, resultado.getPrecioBase());
        assertEquals("TELEMEDICINA", resultado.getModalidad());
    }

    @Test
    @DisplayName("update - debe actualizar servicio existente")
    void update_DebeActualizarServicioExistente() {
        ServicioClinico datosActualizados = new ServicioClinico();
        datosActualizados.setNombre("Consulta Especializada");
        datosActualizados.setPrecioBase(30000.0);
        datosActualizados.setCategoria("CONSULTA");
        datosActualizados.setModalidad("PRESENCIAL");
        datosActualizados.setDisponible(false);

        when(servicioClinicoRepository.findById(1L)).thenReturn(Optional.of(servicioTest));
        when(servicioClinicoRepository.save(any(ServicioClinico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServicioClinico resultado = servicioClinicoService.update(1L, datosActualizados);

        assertNotNull(resultado);
        assertEquals("Consulta Especializada", resultado.getNombre());
        assertEquals(30000.0, resultado.getPrecioBase());
        assertEquals(false, resultado.isDisponible());
        verify(servicioClinicoRepository, times(1)).findById(1L);
        verify(servicioClinicoRepository, times(1)).save(any(ServicioClinico.class));
    }

    @Test
    @DisplayName("update - debe lanzar excepcion si servicio no existe")
    void update_DebeLanzarExcepcionSiNoExiste() {
        ServicioClinico datosActualizados = new ServicioClinico();
        datosActualizados.setNombre("Nuevo Nombre");

        when(servicioClinicoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicioClinicoService.update(999L, datosActualizados))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Servicio clinico no encontrado con id");

        verify(servicioClinicoRepository, never()).save(any(ServicioClinico.class));
    }

    @Test
    @DisplayName("update - debe preservar id del servicio original")
    void update_DebePreservarIdDelServicioOriginal() {
        ServicioClinico datosActualizados = new ServicioClinico();
        datosActualizados.setNombre("Nombre Actualizado");
        datosActualizados.setPrecioBase(10000.0);
        datosActualizados.setCategoria("EXAMEN");
        datosActualizados.setModalidad("TELEMEDICINA");
        datosActualizados.setDisponible(true);

        when(servicioClinicoRepository.findById(1L)).thenReturn(Optional.of(servicioTest));
        when(servicioClinicoRepository.save(any(ServicioClinico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServicioClinico resultado = servicioClinicoService.update(1L, datosActualizados);

        assertEquals(1L, resultado.getId());
        assertEquals("Nombre Actualizado", resultado.getNombre());
    }

    @Test
    @DisplayName("deleteById - debe eliminar servicio si existe")
    void deleteById_DebeEliminarServicioSiExiste() {
        when(servicioClinicoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(servicioClinicoRepository).deleteById(1L);

        servicioClinicoService.deleteById(1L);

        verify(servicioClinicoRepository, times(1)).existsById(1L);
        verify(servicioClinicoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById - debe lanzar excepcion si servicio no existe")
    void deleteById_DebeLanzarExcepcionSiNoExiste() {
        when(servicioClinicoRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> servicioClinicoService.deleteById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Servicio clinico no encontrado con id");

        verify(servicioClinicoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("findAll - debe retornar servicios con datos completos")
    void findAll_DebeRetornarServiciosConDatosCompletos() {
        List<ServicioClinico> servicios = Arrays.asList(servicioTest);
        when(servicioClinicoRepository.findAll()).thenReturn(servicios);

        List<ServicioClinico> resultado = servicioClinicoService.findAll();

        assertThat(resultado).hasSize(1);
        ServicioClinico s = resultado.get(0);
        assertThat(s.getId()).isEqualTo(1L);
        assertThat(s.getNombre()).isEqualTo("Consulta General");
        assertThat(s.getPrecioBase()).isEqualTo(15000.0);
        assertThat(s.getCategoria()).isEqualTo("CONSULTA");
        assertThat(s.getModalidad()).isEqualTo("PRESENCIAL");
        assertThat(s.isDisponible()).isTrue();
    }
}
