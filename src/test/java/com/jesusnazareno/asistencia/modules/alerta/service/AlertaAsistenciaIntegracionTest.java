package com.jesusnazareno.asistencia.modules.alerta.service;

import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.service.CalendarioEscolarService;
import com.jesusnazareno.asistencia.modules.academico.service.HorarioAsistenciaService;
import com.jesusnazareno.asistencia.modules.alerta.event.AlertaEvaluacionEvent;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Asistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Justificacion;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoJustificacion;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaRequest;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaResponse;
import com.jesusnazareno.asistencia.modules.asistencia.dto.RevisionJustificacionRequest;
import com.jesusnazareno.asistencia.modules.asistencia.mapper.AsistenciaMapper;
import com.jesusnazareno.asistencia.modules.asistencia.mapper.JustificacionMapper;
import com.jesusnazareno.asistencia.modules.asistencia.repository.AsistenciaRepository;
import com.jesusnazareno.asistencia.modules.asistencia.repository.JustificacionRepository;
import com.jesusnazareno.asistencia.modules.asistencia.service.AsistenciaServiceImpl;
import com.jesusnazareno.asistencia.modules.asistencia.service.JustificacionServiceImpl;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Pruebas de Integración de Eventos - Alerta con Asistencia y Justificación")
class AlertaAsistenciaIntegracionTest {

    @Mock
    private AsistenciaRepository asistenciaRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HorarioAsistenciaService horarioAsistenciaService;

    @Mock
    private CalendarioEscolarService calendarioEscolarService;

    private final AsistenciaMapper asistenciaMapper = new AsistenciaMapper();

    @Mock
    private JustificacionRepository justificacionRepository;

    private final JustificacionMapper justificacionMapper = new JustificacionMapper();

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private AsistenciaServiceImpl asistenciaService;
    private JustificacionServiceImpl justificacionService;

    private Matricula matricula;
    private AnioEscolar anioEscolar;
    private Usuario usuarioRegistrador;

    @BeforeEach
    void setUp() {
        asistenciaService = new AsistenciaServiceImpl(
                asistenciaRepository,
                matriculaRepository,
                asistenciaMapper,
                usuarioRepository,
                horarioAsistenciaService,
                calendarioEscolarService,
                eventPublisher
        );

        justificacionService = new JustificacionServiceImpl(
                justificacionRepository,
                asistenciaRepository,
                justificacionMapper,
                usuarioRepository,
                eventPublisher
        );

        anioEscolar = AnioEscolar.builder()
                .id(1L)
                .nombre("2026")
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 12, 20))
                .activo(true)
                .build();

        com.jesusnazareno.asistencia.modules.academico.domain.Seccion seccion =
                com.jesusnazareno.asistencia.modules.academico.domain.Seccion.builder()
                        .id(1L)
                        .nombre("A")
                        .activo(true)
                        .build();

        matricula = Matricula.builder()
                .id(10L)
                .anioEscolar(anioEscolar)
                .seccion(seccion)
                .estado(EstadoMatricula.MATRICULADO)
                .codigoMatricula("MAT-2026-0010")
                .build();

        usuarioRegistrador = Usuario.builder()
                .id(5L)
                .username("auxiliar")
                .build();
    }

    @Test
    @DisplayName("Registrar asistencia publica AlertaEvaluacionEvent con matriculaId")
    void registrarAsistencia_publicaEventoEvaluacionAlerta() {
        LocalDate fecha = LocalDate.of(2026, 4, 15);
        AsistenciaRequest request = AsistenciaRequest.builder()
                .matriculaId(10L)
                .fecha(fecha)
                .estado(EstadoAsistencia.AUSENTE)
                .registradoPor(5L)
                .observacion("Sin justificar")
                .build();

        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matricula));
        when(calendarioEscolarService.esDiaLectivo(1L, fecha)).thenReturn(true);
        when(asistenciaRepository.existsByMatriculaIdAndFecha(10L, fecha)).thenReturn(false);
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuarioRegistrador));

        Asistencia guardada = Asistencia.builder()
                .id(100L)
                .matricula(matricula)
                .fecha(fecha)
                .estado(EstadoAsistencia.AUSENTE)
                .build();
        when(asistenciaRepository.save(any(Asistencia.class))).thenReturn(guardada);

        asistenciaService.registrar(request);

        // Se verifica que el evento de evaluación de alerta fue publicado con la matriculaId correspondiente
        ArgumentCaptor<AlertaEvaluacionEvent> captor = ArgumentCaptor.forClass(AlertaEvaluacionEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        AlertaEvaluacionEvent evento = captor.getValue();
        assertNotNull(evento);
        assertEquals(10L, evento.getMatriculaId());
    }

    @Test
    @DisplayName("Aprobar justificación publica AlertaEvaluacionEvent con matriculaId")
    void aprobarJustificacion_publicaEventoEvaluacionAlerta() {
        LocalDate fecha = LocalDate.of(2026, 4, 15);
        Asistencia asistencia = Asistencia.builder()
                .id(100L)
                .matricula(matricula)
                .fecha(fecha)
                .estado(EstadoAsistencia.AUSENTE)
                .build();

        Justificacion justificacion = Justificacion.builder()
                .id(50L)
                .asistencia(asistencia)
                .estado(EstadoJustificacion.PENDIENTE)
                .build();

        when(justificacionRepository.findById(50L)).thenReturn(Optional.of(justificacion));
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuarioRegistrador));
        when(justificacionRepository.save(any(Justificacion.class))).thenAnswer(i -> i.getArgument(0));
        when(asistenciaRepository.save(any(Asistencia.class))).thenAnswer(i -> i.getArgument(0));

        RevisionJustificacionRequest req = new RevisionJustificacionRequest(5L, "Certificado médico validado.");
        justificacionService.aprobar(50L, req);

        // Verifica que la asistencia pasa a JUSTIFICADO
        assertEquals(EstadoAsistencia.JUSTIFICADO, asistencia.getEstado());

        // Verifica que se dispara el evento de alerta para la matrícula
        ArgumentCaptor<AlertaEvaluacionEvent> captor = ArgumentCaptor.forClass(AlertaEvaluacionEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        AlertaEvaluacionEvent evento = captor.getValue();
        assertNotNull(evento);
        assertEquals(10L, evento.getMatriculaId());
    }
}
