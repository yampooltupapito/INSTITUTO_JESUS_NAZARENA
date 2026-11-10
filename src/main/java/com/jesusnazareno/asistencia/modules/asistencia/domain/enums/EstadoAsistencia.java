package com.jesusnazareno.asistencia.modules.asistencia.domain.enums;

/**
 * Enumeración que representa los estados posibles de la asistencia diaria.
 * Coincide exactamente con la restricción CHECK de PostgreSQL (chk_asistencia_estado).
 */
public enum EstadoAsistencia {
    PRESENTE,
    TARDANZA,
    AUSENTE,
    JUSTIFICADO
}
