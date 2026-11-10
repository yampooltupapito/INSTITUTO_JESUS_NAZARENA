package com.jesusnazareno.asistencia.modules.notificacion.domain.enums;

/**
 * Tipos de notificaciones internas del sistema.
 */
public enum TipoNotificacion {

    ALERTA_ASISTENCIA("Alerta de Asistencia"),
    JUSTIFICACION_CREADA("Justificación Registrada"),
    JUSTIFICACION_APROBADA("Justificación Aprobada"),
    JUSTIFICACION_RECHAZADA("Justificación Rechazada"),
    SISTEMA("Aviso del Sistema");

    private final String descripcion;

    TipoNotificacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
