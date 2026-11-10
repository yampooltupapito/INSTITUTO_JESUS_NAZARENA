package com.jesusnazareno.asistencia.modules.asistencia.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Asistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Justificacion;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoJustificacion;
import com.jesusnazareno.asistencia.modules.asistencia.dto.JustificacionRequest;
import com.jesusnazareno.asistencia.modules.asistencia.dto.JustificacionResponse;
import com.jesusnazareno.asistencia.modules.asistencia.dto.RevisionJustificacionRequest;
import com.jesusnazareno.asistencia.modules.asistencia.mapper.JustificacionMapper;
import com.jesusnazareno.asistencia.modules.asistencia.repository.AsistenciaRepository;
import com.jesusnazareno.asistencia.modules.asistencia.repository.JustificacionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de la capa de servicio para la gestión de justificaciones de asistencia.
 */
@Service
public class JustificacionServiceImpl implements JustificacionService {

    private final JustificacionRepository justificacionRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final JustificacionMapper justificacionMapper;
    private final com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository usuarioRepository;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;
    private final com.jesusnazareno.asistencia.modules.auditoria.service.AuditoriaService auditoriaService;

    public JustificacionServiceImpl(JustificacionRepository justificacionRepository,
                                    AsistenciaRepository asistenciaRepository,
                                    JustificacionMapper justificacionMapper,
                                    com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository usuarioRepository) {
        this(justificacionRepository, asistenciaRepository, justificacionMapper, usuarioRepository, null, null);
    }

    public JustificacionServiceImpl(JustificacionRepository justificacionRepository,
                                    AsistenciaRepository asistenciaRepository,
                                    JustificacionMapper justificacionMapper,
                                    com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository usuarioRepository,
                                    org.springframework.context.ApplicationEventPublisher eventPublisher) {
        this(justificacionRepository, asistenciaRepository, justificacionMapper, usuarioRepository, eventPublisher, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public JustificacionServiceImpl(JustificacionRepository justificacionRepository,
                                    AsistenciaRepository asistenciaRepository,
                                    JustificacionMapper justificacionMapper,
                                    com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository usuarioRepository,
                                    org.springframework.context.ApplicationEventPublisher eventPublisher,
                                    com.jesusnazareno.asistencia.modules.auditoria.service.AuditoriaService auditoriaService) {
        this.justificacionRepository = justificacionRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.justificacionMapper = justificacionMapper;
        this.usuarioRepository = usuarioRepository;
        this.eventPublisher = eventPublisher;
        this.auditoriaService = auditoriaService;
    }

    @Override
    @Transactional
    public JustificacionResponse crear(JustificacionRequest request) {
        if (request == null) {
            throw new BusinessException("La solicitud de justificación no puede ser nula.");
        }

        // 1. Validar existencia de la Asistencia
        Asistencia asistencia = asistenciaRepository.findById(request.getAsistenciaId())
                .orElseThrow(() -> new ResourceNotFoundException("Registro de asistencia no encontrado con ID: " + request.getAsistenciaId()));

        // 2 y 3. Solo permitir justificar inicialmente AUSENTE o TARDANZA (no PRESENTE ni JUSTIFICADO)
        if (asistencia.getEstado() != EstadoAsistencia.AUSENTE && asistencia.getEstado() != EstadoAsistencia.TARDANZA) {
            throw new BusinessException("Este registro de asistencia no admite justificación.");
        }

        // 4. No permitir duplicar una justificación activa para la misma asistencia
        if (justificacionRepository.existsByAsistenciaIdAndActivoTrue(asistencia.getId())) {
            throw new BusinessException("La asistencia ya tiene una justificación activa.");
        }

        // 5, 6, 7 y 8. Crear entidad con estado PENDIENTE, fecha actual y activo = true
        Justificacion justificacion = justificacionMapper.toEntity(request, asistencia);

        try {
            Justificacion guardada = justificacionRepository.save(justificacion);
            if (eventPublisher != null) {
                eventPublisher.publishEvent(new com.jesusnazareno.asistencia.modules.notificacion.event.NotificacionJustificacionEvent(
                        guardada.getId(), com.jesusnazareno.asistencia.modules.notificacion.domain.enums.TipoNotificacion.JUSTIFICACION_CREADA));
            }
            if (auditoriaService != null) {
                auditoriaService.registrarEventoConContexto(
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.JUSTIFICACION_CREAR,
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.JUSTIFICACIONES,
                        "JUSTIFICACION",
                        guardada.getId(),
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                        "Se registró justificación para asistencia ID: " + guardada.getAsistencia().getId(),
                        null
                );
            }
            return justificacionMapper.toResponse(guardada);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Error de concurrencia o duplicidad al registrar la justificación.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public JustificacionResponse buscarPorId(Long id) {
        if (id == null) {
            throw new ResourceNotFoundException("Identificador de justificación no puede ser nulo.");
        }
        Justificacion justificacion = justificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Justificación no encontrada con ID: " + id));
        return justificacionMapper.toResponse(justificacion);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JustificacionResponse> listar(Pageable pageable) {
        return justificacionRepository.findAll(pageable)
                .map(justificacionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JustificacionResponse> listarPorEstado(EstadoJustificacion estado, Pageable pageable) {
        if (estado == null) {
            throw new BusinessException("El estado de filtro no puede ser nulo.");
        }
        return justificacionRepository.findByEstado(estado, pageable)
                .map(justificacionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JustificacionResponse> buscarJustificaciones(
            String q,
            EstadoJustificacion estado,
            LocalDate fechaSolicitud,
            LocalDate fechaAsistencia,
            Long anioEscolarId,
            Long gradoId,
            Long seccionId,
            Pageable pageable) {
        String cleanQ = (q != null && !q.isBlank()) ? q.trim() : null;
        return justificacionRepository.buscarJustificaciones(
                cleanQ, estado, fechaSolicitud, fechaAsistencia,
                anioEscolarId, gradoId, seccionId, pageable)
                .map(justificacionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JustificacionResponse> listarPorAsistencia(Long asistenciaId) {
        if (asistenciaId == null) {
            throw new ResourceNotFoundException("Identificador de asistencia no puede ser nulo.");
        }
        return justificacionRepository.findByAsistenciaId(asistenciaId).stream()
                .map(justificacionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<JustificacionResponse> buscarActivaPorAsistencia(Long asistenciaId) {
        if (asistenciaId == null) {
            return Optional.empty();
        }
        return justificacionRepository.findByAsistenciaIdAndActivoTrue(asistenciaId)
                .map(justificacionMapper::toResponse);
    }

    @Override
    @Transactional
    public JustificacionResponse aprobar(Long id, RevisionJustificacionRequest revision) {
        if (id == null) {
            throw new ResourceNotFoundException("Identificador de justificación no puede ser nulo.");
        }
        if (revision == null) {
            throw new BusinessException("Los datos de revisión no pueden ser nulos.");
        }

        Justificacion justificacion = justificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Justificación no encontrada con ID: " + id));

        // Transición permitida únicamente desde PENDIENTE
        if (justificacion.getEstado() != EstadoJustificacion.PENDIENTE) {
            throw new BusinessException("La justificación no se encuentra en un estado válido para esta operación.");
        }

        com.jesusnazareno.asistencia.modules.acceso.domain.Usuario revisor = null;
        if (revision.getRevisadoPor() != null) {
            revisor = usuarioRepository.findById(revision.getRevisadoPor())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario revisor no encontrado con ID: " + revision.getRevisadoPor()));
        }

        justificacion.setEstado(EstadoJustificacion.APROBADA);
        justificacion.setRevisadoPor(revisor);
        justificacion.setFechaRevision(LocalDateTime.now(ZoneId.of("America/Lima")));
        if (revision.getObservacionRevision() != null) {
            justificacion.setObservacionRevision(revision.getObservacionRevision().trim());
        }

        // Al aprobar, actualizar la asistencia asociada a JUSTIFICADO
        Asistencia asistencia = justificacion.getAsistencia();
        asistencia.setEstado(EstadoAsistencia.JUSTIFICADO);
        asistenciaRepository.save(asistencia);

        if (eventPublisher != null && asistencia.getMatricula() != null) {
            eventPublisher.publishEvent(new com.jesusnazareno.asistencia.modules.alerta.event.AlertaEvaluacionEvent(asistencia.getMatricula().getId()));
        }

        Justificacion guardada = justificacionRepository.save(justificacion);

        if (eventPublisher != null) {
            eventPublisher.publishEvent(new com.jesusnazareno.asistencia.modules.notificacion.event.NotificacionJustificacionEvent(
                    guardada.getId(), com.jesusnazareno.asistencia.modules.notificacion.domain.enums.TipoNotificacion.JUSTIFICACION_APROBADA));
        }

        if (auditoriaService != null) {
            auditoriaService.registrarEventoConContexto(
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.JUSTIFICACION_APROBAR,
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.JUSTIFICACIONES,
                    "JUSTIFICACION",
                    guardada.getId(),
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                    "Se aprobó la justificación de asistencia ID: " + guardada.getAsistencia().getId(),
                    guardada.getObservacionRevision()
            );
        }

        return justificacionMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public JustificacionResponse rechazar(Long id, RevisionJustificacionRequest revision) {
        if (id == null) {
            throw new ResourceNotFoundException("Identificador de justificación no puede ser nulo.");
        }
        if (revision == null) {
            throw new BusinessException("Los datos de revisión no pueden ser nulos.");
        }

        Justificacion justificacion = justificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Justificación no encontrada con ID: " + id));

        // Transición permitida únicamente desde PENDIENTE
        if (justificacion.getEstado() != EstadoJustificacion.PENDIENTE) {
            throw new BusinessException("La justificación no se encuentra en un estado válido para esta operación.");
        }

        com.jesusnazareno.asistencia.modules.acceso.domain.Usuario revisor = null;
        if (revision.getRevisadoPor() != null) {
            revisor = usuarioRepository.findById(revision.getRevisadoPor())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario revisor no encontrado con ID: " + revision.getRevisadoPor()));
        }

        justificacion.setEstado(EstadoJustificacion.RECHAZADA);
        justificacion.setRevisadoPor(revisor);
        justificacion.setFechaRevision(LocalDateTime.now(ZoneId.of("America/Lima")));
        if (revision.getObservacionRevision() != null) {
            justificacion.setObservacionRevision(revision.getObservacionRevision().trim());
        }

        // Si se rechaza, la asistencia conserva su estado original (AUSENTE o TARDANZA). NO se cambia a JUSTIFICADO.
        Justificacion guardada = justificacionRepository.save(justificacion);

        if (eventPublisher != null) {
            eventPublisher.publishEvent(new com.jesusnazareno.asistencia.modules.notificacion.event.NotificacionJustificacionEvent(
                    guardada.getId(), com.jesusnazareno.asistencia.modules.notificacion.domain.enums.TipoNotificacion.JUSTIFICACION_RECHAZADA));
        }

        if (auditoriaService != null) {
            auditoriaService.registrarEventoConContexto(
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.JUSTIFICACION_RECHAZAR,
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.JUSTIFICACIONES,
                    "JUSTIFICACION",
                    guardada.getId(),
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                    "Se rechazó la justificación de asistencia ID: " + guardada.getAsistencia().getId(),
                    guardada.getObservacionRevision()
            );
        }

        return justificacionMapper.toResponse(guardada);
    }
}
