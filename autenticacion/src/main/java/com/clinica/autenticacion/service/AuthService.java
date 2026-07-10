package com.clinica.autenticacion.service;

import com.clinica.autenticacion.dto.AuthResponse;
import com.clinica.autenticacion.model.Usuario;
import com.clinica.autenticacion.repository.UsuarioRepository;
import com.clinica.autenticacion.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse login(String nombre, String password) {
        Usuario usuario = usuarioRepository.findFirstByNombre(nombre)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + nombre));

        if (!"activo".equalsIgnoreCase(usuario.getEstado())) {
            throw new RuntimeException("Cuenta inactiva. Contacte al administrador.");
        }

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Contrasena incorrecta para usuario: " + nombre);
        }

        String token = jwtUtil.generateToken(usuario.getNombre(), usuario.getRol());
        
        return new AuthResponse("Login exitoso", token, usuario.getRol(), usuario.getNombre());
    }

    public Usuario register(Map<String, String> data) {
        Usuario usuario = new Usuario();
        usuario.setNombre(data.get("nombre"));
        usuario.setEmail(data.get("email"));
        usuario.setPassword(passwordEncoder.encode(data.get("password")));
        usuario.setRol(data.getOrDefault("rol", "ADMIN"));
        usuario.setTelefono(data.getOrDefault("telefono", ""));
        usuario.setEstado(data.getOrDefault("estado", "activo"));
        return usuarioRepository.save(usuario);
    }
}
