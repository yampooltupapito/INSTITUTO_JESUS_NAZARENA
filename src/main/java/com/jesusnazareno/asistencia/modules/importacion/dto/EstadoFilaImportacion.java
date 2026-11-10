package com.jesusnazareno.asistencia.modules.importacion.dto;

public enum EstadoFilaImportacion {
    VALIDA("Válida"),
    ERROR("Con Error"),
    YA_EXISTE("Ya Existe");

    private final String descripcion;

    EstadoFilaImportacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
