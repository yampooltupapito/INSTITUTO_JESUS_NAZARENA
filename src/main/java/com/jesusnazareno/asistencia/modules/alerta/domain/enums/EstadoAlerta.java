package com.jesusnazareno.asistencia.modules.alerta.domain.enums;

/**
 * Enumeración para los estados del ciclo de vida de una alerta de asistencia.
 * Coincide exactamente con el CHECK constraint en PostgreSQL.
 */
public enum EstadoAlerta {
    PENDIENTE("Pendiente"),
    EN_REVISION("En Revisión"),
    RESUELTA("Resuelta");

    private final String descripcion;

    EstadoAlerta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
