package com.clinica.agenda.service;
import com.clinica.agenda.entities.Doctor;
import com.clinica.agenda.entities.Especialidad;
import com.clinica.agenda.repository.DoctorRepository;
import com.clinica.agenda.repository.EspecialidadRepository;
import com.clinica.agenda.services.DoctorServicesImplement;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class DoctorServiceImplementTests {
    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private EspecialidadRepository especialidadRepository;

    @InjectMocks
    private DoctorServicesImplement doctorService;

    private Doctor doctorTest;
    private Especialidad especialidadTest;

    @BeforeEach
    void setUp() {
        especialidadTest = new Especialidad();
        especialidadTest.setId(1L);
        especialidadTest.setNombreEsp("Cardiología");

        doctorTest = new Doctor();
        doctorTest.setId(1L);
        doctorTest.setNombre("Dr. House");
        doctorTest.setEspecialidades(new ArrayList<>(Arrays.asList(especialidadTest)));
    }

    @Test
    @DisplayName("crearDoctor - debe asignar especialidades existentes y guardar")
    void crearDoctor_DebeAsignarEspecialidadesYGuardar() {
        when(especialidadRepository.findById(1L)).thenReturn(Optional.of(especialidadTest));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctorTest);

        Doctor resultado = doctorService.crearDoctor(doctorTest);

        assertThat(resultado.getEspecialidades()).hasSize(1);
        verify(doctorRepository, times(1)).save(doctorTest);
    }

    @Test
    @DisplayName("actualizarDoctor - debe actualizar datos y especialidades")
    void actualizarDoctor_DebeActualizarCamposYEspecialidades() {
        Doctor nuevoData = new Doctor();
        nuevoData.setNombre("Dr. Wilson");
        nuevoData.setEspecialidades(Arrays.asList(especialidadTest));

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctorTest));
        when(especialidadRepository.findById(1L)).thenReturn(Optional.of(especialidadTest));
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(i -> i.getArgument(0));

        Doctor resultado = doctorService.actualizarDoctor(1L, nuevoData);

        assertThat(resultado.getNombre()).isEqualTo("Dr. Wilson");
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    @DisplayName("buscarPorRut - debe retornar doctor si existe")
    void buscarPorRut_DebeRetornarDoctor() {
        when(doctorRepository.findByRut("123-K")).thenReturn(Optional.of(doctorTest));

        Doctor resultado = doctorService.buscarPorRut("123-K");

        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("obtenerEspecialidadesDoctor - debe lanzar excepcion si doctor no existe")
    void obtenerEspecialidadesDoctor_DebeLanzarExcepcionSiDoctorNoExiste() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.obtenerEspecialidadesDoctor(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Doctor no encontrado");
    }
}
    

