package com.jesusnazareno.asistencia.modules.notificacion.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.alerta.domain.AlertaAsistencia;
import com.jesusnazareno.asistencia.modules.alerta.repository.AlertaAsistenciaRepository;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Justificacion;
import com.jesusnazareno.asistencia.modules.asistencia.repository.JustificacionRepository;
import com.jesusnazareno.asistencia.modules.notificacion.domain.Notificacion;
import com.jesusnazareno.asistencia.modules.notificacion.domain.enums.TipoNotificacion;
import com.jesusnazareno.asistencia.modules.notificacion.dto.NotificacionResponse;
import com.jesusnazareno.asistencia.modules.notificacion.event.NotificacionAlertaEvent;
import com.jesusnazareno.asistencia.modules.notificacion.event.NotificacionJustificacionEvent;
import com.jesusnazareno.asistencia.modules.notificacion.mapper.NotificacionMapper;
import com.jesusnazareno.asistencia.modules.notificacion.repository.NotificacionRepository;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de notificaciones internas.
 */
@Service
public class NotificacionServiceImpl implements NotificacionService {

    private static final ZoneId ZONE_LIMA = ZoneId.of("America/Lima");

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlertaAsistenciaRepository alertaRepository;
    private final JustificacionRepository justificacionRepository;
    private final NotificacionMapper notificacionMapper;

    public NotificacionServiceImpl(
            NotificacionRepository notificacionRepository,
            UsuarioRepository usuarioRepository,
            AlertaAsistenciaRepository alertaRepository,
            JustificacionRepository justificacionRepository,
            NotificacionMapper notificacionMapper) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.alertaRepository = alertaRepository;
        this.justificacionRepository = justificacionRepository;
        this.notificacionMapper = notificacionMapper;
    }

    @EventListener
    public void onNotificacionAlertaEvent(NotificacionAlertaEvent event) {
        if (event != null && event.getAlertaId() != null) {
            try {
                crearNotificacionAlerta(event.getAlertaId());
            } catch (Exception ex) {
                // Las notificaciones secundarias nunca deben abortar la transacción principal
            }
        }
    }

    @EventListener
    public void onNotificacionJustificacionEvent(NotificacionJustificacionEvent event) {
        if (event != null && event.getJustificacionId() != null && event.getTipo() != null) {
            try {
                crearNotificacionJustificacion(event.getJustificacionId(), event.getTipo());
            } catch (Exception ex) {
                // Las notificaciones secundarias nunca deben abortar la transacción principal
            }
        }
    }

    @Override
    @Transactional
    public NotificacionResponse crearNotificacion(
            Usuario usuario,
            TipoNotificacion tipo,
            String titulo,
            String mensaje,
            String referenciaTipo,
            Long referenciaId,
            String urlDestino) {

        if (usuario == null || usuario.getId() == null) {
            throw new BusinessException("El destinatario de la notificación es obligatorio.");
        }
        if (tipo == null) {
            throw new BusinessException("El tipo de notificación es obligatorio.");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new BusinessException("El título de la notificación es obligatorio.");
        }
        if (mensaje == null || mensaje.isBlank()) {
            throw new BusinessException("El mensaje de la notificación es obligatorio.");
        }

        // 1. Deduplicación por evento equivalente
        if (referenciaTipo != null && referenciaId != null) {
            boolean existe = notificacionRepository.existsByTipoAndReferenciaTipoAndReferenciaIdAndUsuarioIdAndActivoTrue(
                    tipo, referenciaTipo, referenciaId, usuario.getId());
            if (existe) {
                return null;
            }
        }

        // 2. Sanitización y validación de URL interna
        String urlSegura = sanitizarUrlInterna(urlDestino);

        Notificacion notificacion = Notificacion.builder()
                .usuario(usuario)
                .tipo(tipo)
                .titulo(titulo.trim())
                .mensaje(mensaje.trim())
                .referenciaTipo(referenciaTipo)
                .referenciaId(referenciaId)
                .urlDestino(urlSegura)
                .leida(false)
                .fechaLectura(null)
                .activo(true)
                .build();

        Notificacion guardada = notificacionRepository.save(notificacion);
        return notificacionMapper.toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificacionResponse> listarDelUsuario(
            Long usuarioId,
            Boolean leida,
            TipoNotificacion tipo,
            Pageable pageable) {

        if (usuarioId == null) {
            throw new BusinessException("Identificador de usuario no puede ser nulo.");
        }
        return notificacionRepository.buscarNotificacionesUsuario(usuarioId, leida, tipo, pageable)
                .map(notificacionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarNoLeidas(Long usuarioId) {
        if (usuarioId == null) {
            return 0L;
        }
        return notificacionRepository.countByUsuarioIdAndLeidaFalseAndActivoTrue(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponse> obtenerUltimasNoLeidas(Long usuarioId, int limite) {
        if (usuarioId == null) {
            return List.of();
        }
        return notificacionRepository.findTop5ByUsuarioIdAndLeidaFalseAndActivoTrueOrderByCreatedAtDesc(usuarioId)
                .stream()
                .map(notificacionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponse> obtenerUltimasDelUsuario(Long usuarioId, int limite) {
        if (usuarioId == null) {
            return List.of();
        }
        return notificacionRepository.findTop5ByUsuarioIdAndActivoTrueOrderByCreatedAtDesc(usuarioId)
                .stream()
                .map(notificacionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public NotificacionResponse buscarDelUsuario(Long id, Long usuarioId) {
        if (id == null) {
            throw new ResourceNotFoundException("Identificador de notificación no puede ser nulo.");
        }
        if (usuarioId == null) {
            throw new BusinessException("Usuario no autenticado.");
        }

        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con ID: " + id));

        // Seguridad estricta por propietario
        if (!notificacion.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("No tiene acceso a esta notificación.");
        }
        if (!Boolean.TRUE.equals(notificacion.getActivo())) {
            throw new ResourceNotFoundException("Notificación no encontrada con ID: " + id);
        }

        return notificacionMapper.toResponse(notificacion);
    }

    @Override
    @Transactional
    public NotificacionResponse marcarComoLeida(Long id, Long usuarioId) {
        if (id == null) {
            throw new ResourceNotFoundException("Identificador de notificación no puede ser nulo.");
        }
        if (usuarioId == null) {
            throw new BusinessException("Usuario no autenticado.");
        }

        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con ID: " + id));

        // Seguridad estricta por propietario
        if (!notificacion.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("No tiene acceso a esta notificación.");
        }
        if (!Boolean.TRUE.equals(notificacion.getActivo())) {
            throw new ResourceNotFoundException("Notificación no encontrada con ID: " + id);
        }

        // Idempotencia
        if (Boolean.TRUE.equals(notificacion.getLeida())) {
            return notificacionMapper.toResponse(notificacion);
        }

        notificacion.setLeida(true);
        notificacion.setFechaLectura(LocalDateTime.now(ZONE_LIMA));
        Notificacion guardada = notificacionRepository.save(notificacion);
        return notificacionMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public int marcarTodasComoLeidas(Long usuarioId) {
        if (usuarioId == null) {
            throw new BusinessException("Usuario no autenticado.");
        }
        return notificacionRepository.marcarTodasComoLeidasUsuario(usuarioId, LocalDateTime.now(ZONE_LIMA));
    }

    @Override
    @Transactional
    public void crearNotificacionAlerta(Long alertaId) {
        if (alertaId == null) {
            return;
        }

        Optional<AlertaAsistencia> alertaOpt = alertaRepository.findById(alertaId);
        if (alertaOpt.isEmpty()) {
            return;
        }

        AlertaAsistencia alerta = alertaOpt.get();
        String estudianteNombre = "Estudiante";
        String gradoSeccion = "";
        if (alerta.getMatricula() != null) {
            if (alerta.getMatricula().getEstudiante() != null) {
                Persona est = alerta.getMatricula().getEstudiante();
                estudianteNombre = obtenerNombreCompleto(est);
            }
            if (alerta.getMatricula().getGrado() != null && alerta.getMatricula().getSeccion() != null) {
                gradoSeccion = " (" + alerta.getMatricula().getGrado().getNombre() + " " +
                        alerta.getMatricula().getSeccion().getNombre() + ")";
            }
        }

        String titulo = "Nueva Alerta: " + alerta.getTipo().getDescripcion();
        String mensaje = String.format("%s%s ha alcanzado el criterio de %s (%d incidencias).",
                estudianteNombre, gradoSeccion, alerta.getTipo().getDescripcion().toLowerCase(), alerta.getCantidad());
        String urlDestino = "/alertas/" + alerta.getId();

        // Destinatarios autorizados: ADMIN, DIRECTOR, AUXILIAR
        List<Usuario> destinatarios = usuarioRepository.findByRolCodigoInAndActivoTrue(
                List.of("ADMIN", "DIRECTOR", "AUXILIAR"));

        for (Usuario u : destinatarios) {
            crearNotificacion(u, TipoNotificacion.ALERTA_ASISTENCIA, titulo, mensaje,
                    "ALERTA_ASISTENCIA", alerta.getId(), urlDestino);
        }
    }

    @Override
    @Transactional
    public void crearNotificacionJustificacion(Long justificacionId, TipoNotificacion tipo) {
        if (justificacionId == null || tipo == null) {
            return;
        }

        Optional<Justificacion> justOpt = justificacionRepository.findById(justificacionId);
        if (justOpt.isEmpty()) {
            return;
        }

        Justificacion justificacion = justOpt.get();
        String estudianteNombre = "Estudiante";
        String fechaAsistencia = "";
        Long estudianteId = null;

        if (justificacion.getAsistencia() != null) {
            fechaAsistencia = justificacion.getAsistencia().getFecha() != null
                    ? justificacion.getAsistencia().getFecha().toString()
                    : "";
            if (justificacion.getAsistencia().getMatricula() != null &&
                justificacion.getAsistencia().getMatricula().getEstudiante() != null) {
                Persona est = justificacion.getAsistencia().getMatricula().getEstudiante();
                estudianteNombre = obtenerNombreCompleto(est);
                estudianteId = est.getId();
            }
        }

        String urlDestino = "/justificaciones/" + justificacion.getId();

        if (tipo == TipoNotificacion.JUSTIFICACION_CREADA) {
            // Notificar a usuarios autorizados para revisar justificaciones: ADMIN, DIRECTOR
            String titulo = "Nueva Justificación Registrada";
            String mensaje = String.format("Se registró una justificación para %s (Fecha: %s) por motivo: %s.",
                    estudianteNombre, fechaAsistencia, justificacion.getMotivo());

            List<Usuario> revisores = usuarioRepository.findByRolCodigoInAndActivoTrue(
                    List.of("ADMIN", "DIRECTOR"));

            for (Usuario r : revisores) {
                crearNotificacion(r, TipoNotificacion.JUSTIFICACION_CREADA, titulo, mensaje,
                        "JUSTIFICACION", justificacion.getId(), urlDestino);
            }

        } else if (tipo == TipoNotificacion.JUSTIFICACION_APROBADA || tipo == TipoNotificacion.JUSTIFICACION_RECHAZADA) {
            // Notificar al usuario creador o asociado
            boolean esAprobada = (tipo == TipoNotificacion.JUSTIFICACION_APROBADA);
            String accion = esAprobada ? "aprobada" : "rechazada";
            String titulo = esAprobada ? "Justificación Aprobada" : "Justificación Rechazada";
            String observacion = justificacion.getObservacionRevision() != null && !justificacion.getObservacionRevision().isBlank()
                    ? " Observación: " + justificacion.getObservacionRevision()
                    : "";
            String mensaje = String.format("La justificación de %s del %s fue %s.%s",
                    estudianteNombre, fechaAsistencia, accion, observacion);

            // 1. Buscar usuario creador si existe
            Usuario destinatario = null;
            if (justificacion.getCreatedBy() != null) {
                destinatario = usuarioRepository.findByUsername(justificacion.getCreatedBy()).orElse(null);
            }
            // 2. Si no, buscar usuario del estudiante si existe cuenta
            if (destinatario == null && estudianteId != null) {
                destinatario = usuarioRepository.findByPersonaId(estudianteId).orElse(null);
            }

            if (destinatario != null && Boolean.TRUE.equals(destinatario.getActivo())) {
                crearNotificacion(destinatario, tipo, titulo, mensaje,
                        "JUSTIFICACION", justificacion.getId(), urlDestino);
            }
        }
    }

    private String sanitizarUrlInterna(String url) {
        if (url == null || url.isBlank()) {
            return "/";
        }
        String clean = url.trim();
        // Solo permitir URLs relativas internas que comiencen con '/' y no contengan esquemas ni '//'
        if (clean.startsWith("/") && !clean.startsWith("//") && !clean.contains("://")) {
            return clean;
        }
        return "/";
    }

    private String obtenerNombreCompleto(Persona persona) {
        if (persona == null) {
            return "Estudiante";
        }
        String pat = persona.getApellidoPaterno() != null ? persona.getApellidoPaterno().trim() : "";
        String mat = persona.getApellidoMaterno() != null ? persona.getApellidoMaterno().trim() : "";
        String nom = persona.getNombres() != null ? persona.getNombres().trim() : "";
        String apellidos = (pat + " " + mat).trim();
        if (!apellidos.isEmpty() && !nom.isEmpty()) {
            return apellidos + ", " + nom;
        } else if (!apellidos.isEmpty()) {
            return apellidos;
        } else if (!nom.isEmpty()) {
            return nom;
        }
        return "Estudiante";
    }
}

