package com.jesusnazareno.asistencia.modules.dashboard.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * DTO para representar las asistencias de un día en la tendencia semanal del Dashboard.
 */
public class DashboardTendenciaDia {

    private static final Locale LOCALE_PE = new Locale("es", "PE");
    private static final DateTimeFormatter FORMATTER_CORTO = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter FORMATTER_DIA = DateTimeFormatter.ofPattern("EEE", LOCALE_PE);

    private LocalDate fecha;
    private String fechaFormatted;
    private String diaSemana;
    private long presentes;
    private long tardanzas;
    private long ausentes;
    private long justificados;
    private long total;

    public DashboardTendenciaDia() {
    }

    public DashboardTendenciaDia(LocalDate fecha) {
        this.fecha = fecha;
        this.fechaFormatted = fecha != null ? fecha.format(FORMATTER_CORTO) : "";
        this.diaSemana = fecha != null ? capitalize(fecha.format(FORMATTER_DIA)) : "";
        this.presentes = 0;
        this.tardanzas = 0;
        this.ausentes = 0;
        this.justificados = 0;
        this.total = 0;
    }

    public DashboardTendenciaDia(LocalDate fecha, long presentes, long tardanzas, long ausentes, long justificados) {
        this.fecha = fecha;
        this.fechaFormatted = fecha != null ? fecha.format(FORMATTER_CORTO) : "";
        this.diaSemana = fecha != null ? capitalize(fecha.format(FORMATTER_DIA)) : "";
        this.presentes = presentes;
        this.tardanzas = tardanzas;
        this.ausentes = ausentes;
        this.justificados = justificados;
        this.total = presentes + tardanzas + ausentes + justificados;
    }

    private static String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
        this.fechaFormatted = fecha != null ? fecha.format(FORMATTER_CORTO) : "";
        this.diaSemana = fecha != null ? capitalize(fecha.format(FORMATTER_DIA)) : "";
    }

    public String getFechaFormatted() {
        return fechaFormatted;
    }

    public void setFechaFormatted(String fechaFormatted) {
        this.fechaFormatted = fechaFormatted;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
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
