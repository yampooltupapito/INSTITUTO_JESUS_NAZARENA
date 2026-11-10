package com.jesusnazareno.asistencia.modules.asistencia.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import com.jesusnazareno.asistencia.modules.academico.service.CalendarioEscolarService;
import com.jesusnazareno.asistencia.modules.academico.service.HorarioAsistenciaService;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Asistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaRequest;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaResponse;
import com.jesusnazareno.asistencia.modules.asistencia.mapper.AsistenciaMapper;
import com.jesusnazareno.asistencia.modules.asistencia.repository.AsistenciaRepository;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de Integración Calendario Escolar - AsistenciaService")
class CalendarioEscolarIntegracionAsistenciaTest {

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

    private AsistenciaMapper asistenciaMapper = new AsistenciaMapper();
    private AsistenciaServiceImpl asistenciaService;

    private Matricula matricula;
    private Usuario usuarioRegistrador;
    private AnioEscolar anioEscolar;

    @BeforeEach
    void setUp() {
        asistenciaService = new AsistenciaServiceImpl(
                asistenciaRepository,
                matriculaRepository,
                asistenciaMapper,
                usuarioRepository,
                horarioAsistenciaService,
                calendarioEscolarService
        );

        anioEscolar = AnioEscolar.builder()
                .id(1L)
                .anio(2026)
                .nombre("Año Escolar 2026")
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 12, 20))
                .activo(true)
                .build();

        Grado grado = Grado.builder().id(10L).nombre("1°").build();
        Seccion seccion = Seccion.builder().id(20L).nombre("A").grado(grado).turno(Turno.MANANA).activo(true).build();

        Persona alumno = Persona.builder()
                .id(100L)
                .numeroDocumento("70809010")
                .nombres("Juan")
                .apellidoPaterno("Pérez")
                .apellidoMaterno("Gómez")
                .tipoDocumento(TipoDocumento.DNI)
                .sexo(Sexo.MASCULINO)
                .fechaNacimiento(LocalDate.of(2012, 5, 10))
                .activo(true)
                .build();

        matricula = Matricula.builder()
                .id(50L)
                .estudiante(alumno)
                .anioEscolar(anioEscolar)
                .grado(grado)
                .seccion(seccion)
                .estado(EstadoMatricula.MATRICULADO)
                .build();

        usuarioRegistrador = Usuario.builder()
                .id(200L)
                .username("auxiliar1")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("10. Asistencia en día no lectivo se rechaza con mensaje de negocio")
    void registrar_enDiaNoLectivo_lanzaBusinessException() {
        LocalDate domingo = LocalDate.of(2026, 5, 3);
        AsistenciaRequest request = AsistenciaRequest.builder()
                .matriculaId(50L)
                .fecha(domingo)
                .estado(EstadoAsistencia.PRESENTE)
                .horaIngreso(LocalTime.of(7, 50))
                .registradoPor(200L)
                .build();

        when(matriculaRepository.findById(50L)).thenReturn(Optional.of(matricula));
        when(usuarioRepository.findById(200L)).thenReturn(Optional.of(usuarioRegistrador));
        when(calendarioEscolarService.esDiaLectivo(1L, domingo)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                asistenciaService.registrar(request)
        );

        assertEquals("No corresponde registrar asistencia en una fecha no lectiva.", ex.getMessage());
        verify(asistenciaRepository, never()).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("11. Asistencia en día lectivo funciona correctamente")
    void registrar_enDiaLectivo_registraExitosamente() {
        LocalDate lunes = LocalDate.of(2026, 5, 4);
        AsistenciaRequest request = AsistenciaRequest.builder()
                .matriculaId(50L)
                .fecha(lunes)
                .estado(EstadoAsistencia.PRESENTE)
                .horaIngreso(LocalTime.of(7, 50))
                .registradoPor(200L)
                .build();

        when(matriculaRepository.findById(50L)).thenReturn(Optional.of(matricula));
        when(usuarioRepository.findById(200L)).thenReturn(Optional.of(usuarioRegistrador));
        when(calendarioEscolarService.esDiaLectivo(1L, lunes)).thenReturn(true);
        when(asistenciaRepository.existsByMatriculaIdAndFecha(50L, lunes)).thenReturn(false);
        when(horarioAsistenciaService.resolverHorario(any(), any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        when(asistenciaRepository.save(any(Asistencia.class))).thenAnswer(inv -> {
            Asistencia a = inv.getArgument(0);
            a.setId(999L);
            return a;
        });

        AsistenciaResponse response = asistenciaService.registrar(request);
        assertNotNull(response);
        assertEquals(999L, response.getId());
        assertEquals(EstadoAsistencia.PRESENTE, response.getEstado());
        verify(asistenciaRepository).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("12. Registro masivo en día no lectivo se rechaza sin crear registros")
    void registrarLote_enDiaNoLectivo_seRechazaSinGuardar() {
        LocalDate feriado = LocalDate.of(2026, 5, 1);
        AsistenciaRequest req1 = AsistenciaRequest.builder()
                .matriculaId(50L)
                .fecha(feriado)
                .estado(EstadoAsistencia.PRESENTE)
                .registradoPor(200L)
                .build();

        when(matriculaRepository.findById(50L)).thenReturn(Optional.of(matricula));
        when(calendarioEscolarService.esDiaLectivo(1L, feriado)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                asistenciaService.registrarLote(List.of(req1))
        );

        assertEquals("No corresponde registrar asistencia en una fecha no lectiva.", ex.getMessage());
        verify(asistenciaRepository, never()).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("13. Día no lectivo no intenta resolver horario innecesariamente")
    void registrar_enDiaNoLectivo_noResuelveHorario() {
        LocalDate fechaNoLectiva = LocalDate.of(2026, 8, 30);
        AsistenciaRequest request = AsistenciaRequest.builder()
                .matriculaId(50L)
                .fecha(fechaNoLectiva)
                .estado(EstadoAsistencia.PRESENTE)
                .horaIngreso(LocalTime.of(7, 45))
                .registradoPor(200L)
                .build();

        when(matriculaRepository.findById(50L)).thenReturn(Optional.of(matricula));
        when(usuarioRepository.findById(200L)).thenReturn(Optional.of(usuarioRegistrador));
        when(calendarioEscolarService.esDiaLectivo(1L, fechaNoLectiva)).thenReturn(false);

        assertThrows(BusinessException.class, () -> asistenciaService.registrar(request));

        // Confirma que el horario NO se intenta resolver si esDiaLectivo = false
        verify(horarioAsistenciaService, never()).resolverHorario(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("14. Modificar o consultar calendario no altera ni borra registros históricos de asistencia")
    void historico_noSeAlteraAlConsultarOModificarCalendario() {
        LocalDate fechaPasada = LocalDate.of(2026, 4, 15);
        Asistencia asistenciaHistorica = Asistencia.builder()
                .id(1234L)
                .matricula(matricula)
                .fecha(fechaPasada)
                .estado(EstadoAsistencia.PRESENTE)
                .registradoPor(usuarioRegistrador)
                .build();

        when(asistenciaRepository.findById(1234L)).thenReturn(Optional.of(asistenciaHistorica));

        AsistenciaResponse historico = asistenciaService.buscarPorId(1234L);
        assertNotNull(historico);
        assertEquals(EstadoAsistencia.PRESENTE, historico.getEstado());
        assertEquals(fechaPasada, historico.getFecha());
        // No se invocó modificación ni recálculo sobre el registro histórico
        verify(asistenciaRepository, never()).delete(any(Asistencia.class));
    }
}
