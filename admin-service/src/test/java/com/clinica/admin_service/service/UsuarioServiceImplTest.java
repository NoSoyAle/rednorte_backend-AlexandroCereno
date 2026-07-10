package com.clinica.admin_service.service;

import com.clinica.admin_service.dto.DashboardKpiDto;
import com.clinica.admin_service.dto.RegistroAdminDto;
import com.clinica.admin_service.dto.RegistroDoctorRequest;
import com.clinica.admin_service.dto.UsuarioEstadoDTO;
import com.clinica.admin_service.model.Rol;
import com.clinica.admin_service.model.Usuario;
import com.clinica.admin_service.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuarioTest;
    private Usuario adminTest;

    @BeforeEach
    void setUp() {
        usuarioTest = new Usuario();
        usuarioTest.setId(1L);
        usuarioTest.setNombre("Juan Perez");
        usuarioTest.setRut("12345678-9");
        usuarioTest.setEmail("juan@test.com");
        usuarioTest.setPassword("password123");
        usuarioTest.setTelefono("912345678");
        usuarioTest.setRol(Rol.DOCTOR);
        usuarioTest.setEstado("ACTIVO");

        adminTest = new Usuario();
        adminTest.setId(2L);
        adminTest.setNombre("Admin Principal");
        adminTest.setRut("11111111-1");
        adminTest.setEmail("admin@test.com");
        adminTest.setPassword("admin123");
        adminTest.setTelefono("911111111");
        adminTest.setRol(Rol.ADMIN);
        adminTest.setEstado("ACTIVO");
    }

    @Test
    @DisplayName("guardarUsuario - debe guardar usuario con password encriptado")
    void guardarUsuario_DebeGuardarConPasswordEncriptado() {
        String rawPassword = "password123";
        usuarioTest.setPassword(rawPassword);
        
        when(passwordEncoder.encode(rawPassword)).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTest);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("OK"));

        Usuario resultado = usuarioService.guardarUsuario(usuarioTest);

        assertNotNull(resultado);
        assertEquals("encodedPassword", resultado.getPassword());
        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(usuarioRepository, times(1)).save(usuarioTest);
    }

    @Test
    @DisplayName("guardarUsuario - debe sincronizar con servicio de autenticacion")
    void guardarUsuario_DebeSincronizarConAutenticacion() {
        usuarioTest.setPassword("rawPassword");
        
        when(passwordEncoder.encode("rawPassword")).thenReturn("encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTest);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("OK"));

        usuarioService.guardarUsuario(usuarioTest);

        verify(restTemplate, times(1)).postForEntity(
                eq("http://localhost:8081/auth/register"),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    @DisplayName("guardarUsuario - no debe fallar si sincronizacion con autenticacion falla")
    void guardarUsuario_NoDebeFallarSiSincronizacionFalla() {
        usuarioTest.setPassword("rawPassword");
        
        when(passwordEncoder.encode("rawPassword")).thenReturn("encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTest);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        Usuario resultado = usuarioService.guardarUsuario(usuarioTest);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).save(usuarioTest);
    }

    @Test
    @DisplayName("registrarDoctor - debe registrar doctor exitosamente")
    void registrarDoctor_DebeRegistrarExitosamente() {
        RegistroDoctorRequest request = new RegistroDoctorRequest(
                "Dr. Test", "98765432-1", "dr@test.com", "pass123", "987654321"
        );

        when(usuarioRepository.findByNombre("Dr. Test")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("dr@test.com")).thenReturn(Optional.empty());
        when(usuarioRepository.findByRut("98765432-1")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(3L);
            return u;
        });
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("OK"));

        Usuario resultado = usuarioService.registrarDoctor(request);

        assertNotNull(resultado);
        assertEquals("Dr. Test", resultado.getNombre());
        assertEquals(Rol.DOCTOR, resultado.getRol());
        assertEquals("ACTIVO", resultado.getEstado());
        assertEquals("encodedPass", resultado.getPassword());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("registrarDoctor - debe lanzar excepcion si nombre ya existe")
    void registrarDoctor_DebeLanzarExcepcionSiNombreExiste() {
        RegistroDoctorRequest request = new RegistroDoctorRequest(
                "Dr. Existente", "98765432-1", "dr@test.com", "pass123", "987654321"
        );

        when(usuarioRepository.findByNombre("Dr. Existente")).thenReturn(Optional.of(usuarioTest));

        assertThatThrownBy(() -> usuarioService.registrarDoctor(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un usuario con el nombre");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("registrarDoctor - debe lanzar excepcion si email ya existe")
    void registrarDoctor_DebeLanzarExcepcionSiEmailExiste() {
        RegistroDoctorRequest request = new RegistroDoctorRequest(
                "Dr. Nuevo", "98765432-1", "email@existente.com", "pass123", "987654321"
        );

        when(usuarioRepository.findByNombre("Dr. Nuevo")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("email@existente.com")).thenReturn(Optional.of(usuarioTest));

        assertThatThrownBy(() -> usuarioService.registrarDoctor(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un usuario con el email");
    }

    @Test
    @DisplayName("registrarDoctor - debe lanzar excepcion si RUT ya existe")
    void registrarDoctor_DebeLanzarExcepcionSiRutExiste() {
        RegistroDoctorRequest request = new RegistroDoctorRequest(
                "Dr. Nuevo", "rut-existente", "dr@nuevo.com", "pass123", "987654321"
        );

        when(usuarioRepository.findByNombre("Dr. Nuevo")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("dr@nuevo.com")).thenReturn(Optional.empty());
        when(usuarioRepository.findByRut("rut-existente")).thenReturn(Optional.of(usuarioTest));

        assertThatThrownBy(() -> usuarioService.registrarDoctor(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ya existe un usuario con el RUT");
    }

    @Test
    @DisplayName("registrarDoctor - debe sincronizar con agenda despues de guardar")
    void registrarDoctor_DebeSincronizarConAgenda() {
        RegistroDoctorRequest request = new RegistroDoctorRequest(
                "Dr. Agenda", "12345678-9", "dr@agenda.com", "pass123", "912345678"
        );

        when(usuarioRepository.findByNombre(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByRut(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTest);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("OK"));

        usuarioService.registrarDoctor(request);

        verify(restTemplate, times(1)).postForEntity(
                eq("http://localhost:8085/api/doctor"),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    @DisplayName("listarUsuarios - debe retornar todos los usuarios")
    void listarUsuarios_DebeRetornarTodos() {
        List<Usuario> usuarios = Arrays.asList(usuarioTest, adminTest);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        List<Usuario> resultado = usuarioService.listarUsuarios();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("listarUsuarios - debe retornar lista vacia si no hay usuarios")
    void listarUsuarios_DebeRetornarListaVacia() {
        when(usuarioRepository.findAll()).thenReturn(new ArrayList<>());

        List<Usuario> resultado = usuarioService.listarUsuarios();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("buscarPorRut - debe retornar usuario si existe")
    void buscarPorRut_DebeRetornarUsuarioSiExiste() {
        when(usuarioRepository.findByRut("12345678-9")).thenReturn(Optional.of(usuarioTest));

        Usuario resultado = usuarioService.buscarPorRut("12345678-9");

        assertNotNull(resultado);
        assertEquals("12345678-9", resultado.getRut());
        assertEquals("Juan Perez", resultado.getNombre());
    }

    @Test
    @DisplayName("buscarPorRut - debe lanzar excepcion si usuario no existe")
    void buscarPorRut_DebeLanzarExcepcionSiNoExiste() {
        when(usuarioRepository.findByRut("no-existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.buscarPorRut("no-existe"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado con RUT");
    }

    @Test
    @DisplayName("eliminarUsuario - debe eliminar si usuario existe")
    void eliminarUsuario_DebeEliminarSiExiste() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1L);

        usuarioService.eliminarUsuario(1L);

        verify(usuarioRepository, times(1)).existsById(1L);
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("eliminarUsuario - debe lanzar excepcion si usuario no existe")
    void eliminarUsuario_DebeLanzarExcepcionSiNoExiste() {
        when(usuarioRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.eliminarUsuario(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no existe");

        verify(usuarioRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("obtenerMetricasDashboard - debe retornar metricas correctas")
    void obtenerMetricasDashboard_DebeRetornarMetricas() {
        List<Usuario> usuarios = Arrays.asList(usuarioTest, adminTest);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        DashboardKpiDto resultado = usuarioService.obtenerMetricasDashboard();

        assertNotNull(resultado);
        assertEquals(2, resultado.getTotalUsuarios());
        assertEquals(1, resultado.getTotalAdministradores());
        assertEquals(1, resultado.getTotalDoctores());
        assertThat(resultado.getUsuariosPorRol()).containsEntry("ADMINISTRADORES", 1L);
        assertThat(resultado.getUsuariosPorRol()).containsEntry("DOCTORES", 1L);
    }

    @Test
    @DisplayName("obtenerMetricasDashboard - debe retornar ceros si no hay usuarios")
    void obtenerMetricasDashboard_DebeRetornarCerosSiNoHayUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(new ArrayList<>());

        DashboardKpiDto resultado = usuarioService.obtenerMetricasDashboard();

        assertNotNull(resultado);
        assertEquals(0, resultado.getTotalUsuarios());
        assertEquals(0, resultado.getTotalAdministradores());
        assertEquals(0, resultado.getTotalDoctores());
    }

    @Test
    @DisplayName("actualizarEstadoUsuario - debe actualizar estado correctamente")
    void actualizarEstadoUsuario_DebeActualizarEstado() {
        UsuarioEstadoDTO dto = new UsuarioEstadoDTO();
        dto.setEstado("INACTIVO");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTest);

        Usuario resultado = usuarioService.actualizarEstadoUsuario(1L, dto);

        assertNotNull(resultado);
        assertEquals("INACTIVO", resultado.getEstado());
        verify(usuarioRepository, times(1)).save(usuarioTest);
    }

    @Test
    @DisplayName("actualizarEstadoUsuario - debe lanzar excepcion si usuario no existe")
    void actualizarEstadoUsuario_DebeLanzarExcepcionSiNoExiste() {
        UsuarioEstadoDTO dto = new UsuarioEstadoDTO();
        dto.setEstado("INACTIVO");

        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.actualizarEstadoUsuario(999L, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    @Test
    @DisplayName("obtenerRegistroAdmin - debe retornar citas canceladas y confirmadas")
    void obtenerRegistroAdmin_DebeRetornarDatos() {
        List<Map<String, Object>> citasCanceladas = new ArrayList<>();
        Map<String, Object> cita1 = new HashMap<>();
        cita1.put("id", 1L);
        cita1.put("estado", "CANCELADA");
        cita1.put("doctorId", 1L);
        cita1.put("pacienteId", 1L);
        cita1.put("especialidadId", 1L);
        cita1.put("motivoCancelacion", "Paciente no pudo asistir");
        citasCanceladas.add(cita1);

        List<Map<String, Object>> citasConfirmadas = new ArrayList<>();
        Map<String, Object> cita2 = new HashMap<>();
        cita2.put("id", 2L);
        cita2.put("estado", "CONFIRMADA");
        cita2.put("doctorId", 1L);
        cita2.put("pacienteId", 2L);
        cita2.put("especialidadId", 1L);
        cita2.put("confirmada", true);
        citasConfirmadas.add(cita2);

        List<Map<String, Object>> especialidades = new ArrayList<>();
        Map<String, Object> esp = new HashMap<>();
        esp.put("idEspecialidad", 1L);
        esp.put("nombreEsp", "Cardiologia");
        especialidades.add(esp);

        when(restTemplate.exchange(
                eq("http://localhost:8086/api/cita/estado/CANCELADA"),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(citasCanceladas));

        when(restTemplate.exchange(
                eq("http://localhost:8086/api/cita/confirmadas"),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(citasConfirmadas));

        when(restTemplate.exchange(
                eq("http://localhost:8085/api/especialidad"),
                eq(HttpMethod.GET),
                any(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(especialidades));

        RegistroAdminDto resultado = usuarioService.obtenerRegistroAdmin();

        assertNotNull(resultado);
        assertThat(resultado.getCitasCanceladas()).hasSize(1);
        assertThat(resultado.getCitasConfirmadas()).hasSize(1);
        assertThat(resultado.getEspecialidadesMayorConfirmacion()).isNotEmpty();
    }

    @Test
    @DisplayName("obtenerRegistroAdmin - debe manejar errores de servicios externos")
    void obtenerRegistroAdmin_DebeManejarErrores() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        RegistroAdminDto resultado = usuarioService.obtenerRegistroAdmin();

        assertNotNull(resultado);
        assertThat(resultado.getCitasCanceladas()).isEmpty();
        assertThat(resultado.getCitasConfirmadas()).isEmpty();
        assertThat(resultado.getEspecialidadesMayorConfirmacion()).isEmpty();
    }
}
