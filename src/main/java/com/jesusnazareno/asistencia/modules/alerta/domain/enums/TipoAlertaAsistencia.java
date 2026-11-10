package com.jesusnazareno.asistencia.modules.alerta.domain.enums;

/**
 * Enumeración para los tipos de alertas internas de asistencia.
 * Coincide exactamente con el CHECK constraint en PostgreSQL.
 */
public enum TipoAlertaAsistencia {
    AUSENCIAS_CONSECUTIVAS("Ausencias Consecutivas"),
    AUSENCIAS_ACUMULADAS("Ausencias Acumuladas"),
    TARDANZAS_ACUMULADAS("Tardanzas Acumuladas");

    private final String descripcion;

    TipoAlertaAsistencia(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
