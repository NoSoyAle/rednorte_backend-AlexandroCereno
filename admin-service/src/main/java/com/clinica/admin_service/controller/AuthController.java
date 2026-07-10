package com.clinica.admin_service.controller;

import com.clinica.admin_service.model.Rol;
import com.clinica.admin_service.model.Usuario;
import com.clinica.admin_service.repository.UsuarioRepository;
import com.clinica.admin_service.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        logger.info("=== Login attempt ===");
        logger.info("Email: " + email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElse(null);

        if (usuario == null) {
            logger.warn("Usuario no encontrado con email: " + email);
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
        
        logger.info("Usuario encontrado: " + usuario.getNombre() + ", Rol: " + usuario.getRol().name());

        if (!"ACTIVO".equalsIgnoreCase(usuario.getEstado())) {
            logger.warn("Cuenta inactiva para: " + email);
            return ResponseEntity.status(401).body(Map.of("error", "Cuenta inactiva. Contacte al administrador."));
        }

        boolean passwordMatch = passwordEncoder.matches(password, usuario.getPassword());
        logger.info("Password match: " + passwordMatch);
        
        if (!passwordMatch) {
            logger.warn("Password incorrecto para: " + email);
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }

        String roleWithPrefix = "ROLE_" + usuario.getRol().name();
        logger.info("Generando token con rol: " + roleWithPrefix);
        
        String token = jwtUtil.generateToken(usuario.getEmail(), roleWithPrefix);
        logger.info("Token generado exitosamente, longitud: " + token.length());
        
        return ResponseEntity.ok(Map.of("token", token, "role", usuario.getRol().name()));
    }

    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody Map<String, String> data) {
        String email = data.get("email");
        String password = data.get("password");
        String nombre = data.getOrDefault("nombre", "Admin");
        String rut = data.getOrDefault("rut", "11111111-1");

        if (usuarioRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email ya registrado"));
        }

        Usuario admin = new Usuario();
        admin.setNombre(nombre);
        admin.setRut(rut);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setTelefono("911111111");
        admin.setRol(Rol.ADMIN);
        admin.setEstado("activo");

        usuarioRepository.save(admin);
        String token = jwtUtil.generateToken(admin.getEmail(), "ROLE_ADMIN");
        return ResponseEntity.ok(Map.of("token", token, "role", "ADMIN"));
    }
}
