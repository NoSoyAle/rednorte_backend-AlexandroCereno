package com.clinica.autenticacion.service;

import com.clinica.autenticacion.dto.AuthResponse;
import com.clinica.autenticacion.model.Usuario;
import com.clinica.autenticacion.repository.UsuarioRepository;
import com.clinica.autenticacion.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioActivo;
    private Usuario usuarioInactivo;

    @BeforeEach
    void setUp() {
        usuarioActivo = new Usuario();
        usuarioActivo.setId(1L);
        usuarioActivo.setNombre("admin");
        usuarioActivo.setEmail("admin@rednorte.cl");
        usuarioActivo.setPassword("encodedPassword");
        usuarioActivo.setRol("ADMIN");
        usuarioActivo.setTelefono("123456789");
        usuarioActivo.setEstado("activo");

        usuarioInactivo = new Usuario();
        usuarioInactivo.setId(2L);
        usuarioInactivo.setNombre("usuario_inactivo");
        usuarioInactivo.setEmail("inactivo@rednorte.cl");
        usuarioInactivo.setPassword("encodedPassword");
        usuarioInactivo.setRol("ADMIN");
        usuarioInactivo.setTelefono("987654321");
        usuarioInactivo.setEstado("inactivo");
    }

    @Test
    @DisplayName("login - debe retornar token cuando usuario esta activo")
    void login_DebeRetornarTokenCuandoUsuarioActivo() {
        String nombre = "admin";
        String password = "admin123";

        when(usuarioRepository.findFirstByNombre(nombre)).thenReturn(Optional.of(usuarioActivo));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(nombre, "ADMIN")).thenReturn("jwt-token-123");

        AuthResponse response = authService.login(nombre, password);

        assertNotNull(response);
        assertEquals("Login exitoso", response.getMessage());
        assertEquals("jwt-token-123", response.getToken());
        assertEquals("ADMIN", response.getRole());
        assertEquals("admin", response.getNombre());
        verify(jwtUtil, times(1)).generateToken(nombre, "ADMIN");
    }

    @Test
    @DisplayName("login - debe lanzar excepcion cuando usuario esta inactivo")
    void login_DebeLanzarExcepcionCuandoUsuarioInactivo() {
        String nombre = "usuario_inactivo";
        String password = "password123";

        when(usuarioRepository.findFirstByNombre(nombre)).thenReturn(Optional.of(usuarioInactivo));

        assertThatThrownBy(() -> authService.login(nombre, password))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cuenta inactiva");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("login - debe lanzar excepcion cuando usuario no existe")
    void login_DebeLanzarExcepcionCuandoUsuarioNoExiste() {
        String nombre = "no_existe";
        String password = "password123";

        when(usuarioRepository.findFirstByNombre(nombre)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(nombre, password))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario no encontrado");
    }

    @Test
    @DisplayName("login - debe lanzar excepcion cuando password es incorrecto")
    void login_DebeLanzarExcepcionCuandoPasswordIncorrecto() {
        String nombre = "admin";
        String password = "password_incorrecto";

        when(usuarioRepository.findFirstByNombre(nombre)).thenReturn(Optional.of(usuarioActivo));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(nombre, password))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Contrasena incorrecta");

        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("login - debe validar estado case-insensitive (ACTIVO)")
    void login_DebeValidarEstadoCaseInsensitive_Mayusculas() {
        usuarioActivo.setEstado("ACTIVO");
        String nombre = "admin";
        String password = "admin123";

        when(usuarioRepository.findFirstByNombre(nombre)).thenReturn(Optional.of(usuarioActivo));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(nombre, "ADMIN")).thenReturn("jwt-token-123");

        AuthResponse response = authService.login(nombre, password);

        assertNotNull(response);
        assertEquals("Login exitoso", response.getMessage());
    }

    @Test
    @DisplayName("login - debe validar estado case-insensitive (Activo)")
    void login_DebeValidarEstadoCaseInsensitive_Mixto() {
        usuarioActivo.setEstado("Activo");
        String nombre = "admin";
        String password = "admin123";

        when(usuarioRepository.findFirstByNombre(nombre)).thenReturn(Optional.of(usuarioActivo));
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(nombre, "ADMIN")).thenReturn("jwt-token-123");

        AuthResponse response = authService.login(nombre, password);

        assertNotNull(response);
        assertEquals("Login exitoso", response.getMessage());
    }

    @Test
    @DisplayName("login - debe rechazar usuario con estado INACTIVO en mayusculas")
    void login_DebeRechazarUsuarioInactivoMayusculas() {
        usuarioInactivo.setEstado("INACTIVO");
        String nombre = "usuario_inactivo";
        String password = "password123";

        when(usuarioRepository.findFirstByNombre(nombre)).thenReturn(Optional.of(usuarioInactivo));

        assertThatThrownBy(() -> authService.login(nombre, password))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cuenta inactiva");
    }
}
