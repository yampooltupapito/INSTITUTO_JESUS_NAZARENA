package com.jesusnazareno.asistencia.modules.reporte.dto;

/**
 * Tipos de reportes de asistencia disponibles en el sistema.
 */
public enum TipoReporteAsistencia {
    DIARIO_SECCION("Diario por Sección"),
    POR_ESTUDIANTE("Por Estudiante"),
    RANGO_FECHAS("Rango de Fechas"),
    RESUMEN_SECCION("Resumen por Sección");

    private final String descripcion;

    TipoReporteAsistencia(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
