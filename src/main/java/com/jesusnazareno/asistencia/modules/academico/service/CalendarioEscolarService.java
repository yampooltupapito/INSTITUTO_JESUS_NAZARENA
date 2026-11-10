package com.jesusnazareno.asistencia.modules.academico.service;

import com.jesusnazareno.asistencia.modules.academico.domain.enums.TipoDiaCalendario;
import com.jesusnazareno.asistencia.modules.academico.dto.CalendarioEscolarRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.CalendarioEscolarResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Servicio para la gestión de excepciones y fechas especiales del calendario escolar.
 */
public interface CalendarioEscolarService {

    CalendarioEscolarResponse crear(CalendarioEscolarRequest request);

    CalendarioEscolarResponse actualizar(Long id, CalendarioEscolarRequest request);

    CalendarioEscolarResponse buscarPorId(Long id);

    Page<CalendarioEscolarResponse> listar(Long anioEscolarId, TipoDiaCalendario tipoDia, Boolean esLectivo,
                                           LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable);

    void activar(Long id);

    void desactivar(Long id);

    Optional<CalendarioEscolarResponse> buscarPorFecha(Long anioEscolarId, LocalDate fecha);

    boolean esDiaLectivo(Long anioEscolarId, LocalDate fecha);
}
