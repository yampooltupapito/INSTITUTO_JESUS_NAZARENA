package com.jesusnazareno.asistencia.modules.notificacion.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.acceso.domain.Rol;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.alerta.domain.AlertaAsistencia;
import com.jesusnazareno.asistencia.modules.alerta.domain.enums.EstadoAlerta;
import com.jesusnazareno.asistencia.modules.alerta.domain.enums.TipoAlertaAsistencia;
import com.jesusnazareno.asistencia.modules.alerta.repository.AlertaAsistenciaRepository;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Asistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Justificacion;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoJustificacion;
import com.jesusnazareno.asistencia.modules.asistencia.repository.JustificacionRepository;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.notificacion.domain.Notificacion;
import com.jesusnazareno.asistencia.modules.notificacion.domain.enums.TipoNotificacion;
import com.jesusnazareno.asistencia.modules.notificacion.dto.NotificacionResponse;
import com.jesusnazareno.asistencia.modules.notificacion.event.NotificacionAlertaEvent;
import com.jesusnazareno.asistencia.modules.notificacion.event.NotificacionJustificacionEvent;
import com.jesusnazareno.asistencia.modules.notificacion.mapper.NotificacionMapper;
import com.jesusnazareno.asistencia.modules.notificacion.repository.NotificacionRepository;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Pruebas Unitarias - NotificacionService")
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AlertaAsistenciaRepository alertaRepository;

    @Mock
    private JustificacionRepository justificacionRepository;

    private final NotificacionMapper notificacionMapper = new NotificacionMapper();

    private NotificacionServiceImpl notificacionService;

    private Usuario usuarioA;
    private Usuario usuarioB;
    private Usuario usuarioDirector;
    private Notificacion notificacionA;
    private Persona personaEstudiante;
    private Matricula matricula;
    private AlertaAsistencia alerta;
    private Justificacion justificacion;

    @BeforeEach
    void setUp() {
        notificacionService = new NotificacionServiceImpl(
                notificacionRepository,
                usuarioRepository,
                alertaRepository,
                justificacionRepository,
                notificacionMapper
        );

        Rol rolDocente = Rol.builder().id(1L).codigo("DOCENTE").nombre("Docente").build();
        Rol rolDirector = Rol.builder().id(2L).codigo("DIRECTOR").nombre("Director").build();

        usuarioA = Usuario.builder().id(10L).username("docente1").rol(rolDocente).activo(true).build();
        usuarioB = Usuario.builder().id(20L).username("docente2").rol(rolDocente).activo(true).build();
        usuarioDirector = Usuario.builder().id(30L).username("director").rol(rolDirector).activo(true).build();

        notificacionA = Notificacion.builder()
                .id(100L)
                .usuario(usuarioA)
                .tipo(TipoNotificacion.ALERTA_ASISTENCIA)
                .titulo("Nueva Alerta de Asistencia")
                .mensaje("El estudiante Juan Perez tiene 3 ausencias consecutivas.")
                .referenciaTipo("ALERTA_ASISTENCIA")
                .referenciaId(5L)
                .urlDestino("/alertas/5")
                .leida(false)
                .fechaLectura(null)
                .activo(true)
                .createdAt(LocalDateTime.now())
                .build();

        personaEstudiante = Persona.builder()
                .id(50L)
                .nombres("Juan")
                .apellidoPaterno("Quispe")
                .apellidoMaterno("Mamani")
                .numeroDocumento("70809010")
                .activo(true)
                .build();

        Grado grado = Grado.builder().id(1L).nombre("1° Grado").build();
        Seccion seccion = Seccion.builder().id(1L).nombre("A").build();

        matricula = Matricula.builder()
                .id(10L)
                .estudiante(personaEstudiante)
                .grado(grado)
                .seccion(seccion)
                .build();

        alerta = AlertaAsistencia.builder()
                .id(5L)
                .matricula(matricula)
                .tipo(TipoAlertaAsistencia.AUSENCIAS_CONSECUTIVAS)
                .cantidad(3)
                .fechaDeteccion(LocalDate.of(2026, 4, 10))
                .estado(EstadoAlerta.PENDIENTE)
                .activo(true)
                .build();

        Asistencia asistencia = Asistencia.builder()
                .id(200L)
                .matricula(matricula)
                .fecha(LocalDate.of(2026, 4, 10))
                .build();

        justificacion = Justificacion.builder()
                .id(25L)
                .asistencia(asistencia)
                .motivo("Cita médica")
                .estado(EstadoJustificacion.PENDIENTE)
                .activo(true)
                .createdBy("docente1")
                .build();
    }

    @Test
    @DisplayName("1. Usuario autenticado puede consultar sus notificaciones")
    void listarDelUsuario_usuarioAutenticado_retornaPagina() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notificacion> pagina = new PageImpl<>(List.of(notificacionA), pageable, 1);

        when(notificacionRepository.buscarNotificacionesUsuario(eq(10L), eq(null), eq(null), any(Pageable.class)))
                .thenReturn(pagina);

        Page<NotificacionResponse> result = notificacionService.listarDelUsuario(10L, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Nueva Alerta de Asistencia", result.getContent().get(0).getTitulo());
        assertEquals("/alertas/5", result.getContent().get(0).getUrlDestino());
    }

    @Test
    @DisplayName("2. Nueva notificación comienza como NO LEÍDA")
    void crear_nuevaNotificacion_iniciaNoLeidaYActiva() {
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(inv -> {
            Notificacion n = inv.getArgument(0);
            n.setId(101L);
            return n;
        });

        NotificacionResponse res = notificacionService.crearNotificacion(
                usuarioA,
                TipoNotificacion.SISTEMA,
                "Mensaje del Sistema",
                "Mantenimiento programado.",
                null,
                null,
                "/dashboard"
        );

        assertNotNull(res);
        assertFalse(res.getLeida());
        assertNull(res.getFechaLectura());

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(notificacionRepository).save(captor.capture());
        Notificacion guardada = captor.getValue();
        assertFalse(guardada.getLeida());
        assertNull(guardada.getFechaLectura());
        assertTrue(guardada.getActivo());
    }

    @Test
    @DisplayName("3. Usuario A no puede consultar notificación de Usuario B")
    void buscarDelUsuario_perteneceAOtroUsuario_lanzaExcepcion() {
        when(notificacionRepository.findById(100L)).thenReturn(Optional.of(notificacionA));

        // Usuario B (id=20L) intenta acceder a notificacionA (pertenece a usuario A con id=10L)
        assertThrows(BusinessException.class, () ->
                notificacionService.buscarDelUsuario(100L, 20L)
        );
    }

    @Test
    @DisplayName("4. Marcar leída actualiza leida=true y establece fechaLectura")
    void marcarComoLeida_notificacionNoLeida_actualizaLeidaYFecha() {
        when(notificacionRepository.findById(100L)).thenReturn(Optional.of(notificacionA));
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        NotificacionResponse res = notificacionService.marcarComoLeida(100L, 10L);

        assertTrue(res.getLeida());
        assertNotNull(res.getFechaLectura());

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(notificacionRepository).save(captor.capture());
        assertTrue(captor.getValue().getLeida());
        assertNotNull(captor.getValue().getFechaLectura());
    }

    @Test
    @DisplayName("5. Marcar leída dos veces es idempotente y seguro")
    void marcarComoLeida_yaLeida_esIdempotente() {
        LocalDateTime fechaOriginal = LocalDateTime.now().minusHours(1);
        notificacionA.setLeida(true);
        notificacionA.setFechaLectura(fechaOriginal);

        when(notificacionRepository.findById(100L)).thenReturn(Optional.of(notificacionA));

        NotificacionResponse res = notificacionService.marcarComoLeida(100L, 10L);

        assertTrue(res.getLeida());
        assertEquals(fechaOriginal, notificacionA.getFechaLectura());
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("6. Usuario A no puede marcar como leída notificación de Usuario B")
    void marcarComoLeida_usuarioNoPropietario_lanzaExcepcion() {
        when(notificacionRepository.findById(100L)).thenReturn(Optional.of(notificacionA));

        assertThrows(BusinessException.class, () ->
                notificacionService.marcarComoLeida(100L, 20L)
        );
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("7. Marcar todas como leídas afecta solo al usuario actual")
    void marcarTodasComoLeidas_afectaSoloAlUsuarioActual() {
        when(notificacionRepository.marcarTodasComoLeidasUsuario(eq(10L), any(LocalDateTime.class)))
                .thenReturn(5);

        int actualizadas = notificacionService.marcarTodasComoLeidas(10L);

        assertEquals(5, actualizadas);
        verify(notificacionRepository).marcarTodasComoLeidasUsuario(eq(10L), any(LocalDateTime.class));
        verify(notificacionRepository, never()).marcarTodasComoLeidasUsuario(eq(20L), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("8. Contador de no leídas es eficiente y correcto")
    void contarNoLeidas_retornaCantidad() {
        when(notificacionRepository.countByUsuarioIdAndLeidaFalseAndActivoTrue(10L)).thenReturn(3L);

        long contador = notificacionService.contarNoLeidas(10L);

        assertEquals(3L, contador);
        verify(notificacionRepository).countByUsuarioIdAndLeidaFalseAndActivoTrue(10L);
    }

    @Test
    @DisplayName("9. Filtro LEIDAS y NO_LEIDAS funciona")
    void listarDelUsuario_conFiltrosEstado_consultaRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        when(notificacionRepository.buscarNotificacionesUsuario(eq(10L), eq(false), eq(TipoNotificacion.ALERTA_ASISTENCIA), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(notificacionA)));

        Page<NotificacionResponse> noLeidas = notificacionService.listarDelUsuario(10L, false, TipoNotificacion.ALERTA_ASISTENCIA, pageable);
        assertEquals(1, noLeidas.getTotalElements());

        when(notificacionRepository.buscarNotificacionesUsuario(eq(10L), eq(true), eq(null), eq(pageable)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<NotificacionResponse> leidas = notificacionService.listarDelUsuario(10L, true, null, pageable);
        assertEquals(0, leidas.getTotalElements());
    }

    @Test
    @DisplayName("10. Evento de alerta genera notificación a usuarios autorizados")
    void handleAlertaEvent_creaNotificacionesParaUsuariosConRolApropiado() {
        when(alertaRepository.findById(5L)).thenReturn(Optional.of(alerta));
        when(usuarioRepository.findByRolCodigoInAndActivoTrue(any()))
                .thenReturn(List.of(usuarioDirector));
        when(notificacionRepository.existsByTipoAndReferenciaTipoAndReferenciaIdAndUsuarioIdAndActivoTrue(
                any(), any(), any(), any()))
                .thenReturn(false);

        NotificacionAlertaEvent evento = new NotificacionAlertaEvent(5L);

        notificacionService.onNotificacionAlertaEvent(evento);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(notificacionRepository).save(captor.capture());
        Notificacion guardada = captor.getValue();
        assertEquals(usuarioDirector.getId(), guardada.getUsuario().getId());
        assertEquals(TipoNotificacion.ALERTA_ASISTENCIA, guardada.getTipo());
        assertEquals("ALERTA_ASISTENCIA", guardada.getReferenciaTipo());
        assertEquals(5L, guardada.getReferenciaId());
        assertEquals("/alertas/5", guardada.getUrlDestino());
    }

    @Test
    @DisplayName("11. No se duplica notificación por el mismo evento")
    void handleAlertaEvent_yaExisteNotificacion_noGuardaDuplicado() {
        when(alertaRepository.findById(5L)).thenReturn(Optional.of(alerta));
        when(usuarioRepository.findByRolCodigoInAndActivoTrue(any()))
                .thenReturn(List.of(usuarioDirector));
        when(notificacionRepository.existsByTipoAndReferenciaTipoAndReferenciaIdAndUsuarioIdAndActivoTrue(
                TipoNotificacion.ALERTA_ASISTENCIA, "ALERTA_ASISTENCIA", 5L, usuarioDirector.getId()))
                .thenReturn(true);

        NotificacionAlertaEvent evento = new NotificacionAlertaEvent(5L);

        notificacionService.onNotificacionAlertaEvent(evento);

        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("12. Evento justificación PENDIENTE notifica a revisores")
    void handleJustificacionEvent_creadaPendiente_notificaRevisores() {
        when(justificacionRepository.findById(25L)).thenReturn(Optional.of(justificacion));
        when(usuarioRepository.findByRolCodigoInAndActivoTrue(any()))
                .thenReturn(List.of(usuarioDirector));
        when(notificacionRepository.existsByTipoAndReferenciaTipoAndReferenciaIdAndUsuarioIdAndActivoTrue(
                any(), any(), any(), any()))
                .thenReturn(false);

        NotificacionJustificacionEvent evento = new NotificacionJustificacionEvent(25L, TipoNotificacion.JUSTIFICACION_CREADA);

        notificacionService.onNotificacionJustificacionEvent(evento);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(notificacionRepository).save(captor.capture());
        Notificacion guardada = captor.getValue();
        assertEquals(TipoNotificacion.JUSTIFICACION_CREADA, guardada.getTipo());
        assertEquals("JUSTIFICACION", guardada.getReferenciaTipo());
        assertEquals(25L, guardada.getReferenciaId());
        assertEquals("/justificaciones/25", guardada.getUrlDestino());
    }

    @Test
    @DisplayName("13. Justificación aprobada notifica solo a destinatario válido")
    void handleJustificacionEvent_aprobada_notificaDestinatarioValido() {
        justificacion.setEstado(EstadoJustificacion.APROBADA);
        justificacion.setObservacionRevision("Aprobada por la dirección");

        when(justificacionRepository.findById(25L)).thenReturn(Optional.of(justificacion));
        when(usuarioRepository.findByUsername("docente1")).thenReturn(Optional.of(usuarioA));
        when(notificacionRepository.existsByTipoAndReferenciaTipoAndReferenciaIdAndUsuarioIdAndActivoTrue(
                any(), any(), any(), any()))
                .thenReturn(false);

        NotificacionJustificacionEvent evento = new NotificacionJustificacionEvent(25L, TipoNotificacion.JUSTIFICACION_APROBADA);

        notificacionService.onNotificacionJustificacionEvent(evento);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(notificacionRepository).save(captor.capture());
        Notificacion guardada = captor.getValue();
        assertEquals(usuarioA.getId(), guardada.getUsuario().getId());
        assertEquals(TipoNotificacion.JUSTIFICACION_APROBADA, guardada.getTipo());
    }

    @Test
    @DisplayName("14. Justificación sin destinatario usuario no rompe ni falla")
    void handleJustificacionEvent_sinDestinatarioUsuario_noFalla() {
        justificacion.setCreatedBy(null);
        when(justificacionRepository.findById(25L)).thenReturn(Optional.of(justificacion));
        when(usuarioRepository.findByPersonaId(anyLong())).thenReturn(Optional.empty());

        NotificacionJustificacionEvent evento = new NotificacionJustificacionEvent(25L, TipoNotificacion.JUSTIFICACION_APROBADA);

        notificacionService.onNotificacionJustificacionEvent(evento);

        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("15. URL externa maliciosa es sanitizada a ruta interna segura")
    void crear_urlMaliciosa_seSanitizaARutaInterna() {
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        NotificacionResponse res = notificacionService.crearNotificacion(
                usuarioA,
                TipoNotificacion.SISTEMA,
                "Aviso",
                "Mensaje",
                null,
                null,
                "https://malicious.example.com/phish"
        );

        assertEquals("/", res.getUrlDestino());
    }

    @Test
    @DisplayName("16. Notificación no causa DELETE físico")
    void noDeleteFisico_conservaRegistro() {
        verify(notificacionRepository, never()).delete(any());
        verify(notificacionRepository, never()).deleteById(anyLong());
    }
}
