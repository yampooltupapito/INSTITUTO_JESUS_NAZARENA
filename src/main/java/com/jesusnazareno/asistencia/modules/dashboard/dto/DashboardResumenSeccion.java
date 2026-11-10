package com.jesusnazareno.asistencia.modules.dashboard.dto;

import java.util.Locale;

/**
 * DTO para el resumen de asistencia agrupado por sección en el Dashboard.
 */
public class DashboardResumenSeccion {

    private Long gradoId;
    private String gradoNombre;
    private Long seccionId;
    private String seccionNombre;
    private long presentes;
    private long tardanzas;
    private long ausentes;
    private long justificados;
    private long total;

    public DashboardResumenSeccion() {
    }

    public DashboardResumenSeccion(Long gradoId, String gradoNombre, Long seccionId, String seccionNombre,
                                   long presentes, long tardanzas, long ausentes, long justificados) {
        this.gradoId = gradoId;
        this.gradoNombre = gradoNombre;
        this.seccionId = seccionId;
        this.seccionNombre = seccionNombre;
        this.presentes = presentes;
        this.tardanzas = tardanzas;
        this.ausentes = ausentes;
        this.justificados = justificados;
        this.total = presentes + tardanzas + ausentes + justificados;
    }

    public double getPorcentajeAsistencia() {
        if (total == 0) {
            return 0.0;
        }
        double efectiva = (double) (presentes + tardanzas);
        return Math.round((efectiva * 100.0 / total) * 10.0) / 10.0;
    }

    public String getPorcentajeFormatted() {
        return String.format(Locale.US, "%.1f%%", getPorcentajeAsistencia());
    }

    public Long getGradoId() {
        return gradoId;
    }

    public void setGradoId(Long gradoId) {
        this.gradoId = gradoId;
    }

    public String getGradoNombre() {
        return gradoNombre;
    }

    public void setGradoNombre(String gradoNombre) {
        this.gradoNombre = gradoNombre;
    }

    public Long getSeccionId() {
        return seccionId;
    }

    public void setSeccionId(Long seccionId) {
        this.seccionId = seccionId;
    }

    public String getSeccionNombre() {
        return seccionNombre;
    }

    public void setSeccionNombre(String seccionNombre) {
        this.seccionNombre = seccionNombre;
    }

    public long getPresentes() {
        return presentes;
    }

    public void setPresentes(long presentes) {
        this.presentes = presentes;
        this.total = this.presentes + this.tardanzas + this.ausentes + this.justificados;
    }

    public long getTardanzas() {
        return tardanzas;
    }

    public void setTardanzas(long tardanzas) {
        this.tardanzas = tardanzas;
        this.total = this.presentes + this.tardanzas + this.ausentes + this.justificados;
    }

    public long getAusentes() {
        return ausentes;
    }

    public void setAusentes(long ausentes) {
        this.ausentes = ausentes;
        this.total = this.presentes + this.tardanzas + this.ausentes + this.justificados;
    }

    public long getJustificados() {
        return justificados;
    }

    public void setJustificados(long justificados) {
        this.justificados = justificados;
        this.total = this.presentes + this.tardanzas + this.ausentes + this.justificados;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
