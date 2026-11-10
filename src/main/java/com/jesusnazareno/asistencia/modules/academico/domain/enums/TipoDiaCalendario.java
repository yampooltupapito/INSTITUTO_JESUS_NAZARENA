package com.jesusnazareno.asistencia.modules.academico.domain.enums;

/**
 * Enumeración que representa los tipos de fechas especiales en el calendario escolar.
 */
public enum TipoDiaCalendario {
    FERIADO("Feriado"),
    NO_LABORABLE("Día No Laborable"),
    SUSPENSION("Suspensión de Clases"),
    ACTIVIDAD_INSTITUCIONAL("Actividad Institucional"),
    LECTIVO_EXTRAORDINARIO("Lectivo Extraordinario");

    private final String descripcion;

    TipoDiaCalendario(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
