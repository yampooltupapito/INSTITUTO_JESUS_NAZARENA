package com.jesusnazareno.asistencia.modules.alerta.service;

import com.jesusnazareno.asistencia.modules.alerta.domain.enums.EstadoAlerta;
import com.jesusnazareno.asistencia.modules.alerta.domain.enums.TipoAlertaAsistencia;
import com.jesusnazareno.asistencia.modules.alerta.dto.AlertaAsistenciaResponse;
import com.jesusnazareno.asistencia.modules.alerta.dto.ConfiguracionAlertaRequest;
import com.jesusnazareno.asistencia.modules.alerta.dto.ConfiguracionAlertaResponse;
import com.jesusnazareno.asistencia.modules.alerta.dto.ResolucionAlertaRequest;
import com.jesusnazareno.asistencia.modules.alerta.dto.RevisionAlertaRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrato de servicios para el módulo de alertas internas de asistencia.
 */
public interface AlertaAsistenciaService {

    /**
     * Evalúa las asistencias de una matrícula y genera/actualiza alertas según los umbrales configurados.
     */
    void evaluarMatricula(Long matriculaId);

    /**
     * Recalcula las alertas para todas las matrículas activas del año escolar vigente.
     */
    void recalcularTodasLasAlertas();

    /**
     * Búsqueda avanzada y paginada de alertas internas con filtros.
     */
    Page<AlertaAsistenciaResponse> buscarAlertas(
            String q,
            TipoAlertaAsistencia tipo,
            EstadoAlerta estado,
            Long anioEscolarId,
            Long gradoId,
            Long seccionId,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Pageable pageable
    );

    /**
     * Obtiene el detalle de una alerta por su ID.
     */
    AlertaAsistenciaResponse buscarPorId(Long id);

    /**
     * Pone una alerta en estado EN_REVISION por parte de un usuario revisor.
     */
    AlertaAsistenciaResponse marcarEnRevision(Long id, RevisionAlertaRequest request, Long usuarioId);

    /**
     * Resuelve definitivamente una alerta con observación obligatoria.
     */
    AlertaAsistenciaResponse resolver(Long id, ResolucionAlertaRequest request, Long usuarioId);

    /**
     * Obtiene el conteo en tiempo real de alertas en estado PENDIENTE.
     */
    long contarAlertasPendientes();

    /**
     * Lista todas las configuraciones de umbrales de alerta.
     */
    List<ConfiguracionAlertaResponse> listarConfiguraciones();

    /**
     * Actualiza el umbral de una configuración de alerta.
     */
    ConfiguracionAlertaResponse actualizarConfiguracion(ConfiguracionAlertaRequest request);

    /**
     * Obtiene la configuración activa por tipo.
     */
    ConfiguracionAlertaResponse buscarConfiguracionPorTipo(TipoAlertaAsistencia tipo);
}
