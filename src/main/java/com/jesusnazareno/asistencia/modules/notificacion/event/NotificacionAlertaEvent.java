package com.jesusnazareno.asistencia.modules.notificacion.event;

/**
 * Evento interno para la generación de notificaciones por una alerta de asistencia.
 */
public class NotificacionAlertaEvent {

    private final Long alertaId;

    public NotificacionAlertaEvent(Long alertaId) {
        this.alertaId = alertaId;
    }

    public Long getAlertaId() {
        return alertaId;
    }
}
