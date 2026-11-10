package com.jesusnazareno.asistencia.modules.apoderado.domain.enums;

/**
 * Enumeración que representa el grado de parentesco o relación entre el apoderado y el estudiante.
 */
public enum Parentesco {
    PADRE("Padre"),
    MADRE("Madre"),
    ABUELO("Abuelo"),
    ABUELA("Abuela"),
    HERMANO("Hermano"),
    HERMANA("Hermana"),
    TIO("Tío"),
    TIA("Tía"),
    TUTOR("Tutor Legal"),
    OTRO("Otro");

    private final String descripcion;

    Parentesco(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
