package com.jesusnazareno.asistencia.modules.asistencia.service;

import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoJustificacion;
import com.jesusnazareno.asistencia.modules.asistencia.dto.JustificacionRequest;
import com.jesusnazareno.asistencia.modules.asistencia.dto.JustificacionResponse;
import com.jesusnazareno.asistencia.modules.asistencia.dto.RevisionJustificacionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de servicio para la gestión de justificaciones de asistencia.
 */
public interface JustificacionService {

    /**
     * Registra una nueva solicitud de justificación en estado PENDIENTE.
     *
     * @param request Datos de la justificación
     * @return Justificación creada
     */
    JustificacionResponse crear(JustificacionRequest request);

    /**
     * Busca una justificación por su identificador único.
     *
     * @param id Identificador de la justificación
     * @return Justificación encontrada
     */
    JustificacionResponse buscarPorId(Long id);

    /**
     * Lista todas las justificaciones con paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de justificaciones
     */
    Page<JustificacionResponse> listar(Pageable pageable);

    /**
     * Lista justificaciones filtradas por su estado con paginación.
     *
     * @param estado Estado de la justificación (PENDIENTE, APROBADA, RECHAZADA)
     * @param pageable Configuración de paginación
     * @return Página de justificaciones
     */
    Page<JustificacionResponse> listarPorEstado(EstadoJustificacion estado, Pageable pageable);

    /**
     * Búsqueda avanzada paginada de justificaciones con filtros en base de datos.
     *
     * @param q Texto de búsqueda por documento o nombre de estudiante
     * @param estado Estado de la justificación
     * @param fechaSolicitud Fecha de registro de solicitud
     * @param fechaAsistencia Fecha de la asistencia justificada
     * @param anioEscolarId Identificador del año escolar
     * @param gradoId Identificador del grado
     * @param seccionId Identificador de la sección
     * @param pageable Configuración de paginación
     * @return Página de resultados
     */
    Page<JustificacionResponse> buscarJustificaciones(
            String q,
            EstadoJustificacion estado,
            LocalDate fechaSolicitud,
            LocalDate fechaAsistencia,
            Long anioEscolarId,
            Long gradoId,
            Long seccionId,
            Pageable pageable);

    /**
     * Lista todas las justificaciones asociadas a un registro de asistencia.
     *
     * @param asistenciaId Identificador de la asistencia
     * @return Lista de justificaciones
     */
    List<JustificacionResponse> listarPorAsistencia(Long asistenciaId);

    /**
     * Busca la justificación activa asociada a una asistencia si existe.
     *
     * @param asistenciaId Identificador de la asistencia
     * @return Justificación activa opcional
     */
    Optional<JustificacionResponse> buscarActivaPorAsistencia(Long asistenciaId);

    /**
     * Aprueba una justificación en estado PENDIENTE y actualiza transaccionalmente
     * el estado de la asistencia asociada a JUSTIFICADO.
     *
     * @param id Identificador de la justificación
     * @param revision Datos de la revisión
     * @return Justificación aprobada
     */
    JustificacionResponse aprobar(Long id, RevisionJustificacionRequest revision);

    /**
     * Rechaza una justificación en estado PENDIENTE, conservando el estado original
     * de la asistencia (AUSENTE o TARDANZA).
     *
     * @param id Identificador de la justificación
     * @param revision Datos de la revisión
     * @return Justificación rechazada
     */
    JustificacionResponse rechazar(Long id, RevisionJustificacionRequest revision);
}
