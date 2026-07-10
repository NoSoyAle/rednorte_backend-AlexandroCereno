package com.paciente.paciente.service;

import com.paciente.paciente.Model.Paciente;
import com.paciente.paciente.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PacienteServiceImplTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteServiceImpl pacienteService;

    private Paciente pacienteTest;
    private Paciente pacienteTest2;

    @BeforeEach
    void setUp() {
        pacienteTest = new Paciente();
        pacienteTest.setId(1L);
        pacienteTest.setRut("12345678-9");
        pacienteTest.setNombre("Juan");
        pacienteTest.setApellido("Perez");
        pacienteTest.setEmail("juan@test.com");
        pacienteTest.setTelefono("912345678");
        pacienteTest.setFechaNacimiento(LocalDate.of(1990, 5, 15));
        pacienteTest.setDireccion("Calle Falsa 123");

        pacienteTest2 = new Paciente();
        pacienteTest2.setId(2L);
        pacienteTest2.setRut("98765432-1");
        pacienteTest2.setNombre("Maria");
        pacienteTest2.setApellido("Gonzalez");
        pacienteTest2.setEmail("maria@test.com");
        pacienteTest2.setTelefono("987654321");
        pacienteTest2.setFechaNacimiento(LocalDate.of(1985, 10, 20));
        pacienteTest2.setDireccion("Avenida Siempre 456");
    }

    @Test
    @DisplayName("findAll - debe retornar todos los pacientes")
    void findAll_DebeRetornarTodosLosPacientes() {
        List<Paciente> pacientes = Arrays.asList(pacienteTest, pacienteTest2);
        when(pacienteRepository.findAll()).thenReturn(pacientes);

        List<Paciente> resultado = pacienteService.findAll();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertThat(resultado).containsExactly(pacienteTest, pacienteTest2);
        verify(pacienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll - debe retornar lista vacia si no hay pacientes")
    void findAll_DebeRetornarListaVacia() {
        when(pacienteRepository.findAll()).thenReturn(new ArrayList<>());

        List<Paciente> resultado = pacienteService.findAll();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(pacienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById - debe retornar paciente si existe")
    void findById_DebeRetornarPacienteSiExiste() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteTest));

        Paciente resultado = pacienteService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Juan", resultado.getNombre());
        assertEquals("Perez", resultado.getApellido());
        assertEquals("12345678-9", resultado.getRut());
        verify(pacienteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById - debe retornar null si paciente no existe")
    void findById_DebeRetornarNullSiNoExiste() {
        when(pacienteRepository.findById(999L)).thenReturn(Optional.empty());

        Paciente resultado = pacienteService.findById(999L);

        assertNull(resultado);
        verify(pacienteRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("save - debe guardar y retornar paciente")
    void save_DebeGuardarYRetornarPaciente() {
        when(pacienteRepository.save(pacienteTest)).thenReturn(pacienteTest);

        Paciente resultado = pacienteService.save(pacienteTest);

        assertNotNull(resultado);
        assertEquals(pacienteTest.getId(), resultado.getId());
        assertEquals(pacienteTest.getNombre(), resultado.getNombre());
        verify(pacienteRepository, times(1)).save(pacienteTest);
    }

    @Test
    @DisplayName("save - debe guardar paciente con todos los campos")
    void save_DebeGuardarPacienteConTodosLosCampos() {
        Paciente nuevoPaciente = new Paciente();
        nuevoPaciente.setRut("11111111-1");
        nuevoPaciente.setNombre("Pedro");
        nuevoPaciente.setApellido("Soto");
        nuevoPaciente.setEmail("pedro@test.com");
        nuevoPaciente.setTelefono("911111111");
        nuevoPaciente.setFechaNacimiento(LocalDate.of(1995, 3, 25));
        nuevoPaciente.setDireccion("Calle Nueva 789");

        when(pacienteRepository.save(nuevoPaciente)).thenAnswer(invocation -> {
            Paciente p = invocation.getArgument(0);
            p.setId(3L);
            return p;
        });

        Paciente resultado = pacienteService.save(nuevoPaciente);

        assertNotNull(resultado);
        assertEquals(3L, resultado.getId());
        assertEquals("Pedro", resultado.getNombre());
        assertEquals("Soto", resultado.getApellido());
        assertEquals("pedro@test.com", resultado.getEmail());
        assertThat(resultado.getFechaNacimiento()).isEqualTo(LocalDate.of(1995, 3, 25));
    }

    @Test
    @DisplayName("deletebyId - debe eliminar paciente por id")
    void deletebyId_DebeEliminarPaciente() {
        doNothing().when(pacienteRepository).deleteById(1L);

        pacienteService.deletebyId(1L);

        verify(pacienteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deletebyId - debe llamar a deleteById con el id correcto")
    void deletebyId_DebeLlamarDeleteByIdConIdCorrecto() {
        Long idAEliminar = 5L;
        doNothing().when(pacienteRepository).deleteById(idAEliminar);

        pacienteService.deletebyId(idAEliminar);

        verify(pacienteRepository, times(1)).deleteById(idAEliminar);
    }

    @Test
    @DisplayName("findAll - debe retornar pacientes con datos completos")
    void findAll_DebeRetornarPacientesConDatosCompletos() {
        List<Paciente> pacientes = Arrays.asList(pacienteTest);
        when(pacienteRepository.findAll()).thenReturn(pacientes);

        List<Paciente> resultado = pacienteService.findAll();

        assertThat(resultado).hasSize(1);
        Paciente p = resultado.get(0);
        assertThat(p.getRut()).isEqualTo("12345678-9");
        assertThat(p.getNombre()).isEqualTo("Juan");
        assertThat(p.getApellido()).isEqualTo("Perez");
        assertThat(p.getEmail()).isEqualTo("juan@test.com");
        assertThat(p.getTelefono()).isEqualTo("912345678");
        assertThat(p.getFechaNacimiento()).isEqualTo(LocalDate.of(1990, 5, 15));
        assertThat(p.getDireccion()).isEqualTo("Calle Falsa 123");
    }
}
