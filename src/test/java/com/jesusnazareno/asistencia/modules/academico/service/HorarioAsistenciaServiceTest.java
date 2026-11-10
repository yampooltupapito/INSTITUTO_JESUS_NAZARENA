package com.jesusnazareno.asistencia.modules.academico.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.HorarioAsistencia;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import com.jesusnazareno.asistencia.modules.academico.dto.HorarioAsistenciaRequest;
import com.jesusnazareno.asistencia.modules.academico.mapper.HorarioAsistenciaMapper;
import com.jesusnazareno.asistencia.modules.academico.repository.AnioEscolarRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.GradoRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.HorarioAsistenciaRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.SeccionRepository;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - HorarioAsistenciaService")
class HorarioAsistenciaServiceTest {

    @Mock
    private HorarioAsistenciaRepository horarioAsistenciaRepository;

    @Mock
    private AnioEscolarRepository anioEscolarRepository;

    @Mock
    private GradoRepository gradoRepository;

    @Mock
    private SeccionRepository seccionRepository;

    private HorarioAsistenciaMapper horarioAsistenciaMapper;
    private HorarioAsistenciaServiceImpl horarioService;

    private AnioEscolar anioEscolar;
    private Grado gradoPrimaria;
    private Grado gradoSecundaria;
    private Seccion seccionA;
    private HorarioAsistencia horarioTurnoGeneral;
    private HorarioAsistencia horarioGrado;
    private HorarioAsistencia horarioSeccion;

    @BeforeEach
    void setUp() {
        horarioAsistenciaMapper = new HorarioAsistenciaMapper();
        horarioService = new HorarioAsistenciaServiceImpl(
                horarioAsistenciaRepository,
                anioEscolarRepository,
                gradoRepository,
                seccionRepository,
                horarioAsistenciaMapper
        );
        anioEscolar = AnioEscolar.builder()
                .id(1L)
                .anio(2026)
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 12, 20))
                .activo(true)
                .build();

        gradoPrimaria = Grado.builder()
                .id(10L)
                .nombre("1° de Primaria")
                .nivel(NivelEducativo.PRIMARIA)
                .activo(true)
                .build();

        gradoSecundaria = Grado.builder()
                .id(20L)
                .nombre("1° de Secundaria")
                .nivel(NivelEducativo.SECUNDARIA)
                .activo(true)
                .build();

        seccionA = Seccion.builder()
                .id(100L)
                .nombre("A")
                .grado(gradoPrimaria)
                .turno(Turno.MANANA)
                .activo(true)
                .build();

        horarioTurnoGeneral = HorarioAsistencia.builder()
                .id(1L)
                .anioEscolar(anioEscolar)
                .turno(Turno.MANANA)
                .grado(null)
                .seccion(null)
                .horaEntrada(LocalTime.of(7, 45))
                .minutosTolerancia(10)
                .horaSalida(LocalTime.of(13, 15))
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 12, 20))
                .activo(true)
                .build();

        horarioGrado = HorarioAsistencia.builder()
                .id(2L)
                .anioEscolar(anioEscolar)
                .turno(Turno.MANANA)
                .grado(gradoPrimaria)
                .seccion(null)
                .horaEntrada(LocalTime.of(7, 40))
                .minutosTolerancia(10)
                .horaSalida(LocalTime.of(13, 0))
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 12, 20))
                .activo(true)
                .build();

        horarioSeccion = HorarioAsistencia.builder()
                .id(3L)
                .anioEscolar(anioEscolar)
                .turno(Turno.MANANA)
                .grado(gradoPrimaria)
                .seccion(seccionA)
                .horaEntrada(LocalTime.of(7, 30))
                .minutosTolerancia(5)
                .horaSalida(LocalTime.of(13, 0))
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 12, 20))
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("1. Horario con sección tiene prioridad máxima sobre grado y turno general")
    void testResolverHorario_PrioridadSeccion() {
        LocalDate fecha = LocalDate.of(2026, 4, 15);
        when(horarioAsistenciaRepository.findActivosPorSeccion(1L, Turno.MANANA, 100L, fecha))
                .thenReturn(List.of(horarioSeccion));

        Optional<HorarioAsistencia> resultado = horarioService.resolverHorario(fecha, 1L, 10L, 100L, Turno.MANANA);

        assertTrue(resultado.isPresent());
        assertEquals(3L, resultado.get().getId());
        assertEquals(seccionA, resultado.get().getSeccion());
    }

    @Test
    @DisplayName("2. Horario de grado es fallback cuando no hay horario específico de sección")
    void testResolverHorario_FallbackGrado() {
        LocalDate fecha = LocalDate.of(2026, 4, 15);
        when(horarioAsistenciaRepository.findActivosPorSeccion(1L, Turno.MANANA, 100L, fecha))
                .thenReturn(Collections.emptyList());
        when(horarioAsistenciaRepository.findActivosPorGrado(1L, Turno.MANANA, 10L, fecha))
                .thenReturn(List.of(horarioGrado));

        Optional<HorarioAsistencia> resultado = horarioService.resolverHorario(fecha, 1L, 10L, 100L, Turno.MANANA);

        assertTrue(resultado.isPresent());
        assertEquals(2L, resultado.get().getId());
        assertEquals(gradoPrimaria, resultado.get().getGrado());
    }

    @Test
    @DisplayName("3. Horario de turno general es fallback cuando no hay horario de grado ni sección")
    void testResolverHorario_FallbackTurno() {
        LocalDate fecha = LocalDate.of(2026, 4, 15);
        when(horarioAsistenciaRepository.findActivosPorSeccion(1L, Turno.MANANA, 100L, fecha))
                .thenReturn(Collections.emptyList());
        when(horarioAsistenciaRepository.findActivosPorGrado(1L, Turno.MANANA, 10L, fecha))
                .thenReturn(Collections.emptyList());
        when(horarioAsistenciaRepository.findActivosPorTurnoGeneral(1L, Turno.MANANA, fecha))
                .thenReturn(List.of(horarioTurnoGeneral));

        Optional<HorarioAsistencia> resultado = horarioService.resolverHorario(fecha, 1L, 10L, 100L, Turno.MANANA);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals(Turno.MANANA, resultado.get().getTurno());
    }

    @Test
    @DisplayName("4. Hora antes del límite resulta en PRESENTE")
    void testDeterminarEstado_AntesDelLimite_Presente() {
        // horaEntrada = 07:45, minutosTolerancia = 10 -> límite 07:55
        LocalTime horaIngreso = LocalTime.of(7, 50);
        EstadoAsistencia estado = horarioService.determinarEstado(horarioTurnoGeneral, horaIngreso);
        assertEquals(EstadoAsistencia.PRESENTE, estado);
    }

    @Test
    @DisplayName("5. Hora exactamente en el límite resulta en PRESENTE")
    void testDeterminarEstado_EnElLimite_Presente() {
        // horaEntrada = 07:45, minutosTolerancia = 10 -> límite exacto 07:55:00
        LocalTime horaIngreso = LocalTime.of(7, 55, 0);
        EstadoAsistencia estado = horarioService.determinarEstado(horarioTurnoGeneral, horaIngreso);
        assertEquals(EstadoAsistencia.PRESENTE, estado);
    }

    @Test
    @DisplayName("6. Hora después del límite resulta en TARDANZA")
    void testDeterminarEstado_DespuesDelLimite_Tardanza() {
        // horaEntrada = 07:45, minutosTolerancia = 10 -> límite 07:55
        LocalTime horaIngreso = LocalTime.of(7, 55, 1);
        EstadoAsistencia estado = horarioService.determinarEstado(horarioTurnoGeneral, horaIngreso);
        assertEquals(EstadoAsistencia.TARDANZA, estado);

        LocalTime horaTarde = LocalTime.of(8, 0);
        assertEquals(EstadoAsistencia.TARDANZA, horarioService.determinarEstado(horarioTurnoGeneral, horaTarde));
    }

    @Test
    @DisplayName("7. Tolerancia 0 minutos funciona: a la hora es PRESENTE, después es TARDANZA")
    void testDeterminarEstado_ToleranciaCero() {
        horarioTurnoGeneral.setMinutosTolerancia(0);
        // horaEntrada = 07:45, límite = 07:45
        assertEquals(EstadoAsistencia.PRESENTE, horarioService.determinarEstado(horarioTurnoGeneral, LocalTime.of(7, 45)));
        assertEquals(EstadoAsistencia.TARDANZA, horarioService.determinarEstado(horarioTurnoGeneral, LocalTime.of(7, 45, 1)));
    }

    @Test
    @DisplayName("10. Horarios solapados incompatibles se rechazan con BusinessException")
    void testCrear_SolapamientoIncompatible_LanzaExcepcion() {
        HorarioAsistenciaRequest req = HorarioAsistenciaRequest.builder()
                .anioEscolarId(1L)
                .turno(Turno.MANANA)
                .horaEntrada(LocalTime.of(7, 45))
                .minutosTolerancia(10)
                .horaSalida(LocalTime.of(13, 0))
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 7, 31))
                .build();

        when(anioEscolarRepository.findById(1L)).thenReturn(Optional.of(anioEscolar));
        when(horarioAsistenciaRepository.findSolapamientos(eq(1L), eq(Turno.MANANA), any(), any(), any(), any(), any()))
                .thenReturn(List.of(horarioTurnoGeneral));

        BusinessException ex = assertThrows(BusinessException.class, () -> horarioService.crearHorario(req));
        assertTrue(ex.getMessage().contains("superpone") || ex.getMessage().contains("solapado"));
    }

    @Test
    @DisplayName("11. Sección que no pertenece al grado seleccionado se rechaza")
    void testCrear_SeccionDeOtroGrado_LanzaExcepcion() {
        HorarioAsistenciaRequest req = HorarioAsistenciaRequest.builder()
                .anioEscolarId(1L)
                .gradoId(20L) // Secundaria
                .seccionId(100L) // Seccion pertenece a Primaria
                .turno(Turno.MANANA)
                .horaEntrada(LocalTime.of(7, 45))
                .minutosTolerancia(10)
                .horaSalida(LocalTime.of(13, 0))
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 7, 31))
                .build();

        when(anioEscolarRepository.findById(1L)).thenReturn(Optional.of(anioEscolar));
        when(gradoRepository.findById(20L)).thenReturn(Optional.of(gradoSecundaria));
        when(seccionRepository.findById(100L)).thenReturn(Optional.of(seccionA));

        BusinessException ex = assertThrows(BusinessException.class, () -> horarioService.crearHorario(req));
        assertTrue(ex.getMessage().contains("no pertenece al grado"));
    }

    @Test
    @DisplayName("12. Fechas de vigencia inválidas (fechaFin < fechaInicio) se rechazan")
    void testCrear_FechasInvalidas_LanzaExcepcion() {
        HorarioAsistenciaRequest req = HorarioAsistenciaRequest.builder()
                .anioEscolarId(1L)
                .turno(Turno.MANANA)
                .horaEntrada(LocalTime.of(7, 45))
                .minutosTolerancia(10)
                .horaSalida(LocalTime.of(13, 0))
                .fechaInicio(LocalDate.of(2026, 8, 1))
                .fechaFin(LocalDate.of(2026, 3, 1)) // Menor que fechaInicio
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> horarioService.crearHorario(req));
        assertTrue(ex.getMessage().contains("vigencia"));
    }

    @Test
    @DisplayName("13. Horas inválidas (horaSalida <= horaEntrada) se rechazan")
    void testCrear_HorasInvalidas_LanzaExcepcion() {
        HorarioAsistenciaRequest req = HorarioAsistenciaRequest.builder()
                .anioEscolarId(1L)
                .turno(Turno.MANANA)
                .horaEntrada(LocalTime.of(13, 0))
                .minutosTolerancia(10)
                .horaSalida(LocalTime.of(7, 45)) // Menor que horaEntrada
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 12, 20))
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> horarioService.crearHorario(req));
        assertTrue(ex.getMessage().contains("hora de salida"));
    }

    @Test
    @DisplayName("14. Si no existe horario configurado devuelve Optional.empty()")
    void testResolverHorario_SinHorarioConfigurado() {
        LocalDate fecha = LocalDate.of(2026, 4, 15);
        when(horarioAsistenciaRepository.findActivosPorSeccion(1L, Turno.MANANA, 100L, fecha))
                .thenReturn(Collections.emptyList());
        when(horarioAsistenciaRepository.findActivosPorGrado(1L, Turno.MANANA, 10L, fecha))
                .thenReturn(Collections.emptyList());
        when(horarioAsistenciaRepository.findActivosPorTurnoGeneral(1L, Turno.MANANA, fecha))
                .thenReturn(Collections.emptyList());

        Optional<HorarioAsistencia> resultado = horarioService.resolverHorario(fecha, 1L, 10L, 100L, Turno.MANANA);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("31. Activar y desactivar horario realiza actualización lógica sin eliminar")
    void testActivarYDesactivar_Logico() {
        when(horarioAsistenciaRepository.findById(1L)).thenReturn(Optional.of(horarioTurnoGeneral));
        when(horarioAsistenciaRepository.save(any(HorarioAsistencia.class))).thenAnswer(i -> i.getArgument(0));

        horarioService.desactivarHorario(1L);
        assertFalse(horarioTurnoGeneral.getActivo());

        horarioService.activarHorario(1L);
        assertTrue(horarioTurnoGeneral.getActivo());
    }
}
