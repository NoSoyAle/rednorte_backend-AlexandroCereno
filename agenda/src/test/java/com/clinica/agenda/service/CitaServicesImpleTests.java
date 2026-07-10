package com.clinica.agenda.service;
import com.clinica.agenda.entities.Cita;
import com.clinica.agenda.entities.DisponibilidadDoctor;
import com.clinica.agenda.repository.CitaRpository; 
import com.clinica.agenda.repository.DisponibilidadDoctorRepository;
import com.clinica.agenda.entities.dto.PacienteDTO;
import com.clinica.agenda.entities.dto.CitaDetDTO;
import com.clinica.agenda.entities.dto.PacienteAtendidoDTO;
import com.clinica.agenda.enums.DiaSemana;
import com.clinica.agenda.enums.EstadoCita;
import com.clinica.agenda.services.CitaServiceImplement;
    
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
    
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
    
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
    
@ExtendWith(MockitoExtension.class)
public class CitaServicesImpleTests {
    
        @Mock
        private CitaRpository citaRepo;
    
        @Mock
        private DisponibilidadDoctorRepository disponibilidadDoctorRepository;
    
        @Mock
        private WebClient pacienteWebClient;
    
        // Mocks encadenados necesarios para simular el comportamiento fluido de WebClient
        @Mock
        private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    
        @Mock
        private WebClient.RequestHeadersSpec requestHeadersSpec;
    
        @Mock
        private WebClient.ResponseSpec responseSpec;
    
        @Mock
        private Mono<PacienteDTO> pacienteMono;
    
        @InjectMocks
        private CitaServiceImplement citaService;
    
        private Cita citaTest;
        private PacienteDTO pacienteTest;
        private DisponibilidadDoctor disponibilidadTest;
    
        @BeforeEach
        void setUp() {
            // Cita de prueba base (Fecha cae día Lunes para las pruebas de horarios)
            citaTest = new Cita();
            citaTest.setId(1L);
            citaTest.setPacienteId(100L);
            citaTest.setFecha(LocalDate.of(2026, 6, 22)); // 22 de Junio 2026 es Lunes
            citaTest.setHoraInicio(LocalTime.of(9, 0));
            citaTest.setHoraFin(LocalTime.of(9, 30));
            citaTest.setEstado(EstadoCita.REALIZADA);
    
            // DTO de prueba que retornará el microservicio de Pacientes mediante WebClient
            pacienteTest = new PacienteDTO();
            pacienteTest.setId(100L);
            pacienteTest.setNombre("Juan");
            pacienteTest.setApellido("Pérez");
            pacienteTest.setRut("12.345.678-9");
            pacienteTest.setEmail("juan.perez@clinica.com");
            pacienteTest.setTelefono("+56912345678");
    
            // Disponibilidad de doctor base
            disponibilidadTest = new DisponibilidadDoctor();
            disponibilidadTest.setHoraInicio(LocalTime.of(8, 0));
            disponibilidadTest.setHoraFin(LocalTime.of(10, 0));
            disponibilidadTest.setDuracionMinutos(30);
        }
    
        /**
         * Helper Method para simular las llamadas encadenadas de WebClient.
         * Usamos lenient() para evitar problemas si algún test no ejecuta toda la cadena.
         */
        private void mockWebClientChain(PacienteDTO respuestaDto) {
            lenient().when(pacienteWebClient.get()).thenReturn(requestHeadersUriSpec);
            lenient().when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
            lenient().when(requestHeadersUriSpec.uri(anyString(), any(Object.class))).thenReturn(requestHeadersSpec);
            lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
            lenient().when(responseSpec.bodyToMono(PacienteDTO.class)).thenReturn(pacienteMono);
            lenient().when(pacienteMono.block()).thenReturn(respuestaDto);
        }
    
        @Test
        @DisplayName("listarTodos - debe retornar todas las citas guardadas")
        void listarTodos_DebeRetornarTodasLasCitas() {
            when(citaRepo.findAll()).thenReturn(Arrays.asList(citaTest));
    
            List<Cita> resultado = citaService.listarTodos();
    
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(citaRepo, times(1)).findAll();
        }
    
        @Test
        @DisplayName("buscarPorId - debe retornar la cita cuando el id existe")
        void buscarPorId_DebeRetornarCitaSiExiste() {
            when(citaRepo.findById(1L)).thenReturn(Optional.of(citaTest));
    
            Cita resultado = citaService.buscarPorId(1L);
    
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            verify(citaRepo, times(1)).findById(1L);
        }
    
        @Test
        @DisplayName("buscarPorId - debe lanzar RuntimeException si la cita no existe")
        void buscarPorId_DebeLanzarExcepcionSiNoExiste() {
            when(citaRepo.findById(999L)).thenReturn(Optional.empty());
    
            assertThatThrownBy(() -> citaService.buscarPorId(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Cita no encontrada");
        }
    
        @Test
        @DisplayName("guardar - debe persistir la cita si el cliente WebClient encuentra al paciente")
        void guardar_DebeGuardarCitaSiPacienteExiste() {
            mockWebClientChain(pacienteTest);
            when(citaRepo.save(citaTest)).thenReturn(citaTest);
    
            Cita resultado = citaService.guardar(citaTest);
    
            assertNotNull(resultado);
            verify(citaRepo, times(1)).save(citaTest);
        }
    
        @Test
        @DisplayName("guardar - debe lanzar excepcion y no guardar si WebClient retorna null")
        void guardar_DebeLanzarExcepcionSiPacienteNoExiste() {
            mockWebClientChain(null);
    
            assertThatThrownBy(() -> citaService.guardar(citaTest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Paciente no encontrado");
    
            verify(citaRepo, never()).save(any(Cita.class));
        }
    
        @Test
        @DisplayName("obtenerPorFecha - debe mapear citas a CitaDetDTO correctamente consumiendo WebClient")
        void obtenerPorFecha_DebeRetornarListaCitaDetDTO() {
            mockWebClientChain(pacienteTest);
            LocalDate fechaBusqueda = LocalDate.of(2026, 6, 22);
            when(citaRepo.findByDoctor_IdAndFecha(1L, fechaBusqueda)).thenReturn(Arrays.asList(citaTest));
    
            List<CitaDetDTO> resultado = citaService.obtenerPorFecha(1L, fechaBusqueda);
    
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            
            CitaDetDTO dto = resultado.get(0);
            assertEquals("Juan Pérez", dto.getNombrePaciente());
            assertEquals(citaTest.getId(), dto.getId());
            assertEquals(pacienteTest.getRut(), dto.getRutPaciente());
        }
    
        @Test
        @DisplayName("obtenerPacientesAtendidos - debe filtrar citas REALIZADAS y eliminar duplicados")
        void obtenerPacientesAtendidos_DebeFiltrarYRetornarPacientesUnicos() {
            mockWebClientChain(pacienteTest);
            
            Cita citaDuplicadaMismoPaciente = new Cita();
            citaDuplicadaMismoPaciente.setPacienteId(100L);
            citaDuplicadaMismoPaciente.setEstado(EstadoCita.REALIZADA);
    
            Cita citaCancelada = new Cita();
            citaCancelada.setPacienteId(200L);
            citaCancelada.setEstado(EstadoCita.CANCELADA); // Esta debe ser ignorada por el filtro
    
            when(citaRepo.findByDoctor_Id(1L)).thenReturn(Arrays.asList(citaTest, citaDuplicadaMismoPaciente, citaCancelada));
    
            List<PacienteAtendidoDTO> resultado = citaService.obtenerPacientesAtendidos(1L);
    
            assertNotNull(resultado);
            assertEquals(1, resultado.size()); // El .distinct() del stream debe dejar solo 1 elemento
            assertEquals("Juan", resultado.get(0).getNombre());
        }
    
        @Test
        @DisplayName("actualizar - debe actualizar estado y doctor preservando la cita original")
        void actualizar_DebeActualizarCamposDeCitaExistente() {
            when(citaRepo.findById(1L)).thenReturn(Optional.of(citaTest));
            when(citaRepo.save(any(Cita.class))).thenAnswer(invocation -> invocation.getArgument(0));
    
            Cita datosNuevos = new Cita();
            datosNuevos.setEstado(EstadoCita.CANCELADA);
    
            Cita resultado = citaService.actualizar(1L, datosNuevos);
    
            assertNotNull(resultado);
            assertEquals(EstadoCita.CANCELADA, resultado.getEstado());
            verify(citaRepo, times(1)).save(any(Cita.class));
        }
    
        @Test
        @DisplayName("eliminar - debe delegar la eliminacion al repositorio")
        void eliminar_DebeInvocarDeleteById() {
            doNothing().when(citaRepo).deleteById(1L);
    
            citaService.eliminar(1L);
    
            verify(citaRepo, times(1)).deleteById(1L);
        }
    
        @Test
        @DisplayName("obtenerCitasDoctor - debe listar todas las citas de un doctor")
        void obtenerCitasDoctor_DebeRetornarCitasDelDoctor() {
            when(citaRepo.findByDoctor_Id(1L)).thenReturn(Arrays.asList(citaTest));
    
            List<Cita> resultado = citaService.obtenerCitasDoctor(1L);
    
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(citaRepo, times(1)).findByDoctor_Id(1L);
        }
    
        @Test
        @DisplayName("obtenerHorariosDisponibles - debe calcular bloques de tiempo excluyendo horas ocupadas")
        void obtenerHorariosDisponibles_DebeCalcularHorariosYExcluirOcupados() {
            LocalDate lunes = LocalDate.of(2026, 6, 22); // Convierte a DiaSemana.LUNES
            
            when(disponibilidadDoctorRepository.findByDoctor_IdAndDiaSemana(1L, DiaSemana.LUNES))
                    .thenReturn(Arrays.asList(disponibilidadTest)); // Rango: 08:00 a 10:00, cada 30 min
    
            // Simulamos una cita agendada (ocupada) a las 08:30
            Cita citaOcupada = new Cita();
            citaOcupada.setHoraInicio(LocalTime.of(8, 30));
            citaOcupada.setEstado(EstadoCita.Pendiente);
    
            // Simulamos otra cita pero CANCELADA a las 09:00 (esta hora SÍ debe aparecer disponible)
            Cita citaCancelada = new Cita();
            citaCancelada.setHoraInicio(LocalTime.of(9, 0));
            citaCancelada.setEstado(EstadoCita.CANCELADA);
    
            when(citaRepo.findByDoctor_IdAndFecha(1L, lunes)).thenReturn(Arrays.asList(citaOcupada, citaCancelada));
    
            List<LocalTime> resultado = citaService.obtenerHorariosDisponibles(1L, lunes);
    
            assertNotNull(resultado);
            // Rangos esperados del bucle: 08:00 (Ok), 08:30 (Ocupado), 09:00 (Ok - cancelado), 09:30 (Ok) -> Fin 10:00
            assertEquals(3, resultado.size());
            assertThat(resultado).containsExactly(
                    LocalTime.of(8, 0),
                    LocalTime.of(9, 0),
                    LocalTime.of(9, 30)
            );
        }
    
        @Test
        @DisplayName("obtenerHorariosDisponibles - debe retornar vacio si el doctor no tiene disponibilidad configurada")
        void obtenerHorariosDisponibles_DebeRetornarListaVaciaSiNoHayConfiguracion() {
            LocalDate lunes = LocalDate.of(2026, 6, 22);
            when(disponibilidadDoctorRepository.findByDoctor_IdAndDiaSemana(1L, DiaSemana.LUNES))
                    .thenReturn(new ArrayList<>()); // Retorna lista vacía (.findFirst() resultará en Empty)
    
            List<LocalTime> resultado = citaService.obtenerHorariosDisponibles(1L, lunes);
    
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(citaRepo, never()).findByDoctor_IdAndFecha(any(), any()); // No debería ni consultar las citas
        };
    }

