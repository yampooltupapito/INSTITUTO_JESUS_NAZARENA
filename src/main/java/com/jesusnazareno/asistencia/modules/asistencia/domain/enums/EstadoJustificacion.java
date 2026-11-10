package com.jesusnazareno.asistencia.modules.asistencia.domain.enums;

/**
 * Enumeración que representa los estados válidos de una justificación de asistencia.
 * Coincide exactamente con la restricción CHECK de PostgreSQL (chk_justificacion_estado).
 */
public enum EstadoJustificacion {
    PENDIENTE,
    APROBADA,
    RECHAZADA
}
