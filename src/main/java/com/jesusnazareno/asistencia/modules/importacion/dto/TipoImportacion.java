package com.jesusnazareno.asistencia.modules.importacion.dto;

public enum TipoImportacion {
    ESTUDIANTES("Importación de Estudiantes"),
    MATRICULAS("Importación de Matrículas");

    private final String descripcion;

    TipoImportacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
