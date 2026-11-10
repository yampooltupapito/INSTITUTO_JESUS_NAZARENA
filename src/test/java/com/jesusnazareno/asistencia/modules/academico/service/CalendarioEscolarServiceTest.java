package com.jesusnazareno.asistencia.modules.academico.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.CalendarioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.TipoDiaCalendario;
import com.jesusnazareno.asistencia.modules.academico.dto.CalendarioEscolarRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.CalendarioEscolarResponse;
import com.jesusnazareno.asistencia.modules.academico.mapper.CalendarioEscolarMapper;
import com.jesusnazareno.asistencia.modules.academico.repository.AnioEscolarRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.CalendarioEscolarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - CalendarioEscolarService")
class CalendarioEscolarServiceTest {

    @Mock
    private CalendarioEscolarRepository calendarioEscolarRepository;

    @Mock
    private AnioEscolarRepository anioEscolarRepository;

    private CalendarioEscolarMapper calendarioEscolarMapper;
    private CalendarioEscolarServiceImpl calendarioEscolarService;

    private AnioEscolar anioEscolar2026;

    @BeforeEach
    void setUp() {
        calendarioEscolarMapper = new CalendarioEscolarMapper();
        calendarioEscolarService = new CalendarioEscolarServiceImpl(
                calendarioEscolarRepository,
                anioEscolarRepository,
                calendarioEscolarMapper
        );
        anioEscolar2026 = AnioEscolar.builder()
                .id(1L)
                .anio(2026)
                .nombre("Año Escolar 2026")
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 12, 20))
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("01. Lunes a viernes dentro del año sin excepción -> día lectivo")
    void esDiaLectivo_lunesAViernesSinExcepcion_esLectivo() {
        // 2026-03-09 es lunes
        LocalDate lunes = LocalDate.of(2026, 3, 9);
        when(anioEscolarRepository.findById(1L)).thenReturn(Optional.of(anioEscolar2026));
        when(calendarioEscolarRepository.findByAnioEscolarIdAndFechaAndActivoTrue(1L, lunes))
                .thenReturn(Optional.empty());

        boolean resultado = calendarioEscolarService.esDiaLectivo(1L, lunes);
        assertTrue(resultado);
    }

    @Test
    @DisplayName("02. Sábado sin excepción -> no lectivo")
    void esDiaLectivo_sabadoSinExcepcion_noLectivo() {
        // 2026-03-14 es sábado
        LocalDate sabado = LocalDate.of(2026, 3, 14);
        when(anioEscolarRepository.findById(1L)).thenReturn(Optional.of(anioEscolar2026));
        when(calendarioEscolarRepository.findByAnioEscolarIdAndFechaAndActivoTrue(1L, sabado))
                .thenReturn(Optional.empty());

        boolean resultado = calendarioEscolarService.esDiaLectivo(1L, sabado);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("03. Domingo sin excepción -> no lectivo")
    void esDiaLectivo_domingoSinExcepcion_noLectivo() {
        // 2026-03-15 es domingo
        LocalDate domingo = LocalDate.of(2026, 3, 15);
        when(anioEscolarRepository.findById(1L)).thenReturn(Optional.of(anioEscolar2026));
        when(calendarioEscolarRepository.findByAnioEscolarIdAndFechaAndActivoTrue(1L, domingo))
                .thenReturn(Optional.empty());

        boolean resultado = calendarioEscolarService.esDiaLectivo(1L, domingo);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("04. Feriado con esLectivo=false -> no lectivo")
    void esDiaLectivo_feriadoNoLectivo_retornaFalse() {
        // 2026-05-01 es viernes (ordinariamente lectivo), pero es FERIADO
        LocalDate fecha = LocalDate.of(2026, 5, 1);
        CalendarioEscolar feriado = CalendarioEscolar.builder()
                .id(10L)
                .anioEscolar(anioEscolar2026)
                .fecha(fecha)
                .tipoDia(TipoDiaCalendario.FERIADO)
                .descripcion("Día del Trabajo")
                .esLectivo(false)
                .activo(true)
                .build();

        when(anioEscolarRepository.findById(1L)).thenReturn(Optional.of(anioEscolar2026));
        when(calendarioEscolarRepository.findByAnioEscolarIdAndFechaAndActivoTrue(1L, fecha))
                .thenReturn(Optional.of(feriado));

        boolean resultado = calendarioEscolarService.esDiaLectivo(1L, fecha);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("05. Suspensión de clases -> no lectivo")
    void esDiaLectivo_suspension_retornaFalse() {
        // 2026-06-10 es miércoles
        LocalDate fecha = LocalDate.of(2026, 6, 10);
        CalendarioEscolar suspension = CalendarioEscolar.builder()
                .id(11L)
                .anioEscolar(anioEscolar2026)
                .fecha(fecha)
                .tipoDia(TipoDiaCalendario.SUSPENSION)
                .descripcion("Mantenimiento de infraestructura")
                .esLectivo(false)
                .activo(true)
                .build();

        when(anioEscolarRepository.findById(1L)).thenReturn(Optional.of(anioEscolar2026));
        when(calendarioEscolarRepository.findByAnioEscolarIdAndFechaAndActivoTrue(1L, fecha))
                .thenReturn(Optional.of(suspension));

        boolean resultado = calendarioEscolarService.esDiaLectivo(1L, fecha);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("06. Lectivo extraordinario en sábado -> lectivo")
    void esDiaLectivo_sabadoLectivoExtraordinario_retornaTrue() {
        // 2026-10-10 es sábado (ordinariamente no lectivo), con LECTIVO_EXTRAORDINARIO y esLectivo=true
        LocalDate sabado = LocalDate.of(2026, 10, 10);
        CalendarioEscolar extraordinario = CalendarioEscolar.builder()
                .id(12L)
                .anioEscolar(anioEscolar2026)
                .fecha(sabado)
                .tipoDia(TipoDiaCalendario.LECTIVO_EXTRAORDINARIO)
                .descripcion("Recuperación de clases por sismo")
                .esLectivo(true)
                .activo(true)
                .build();

        when(anioEscolarRepository.findById(1L)).thenReturn(Optional.of(anioEscolar2026));
        when(calendarioEscolarRepository.findByAnioEscolarIdAndFechaAndActivoTrue(1L, sabado))
                .thenReturn(Optional.of(extraordinario));

        boolean resultado = calendarioEscolarService.esDiaLectivo(1L, sabado);
        assertTrue(resultado);
    }

    @Test
    @DisplayName("07. Excepción activa tiene prioridad sobre regla semanal ordinaria")
    void esDiaLectivo_excepcionActivaTienePrioridad() {
        // Martes 2026-07-28: entre semana, pero feriado activo
        LocalDate fecha = LocalDate.of(2026, 7, 28);
        CalendarioEscolar feriado = CalendarioEscolar.builder()
                .id(13L)
                .anioEscolar(anioEscolar2026)
                .fecha(fecha)
                .tipoDia(TipoDiaCalendario.FERIADO)
                .descripcion("Fiestas Patrias")
                .esLectivo(false)
                .activo(true)
                .build();

        when(anioEscolarRepository.findById(1L)).thenReturn(Optional.of(anioEscolar2026));
        when(calendarioEscolarRepository.findByAnioEscolarIdAndFechaAndActivoTrue(1L, fecha))
                .thenReturn(Optional.of(feriado));

        boolean resultado = calendarioEscolarService.esDiaLectivo(1L, fecha);
        assertFalse(resultado);
    }

    @Test
    @DisplayName("08. Fecha fuera del periodo del año escolar es rechazada al registrar")
    void crear_fechaFueraDelPeriodo_lanzaBusinessException() {
        // Año escolar: 2026-03-01 a 2026-12-20. Fecha: 2030-01-01
        CalendarioEscolarRequest request = CalendarioEscolarRequest.builder()
                .anioEscolarId(1L)
                .fecha(LocalDate.of(2030, 1, 1))
                .tipoDia(TipoDiaCalendario.FERIADO)
                .descripcion("Fecha futura")
                .esLectivo(false)
                .build();

        when(anioEscolarRepository.findById(1L)).thenReturn(Optional.of(anioEscolar2026));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                calendarioEscolarService.crear(request)
        );
        assertEquals("La fecha no pertenece al periodo del año escolar.", ex.getMessage());
    }

    @Test
    @DisplayName("08b. Fecha fuera del periodo en esDiaLectivo devuelve false directamente")
    void esDiaLectivo_fechaFueraDelPeriodo_devuelveFalse() {
        when(anioEscolarRepository.findById(1L)).thenReturn(Optional.of(anioEscolar2026));
        boolean resultado = calendarioEscolarService.esDiaLectivo(1L, LocalDate.of(2025, 1, 10));
        assertFalse(resultado);
    }

    @Test
    @DisplayName("09. Configuración activa duplicada para misma fecha y año es rechazada")
    void crear_configuracionActivaDuplicada_lanzaBusinessException() {
        LocalDate fecha = LocalDate.of(2026, 5, 1);
        CalendarioEscolarRequest request = CalendarioEscolarRequest.builder()
                .anioEscolarId(1L)
                .fecha(fecha)
                .tipoDia(TipoDiaCalendario.FERIADO)
                .descripcion("Día del Trabajo duplicado")
                .esLectivo(false)
                .build();

        when(anioEscolarRepository.findById(1L)).thenReturn(Optional.of(anioEscolar2026));
        when(calendarioEscolarRepository.existsByAnioEscolarIdAndFechaAndActivoTrue(1L, fecha))
                .thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                calendarioEscolarService.crear(request)
        );
        assertEquals("Ya existe una configuración activa para esta fecha.", ex.getMessage());
    }

    @Test
    @DisplayName("20. Filtros y paginación funcionan correctamente")
    void listar_conFiltrosYPaginacion_retornaPagina() {
        Pageable pageable = PageRequest.of(0, 10);
        CalendarioEscolar c = CalendarioEscolar.builder()
                .id(1L)
                .anioEscolar(anioEscolar2026)
                .fecha(LocalDate.of(2026, 5, 1))
                .tipoDia(TipoDiaCalendario.FERIADO)
                .descripcion("Feriado")
                .esLectivo(false)
                .activo(true)
                .build();

        when(calendarioEscolarRepository.buscarConFiltros(1L, TipoDiaCalendario.FERIADO, false, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(c), pageable, 1));

        Page<CalendarioEscolarResponse> result = calendarioEscolarService.listar(
                1L, TipoDiaCalendario.FERIADO, false, null, null, pageable
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Feriado", result.getContent().get(0).getTipoDiaDescripcion());
    }

    @Test
    @DisplayName("22. Desactivar y activar preservan histórico sin borrado físico")
    void desactivarYActivar_preservanRegistro() {
        CalendarioEscolar c = CalendarioEscolar.builder()
                .id(5L)
                .anioEscolar(anioEscolar2026)
                .fecha(LocalDate.of(2026, 6, 24))
                .tipoDia(TipoDiaCalendario.ACTIVIDAD_INSTITUCIONAL)
                .descripcion("Día del Campesino")
                .esLectivo(false)
                .activo(true)
                .build();

        when(calendarioEscolarRepository.findById(5L)).thenReturn(Optional.of(c));

        calendarioEscolarService.desactivar(5L);
        assertFalse(c.getActivo());
        verify(calendarioEscolarRepository).save(c);

        when(calendarioEscolarRepository.existsByAnioEscolarIdAndFechaAndActivoTrueAndIdNot(1L, LocalDate.of(2026, 6, 24), 5L))
                .thenReturn(false);

        calendarioEscolarService.activar(5L);
        assertTrue(c.getActivo());
        verify(calendarioEscolarRepository, times(2)).save(c);
    }
}
