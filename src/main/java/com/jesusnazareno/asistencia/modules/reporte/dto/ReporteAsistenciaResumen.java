package com.jesusnazareno.asistencia.modules.reporte.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * DTO que consolida los totales e indicadores porcentuales de un reporte de asistencia.
 * Aplica protección estricta contra división por cero y mantiene JUSTIFICADO estrictamente separado.
 */
public class ReporteAsistenciaResumen {

    private long totalMatriculados;
    private long totalRegistros;
    private long presentes;
    private long tardanzas;
    private long ausentes;
    private long justificados;

    public ReporteAsistenciaResumen() {
    }

    public ReporteAsistenciaResumen(long totalMatriculados, long totalRegistros,
                                   long presentes, long tardanzas,
                                   long ausentes, long justificados) {
        this.totalMatriculados = totalMatriculados;
        this.totalRegistros = totalRegistros;
        this.presentes = presentes;
        this.tardanzas = tardanzas;
        this.ausentes = ausentes;
        this.justificados = justificados;
    }

    public long getTotalMatriculados() {
        return totalMatriculados;
    }

    public void setTotalMatriculados(long totalMatriculados) {
        this.totalMatriculados = totalMatriculados;
    }

    public long getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(long totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public long getPresentes() {
        return presentes;
    }

    public void setPresentes(long presentes) {
        this.presentes = presentes;
    }

    public long getTardanzas() {
        return tardanzas;
    }

    public void setTardanzas(long tardanzas) {
        this.tardanzas = tardanzas;
    }

    public long getAusentes() {
        return ausentes;
    }

    public void setAusentes(long ausentes) {
        this.ausentes = ausentes;
    }

    public long getJustificados() {
        return justificados;
    }

    public void setJustificados(long justificados) {
        this.justificados = justificados;
    }

    public double getPorcentajePresentes() {
        if (totalRegistros == 0) {
            return 0.0;
        }
        return redondear((presentes * 100.0) / totalRegistros);
    }

    public double getPorcentajeTardanzas() {
        if (totalRegistros == 0) {
            return 0.0;
        }
        return redondear((tardanzas * 100.0) / totalRegistros);
    }

    public double getPorcentajeAusentes() {
        if (totalRegistros == 0) {
            return 0.0;
        }
        return redondear((ausentes * 100.0) / totalRegistros);
    }

    public double getPorcentajeJustificados() {
        if (totalRegistros == 0) {
            return 0.0;
        }
        return redondear((justificados * 100.0) / totalRegistros);
    }

    /**
     * Calcula el porcentaje de asistencia general (Presentes + Tardanzas).
     */
    public double getPorcentajeAsistencia() {
        if (totalRegistros == 0) {
            return 0.0;
        }
        return redondear(((presentes + tardanzas) * 100.0) / totalRegistros);
    }

    private double redondear(double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public String getPorcentajePresentesFormatted() {
        return totalRegistros == 0 ? "0.0%" : String.format(java.util.Locale.US, "%.1f%%", getPorcentajePresentes());
    }

    public String getPorcentajeTardanzasFormatted() {
        return totalRegistros == 0 ? "0.0%" : String.format(java.util.Locale.US, "%.1f%%", getPorcentajeTardanzas());
    }

    public String getPorcentajeAusentesFormatted() {
        return totalRegistros == 0 ? "0.0%" : String.format(java.util.Locale.US, "%.1f%%", getPorcentajeAusentes());
    }

    public String getPorcentajeJustificadosFormatted() {
        return totalRegistros == 0 ? "0.0%" : String.format(java.util.Locale.US, "%.1f%%", getPorcentajeJustificados());
    }

    public String getPorcentajeAsistenciaFormatted() {
        return totalRegistros == 0 ? "0.0%" : String.format(java.util.Locale.US, "%.1f%%", getPorcentajeAsistencia());
    }
}
