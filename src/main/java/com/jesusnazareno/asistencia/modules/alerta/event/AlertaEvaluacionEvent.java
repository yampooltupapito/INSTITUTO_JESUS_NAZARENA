package com.jesusnazareno.asistencia.modules.alerta.event;

/**
 * Evento interno de Spring para solicitar la evaluación de alertas de asistencia
 * para una matrícula específica tras registrar, actualizar o justificar asistencias.
 */
public class AlertaEvaluacionEvent {

    private final Long matriculaId;

    public AlertaEvaluacionEvent(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public Long getMatriculaId() {
        return matriculaId;
    }
}
