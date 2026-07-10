package com.clinica.admin_service.service;

import com.clinica.admin_service.dto.DashboardKpiDto;
import com.clinica.admin_service.dto.RegistroAdminDto;
import com.clinica.admin_service.dto.RegistroDoctorRequest;
import com.clinica.admin_service.dto.UsuarioEstadoDTO;
import com.clinica.admin_service.model.Rol;
import com.clinica.admin_service.model.Usuario;
import com.clinica.admin_service.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RestTemplate restTemplate;

    private static final String AUTH_SERVICE_URL = "http://localhost:8081/auth/register";
    private static final String AGENDA_SERVICE_URL = "http://localhost:8085/api/doctor";
    private static final String CITAS_SERVICE_URL = "http://localhost:8085/api/citas";
    private static final String ESPECIALIDAD_SERVICE_URL = "http://localhost:8085/api/especialidad";

    @Override
    public Usuario guardarUsuario(Usuario usuario) {
        String rawPassword = usuario.getPassword();
        usuario.setPassword(passwordEncoder.encode(rawPassword));
        Usuario savedUsuario = usuarioRepository.save(usuario);
        
        sincronizarConAutenticacion(savedUsuario, rawPassword);
        
        return savedUsuario;
    }

    @Override
    public Usuario registrarDoctor(RegistroDoctorRequest request) {
        if (usuarioRepository.findByNombre(request.getNombre()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con el nombre: " + request.getNombre());
        }
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con el email: " + request.getEmail());
        }
        if (usuarioRepository.findByRut(request.getRut()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con el RUT: " + request.getRut());
        }

        Usuario doctor = new Usuario();
        doctor.setNombre(request.getNombre());
        doctor.setRut(request.getRut());
        doctor.setEmail(request.getEmail());
        doctor.setPassword(passwordEncoder.encode(request.getPassword()));
        doctor.setTelefono(request.getTelefono());
        doctor.setRol(Rol.DOCTOR);
        doctor.setEstado("ACTIVO");

        Usuario savedDoctor = usuarioRepository.save(doctor);
        
        sincronizarConAutenticacion(savedDoctor, request.getPassword());
        sincronizarConAgenda(savedDoctor);
        
        return savedDoctor;
    }

    private void sincronizarConAutenticacion(Usuario usuario, String rawPassword) {
        try {
            Map<String, String> authRequest = new HashMap<>();
            authRequest.put("nombre", usuario.getNombre());
            authRequest.put("email", usuario.getEmail());
            authRequest.put("password", rawPassword);
            authRequest.put("rol", usuario.getRol().name());
            authRequest.put("telefono", usuario.getTelefono() != null ? usuario.getTelefono() : "");
            authRequest.put("estado", usuario.getEstado() != null ? usuario.getEstado() : "activo");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(authRequest, headers);

            restTemplate.postForEntity(AUTH_SERVICE_URL, entity, String.class);
        } catch (Exception e) {
            System.err.println("Error al sincronizar con autenticacion: " + e.getMessage());
        }
    }

    private void sincronizarConAgenda(Usuario doctor) {
        try {
            Map<String, Object> agendaRequest = new HashMap<>();
            agendaRequest.put("nombre", doctor.getNombre());
            agendaRequest.put("apellido", "");
            agendaRequest.put("rut", doctor.getRut());
            agendaRequest.put("correo", doctor.getEmail());
            agendaRequest.put("telefono", doctor.getTelefono() != null ? doctor.getTelefono() : "");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(agendaRequest, headers);

            restTemplate.postForEntity(AGENDA_SERVICE_URL, entity, String.class);
        } catch (Exception e) {
            System.err.println("Error al sincronizar con agenda: " + e.getMessage());
        }
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario buscarPorRut(String rut) {
        return usuarioRepository.findByRut(rut)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con RUT: " + rut));
    }

    @Override
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("El usuario con ID " + id + " no existe.");
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public DashboardKpiDto obtenerMetricasDashboard() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        
        long total = usuarios.size();
        long admins = usuarios.stream().filter(u -> u.getRol() == Rol.ADMIN).count();
        long doc = usuarios.stream().filter(u -> u.getRol() == Rol.DOCTOR).count();
        
        Map<String, Long> distribucion = new HashMap<>();
        distribucion.put("ADMINISTRADORES", admins);
        distribucion.put("DOCTORES", doc);
        
        return new DashboardKpiDto(total, admins, doc, distribucion);
    }

    @Override
    public Usuario actualizarEstadoUsuario(Long id, UsuarioEstadoDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        usuario.setEstado(dto.getEstado());
        return usuarioRepository.save(usuario);
    }

    @Override
    public RegistroAdminDto obtenerRegistroAdmin() {
        List<RegistroAdminDto.CitaDto> citasCanceladas = new ArrayList<>();
        List<RegistroAdminDto.CitaDto> citasConfirmadas = new ArrayList<>();
        List<RegistroAdminDto.EspecialidadRankingDto> especialidadesRanking = new ArrayList<>();

        try {
            ResponseEntity<List<Map<String, Object>>> responseCanceladas = restTemplate.exchange(
                    CITAS_SERVICE_URL + "/estado/CANCELADA",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            citasCanceladas = mapearCitas(responseCanceladas.getBody());
        } catch (Exception e) {
            System.err.println("Error al obtener citas canceladas: " + e.getMessage());
        }

        try {
            ResponseEntity<List<Map<String, Object>>> responseConfirmadas = restTemplate.exchange(
                    CITAS_SERVICE_URL + "/estado/CONFIRMADA",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            citasConfirmadas = mapearCitas(responseConfirmadas.getBody());
        } catch (Exception e) {
            System.err.println("Error al obtener citas confirmadas: " + e.getMessage());
        }

        try {
            Map<Long, Long> confirmacionesPorEspecialidad = citasConfirmadas.stream()
                    .filter(c -> c.getEspecialidadId() != null)
                    .collect(Collectors.groupingBy(RegistroAdminDto.CitaDto::getEspecialidadId, Collectors.counting()));

            ResponseEntity<List<Map<String, Object>>> responseEspecialidades = restTemplate.exchange(
                    ESPECIALIDAD_SERVICE_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            List<Map<String, Object>> especialidades = responseEspecialidades.getBody();
            if (especialidades != null) {
                for (Map<String, Object> esp : especialidades) {
                    Long espId = ((Number) esp.get("idEspecialidad")).longValue();
                    String nombreEsp = (String) esp.get("nombreEsp");
                    Long totalConfirmadas = confirmacionesPorEspecialidad.getOrDefault(espId, 0L);

                    RegistroAdminDto.EspecialidadRankingDto ranking = new RegistroAdminDto.EspecialidadRankingDto();
                    ranking.setEspecialidadId(espId);
                    ranking.setNombreEspecialidad(nombreEsp);
                    ranking.setTotalCitasConfirmadas(totalConfirmadas);
                    especialidadesRanking.add(ranking);
                }

                especialidadesRanking.sort(Comparator.comparingLong(RegistroAdminDto.EspecialidadRankingDto::getTotalCitasConfirmadas).reversed());
            }
        } catch (Exception e) {
            System.err.println("Error al obtener especialidades: " + e.getMessage());
        }

        RegistroAdminDto registro = new RegistroAdminDto();
        registro.setCitasCanceladas(citasCanceladas);
        registro.setCitasConfirmadas(citasConfirmadas);
        registro.setEspecialidadesMayorConfirmacion(especialidadesRanking);

        return registro;
    }

    private List<RegistroAdminDto.CitaDto> mapearCitas(List<Map<String, Object>> citasMap) {
        List<RegistroAdminDto.CitaDto> citas = new ArrayList<>();
        if (citasMap != null) {
            for (Map<String, Object> citaMap : citasMap) {
                RegistroAdminDto.CitaDto cita = new RegistroAdminDto.CitaDto();
                cita.setId(((Number) citaMap.get("id")).longValue());
                cita.setEstado((String) citaMap.get("estado"));
                if (citaMap.get("doctorId") != null) {
                    cita.setDoctorId(((Number) citaMap.get("doctorId")).longValue());
                }
                if (citaMap.get("pacienteId") != null) {
                    cita.setPacienteId(((Number) citaMap.get("pacienteId")).longValue());
                }
                if (citaMap.get("especialidadId") != null) {
                    cita.setEspecialidadId(((Number) citaMap.get("especialidadId")).longValue());
                }
                cita.setMotivoCancelacion((String) citaMap.get("motivoCancelacion"));
                if (citaMap.get("confirmada") != null) {
                    cita.setConfirmada((Boolean) citaMap.get("confirmada"));
                }
                citas.add(cita);
            }
        }
        return citas;
    }
}