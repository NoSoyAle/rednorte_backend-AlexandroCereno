package com.clinica.admin_service.filter;

import com.clinica.admin_service.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        
        logger.info("=== JWT Filter ===");
        logger.info("Authorization header: " + authorizationHeader);

        String username = null;
        String jwtToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            logger.info("Token extraido: " + jwtToken.substring(0, Math.min(20, jwtToken.length())) + "...");
            try {
                username = jwtUtil.extractUsername(jwtToken);
                logger.info("Username extraido del token: " + username);
            } catch (Exception e) {
                logger.error("Token JWT invalido o expirado: " + e.getMessage());
            }
        } else {
            logger.warn("No se encontro header Authorization con Bearer");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("Validando token para usuario: " + username);
            boolean isValid = jwtUtil.validateToken(jwtToken);
            logger.info("Token valido: " + isValid);
            
            if (isValid) {
                String role = jwtUtil.extractRole(jwtToken);
                logger.info("Rol extraido del token: " + role);
                
                if (!role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                    logger.info("Rol ajustado a: " + role);
                }
                
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.singletonList(new SimpleGrantedAuthority(role)));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("Autenticacion establecida en SecurityContext con autoridad: " + role);
            } else {
                logger.error("Token no paso la validacion");
            }
        } else if (username == null) {
            logger.warn("Username es null, no se establecera autenticacion");
        } else {
            logger.info("Ya existe autenticacion en SecurityContext");
        }

        chain.doFilter(request, response);
    }
}
