package com.jesusnazareno.asistencia.modules.notificacion.event;

import com.jesusnazareno.asistencia.modules.notificacion.domain.enums.TipoNotificacion;

/**
 * Evento interno para la generación de notificaciones por una justificación (creada, aprobada, rechazada).
 */
public class NotificacionJustificacionEvent {

    private final Long justificacionId;
    private final TipoNotificacion tipo;

    public NotificacionJustificacionEvent(Long justificacionId, TipoNotificacion tipo) {
        this.justificacionId = justificacionId;
        this.tipo = tipo;
    }

    public Long getJustificacionId() {
        return justificacionId;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }
}
