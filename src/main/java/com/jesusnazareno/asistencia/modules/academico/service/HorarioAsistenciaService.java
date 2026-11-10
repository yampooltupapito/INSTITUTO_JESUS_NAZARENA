package com.jesusnazareno.asistencia.modules.academico.service;

import com.jesusnazareno.asistencia.modules.academico.domain.HorarioAsistencia;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import com.jesusnazareno.asistencia.modules.academico.dto.HorarioAsistenciaRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.HorarioAsistenciaResponse;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la administración y resolución de horarios escolares de asistencia.
 */
public interface HorarioAsistenciaService {

    HorarioAsistenciaResponse crearHorario(HorarioAsistenciaRequest request);

    HorarioAsistenciaResponse buscarPorId(Long id);

    List<HorarioAsistenciaResponse> listarTodos();

    Page<HorarioAsistenciaResponse> listarPaginado(Long anioEscolarId, Turno turno, String estado, Pageable pageable);

    HorarioAsistenciaResponse actualizarHorario(Long id, HorarioAsistenciaRequest request);

    HorarioAsistenciaResponse activarHorario(Long id);

    void desactivarHorario(Long id);

    /**
     * Resuelve el horario escolar aplicable respetando la prioridad:
     * 1. Sección específica
     * 2. Grado específico
     * 3. Turno general del año escolar
     */
    Optional<HorarioAsistencia> resolverHorario(LocalDate fecha, Long anioEscolarId, Long gradoId, Long seccionId, Turno turno);

    /**
     * Determina si la llegada es PRESENTE o TARDANZA según hora de entrada + tolerancia.
     */
    EstadoAsistencia determinarEstado(HorarioAsistencia horario, LocalTime horaIngreso);

    LocalTime calcularHoraLimite(HorarioAsistencia horario);
}
