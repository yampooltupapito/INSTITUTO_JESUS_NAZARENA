package com.jesusnazareno.asistencia.modules.asistencia.service;

import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaRequest;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaResponse;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Contrato del servicio para la gestión de asistencia diaria.
 */
public interface AsistenciaService {

    /**
     * Registra la asistencia diaria para una matrícula en una fecha determinada.
     */
    AsistenciaResponse registrar(AsistenciaRequest request);

    /**
     * Busca un registro de asistencia por su identificador único.
     */
    AsistenciaResponse buscarPorId(Long id);

    /**
     * Actualiza de manera controlada el estado, horarios y observación de una asistencia existente.
     */
    AsistenciaResponse actualizar(Long id, AsistenciaRequest request);

    /**
     * Lista todas las asistencias con paginación.
     */
    Page<AsistenciaResponse> listar(Pageable pageable);

    /**
     * Lista las asistencias registradas en una fecha específica con paginación.
     */
    Page<AsistenciaResponse> listarPorFecha(LocalDate fecha, Pageable pageable);

    /**
     * Lista todas las asistencias registradas en una fecha específica.
     */
    List<AsistenciaResponse> listarPorFecha(LocalDate fecha);

    /**
     * Lista las asistencias de una matrícula con paginación.
     */
    Page<AsistenciaResponse> listarPorMatricula(Long matriculaId, Pageable pageable);

    /**
     * Lista todas las asistencias de una matrícula.
     */
    List<AsistenciaResponse> listarPorMatricula(Long matriculaId);

    /**
     * Lista asistencias dentro de un rango de fechas con paginación.
     */
    Page<AsistenciaResponse> listarPorRangoDeFechas(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable);

    /**
     * Lista asistencias de una matrícula específica dentro de un rango de fechas.
     */
    List<AsistenciaResponse> listarPorMatriculaYRangoDeFechas(Long matriculaId, LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Registra un lote de asistencias (ej. para una sección completa) de forma transaccional.
     */
    List<AsistenciaResponse> registrarLote(List<AsistenciaRequest> requests);

    /**
     * Búsqueda avanzada paginada con múltiples filtros ejecutada en BD.
     */
    Page<AsistenciaResponse> buscarAsistencias(
            String q,
            Long anioEscolarId,
            Long gradoId,
            Long seccionId,
            EstadoAsistencia estado,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Pageable pageable
    );

    /**
     * Retorna las matrículas válidas (MATRICULADO) de una sección para el registro de asistencia.
     */
    List<Matricula> listarMatriculasValidasParaRegistro(Long seccionId, Long anioEscolarId);

    /**
     * Busca opcionalmente la asistencia existente de una matrícula en una fecha.
     */
    Optional<AsistenciaResponse> buscarAsistenciaExistente(Long matriculaId, LocalDate fecha);
}

