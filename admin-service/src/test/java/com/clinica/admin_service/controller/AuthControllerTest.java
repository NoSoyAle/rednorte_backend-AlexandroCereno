package com.clinica.admin_service.controller;

import com.clinica.admin_service.model.Rol;
import com.clinica.admin_service.model.Usuario;
import com.clinica.admin_service.repository.UsuarioRepository;
import com.clinica.admin_service.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private Usuario usuarioActivo;
    private Usuario usuarioInactivo;

    @BeforeEach
    void setUp() {
        usuarioActivo = new Usuario();
        usuarioActivo.setId(1L);
        usuarioActivo.setNombre("Admin");
        usuarioActivo.setRut("11111111-1");
        usuarioActivo.setEmail("admin@rednorte.cl");
        usuarioActivo.setPassword("encodedPassword");
        usuarioActivo.setTelefono("911111111");
        usuarioActivo.setRol(Rol.ADMIN);
        usuarioActivo.setEstado("ACTIVO");

        usuarioInactivo = new Usuario();
        usuarioInactivo.setId(2L);
        usuarioInactivo.setNombre("Inactivo");
        usuarioInactivo.setRut("22222222-2");
        usuarioInactivo.setEmail("inactivo@rednorte.cl");
        usuarioInactivo.setPassword("encodedPassword");
        usuarioInactivo.setTelefono("922222222");
        usuarioInactivo.setRol(Rol.ADMIN);
        usuarioInactivo.setEstado("INACTIVO");
    }

    @Test
    @DisplayName("login - debe retornar token cuando usuario esta ACTIVO")
    void login_DebeRetornarTokenCuandoUsuarioActivo() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "admin@rednorte.cl");
        credentials.put("password", "admin123");

        when(usuarioRepository.findByEmail("admin@rednorte.cl")).thenReturn(Optional.of(usuarioActivo));
        when(passwordEncoder.matches("admin123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("admin@rednorte.cl", "ROLE_ADMIN")).thenReturn("jwt-token-123");

        ResponseEntity<?> response = authController.login(credentials);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertThat((Map<String, String>) response.getBody()).containsKey("token");
        assertThat((Map<String, String>) response.getBody()).containsEntry("role", "ADMIN");
        verify(jwtUtil, times(1)).generateToken("admin@rednorte.cl", "ROLE_ADMIN");
    }

    @Test
    @DisplayName("login - debe retornar 401 cuando usuario esta INACTIVO")
    void login_DebeRetornar401CuandoUsuarioInactivo() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "inactivo@rednorte.cl");
        credentials.put("password", "password123");

        when(usuarioRepository.findByEmail("inactivo@rednorte.cl")).thenReturn(Optional.of(usuarioInactivo));

        ResponseEntity<?> response = authController.login(credentials);

        assertEquals(401, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertThat((Map<String, String>) response.getBody()).containsEntry("error", "Cuenta inactiva. Contacte al administrador.");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("login - debe retornar 401 cuando usuario no existe")
    void login_DebeRetornar401CuandoUsuarioNoExiste() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "no_existe@rednorte.cl");
        credentials.put("password", "password123");

        when(usuarioRepository.findByEmail("no_existe@rednorte.cl")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.login(credentials);

        assertEquals(401, response.getStatusCode().value());
        assertThat((Map<String, String>) response.getBody()).containsEntry("error", "Credenciales inválidas");
    }

    @Test
    @DisplayName("login - debe retornar 401 cuando password es incorrecto")
    void login_DebeRetornar401CuandoPasswordIncorrecto() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "admin@rednorte.cl");
        credentials.put("password", "password_incorrecto");

        when(usuarioRepository.findByEmail("admin@rednorte.cl")).thenReturn(Optional.of(usuarioActivo));
        when(passwordEncoder.matches("password_incorrecto", "encodedPassword")).thenReturn(false);

        ResponseEntity<?> response = authController.login(credentials);

        assertEquals(401, response.getStatusCode().value());
        assertThat((Map<String, String>) response.getBody()).containsEntry("error", "Credenciales inválidas");
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("login - debe validar estado case-insensitive (activo)")
    void login_DebeValidarEstadoCaseInsensitive_Minusculas() {
        usuarioActivo.setEstado("activo");
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "admin@rednorte.cl");
        credentials.put("password", "admin123");

        when(usuarioRepository.findByEmail("admin@rednorte.cl")).thenReturn(Optional.of(usuarioActivo));
        when(passwordEncoder.matches("admin123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("admin@rednorte.cl", "ROLE_ADMIN")).thenReturn("jwt-token-123");

        ResponseEntity<?> response = authController.login(credentials);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("login - debe rechazar usuario con estado inactivo en minusculas")
    void login_DebeRechazarUsuarioInactivoMinusculas() {
        usuarioInactivo.setEstado("inactivo");
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "inactivo@rednorte.cl");
        credentials.put("password", "password123");

        when(usuarioRepository.findByEmail("inactivo@rednorte.cl")).thenReturn(Optional.of(usuarioInactivo));

        ResponseEntity<?> response = authController.login(credentials);

        assertEquals(401, response.getStatusCode().value());
        assertThat((Map<String, String>) response.getBody()).containsEntry("error", "Cuenta inactiva. Contacte al administrador.");
    }
}
