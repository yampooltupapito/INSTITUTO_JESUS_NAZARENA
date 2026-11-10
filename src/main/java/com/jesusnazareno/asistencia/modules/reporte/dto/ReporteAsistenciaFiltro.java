package com.jesusnazareno.asistencia.modules.reporte.dto;

import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO para la captura y transporte de filtros de consulta de reportes de asistencia.
 * No es una entidad JPA.
 */
public class ReporteAsistenciaFiltro {

    private TipoReporteAsistencia tipoReporte = TipoReporteAsistencia.DIARIO_SECCION;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;

    private Long anioEscolarId;
    private Long gradoId;
    private Long seccionId;
    private Long estudianteId;
    private EstadoAsistencia estado;

    public ReporteAsistenciaFiltro() {
    }

    public TipoReporteAsistencia getTipoReporte() {
        return tipoReporte != null ? tipoReporte : TipoReporteAsistencia.DIARIO_SECCION;
    }

    public void setTipoReporte(TipoReporteAsistencia tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Long getAnioEscolarId() {
        return anioEscolarId;
    }

    public void setAnioEscolarId(Long anioEscolarId) {
        this.anioEscolarId = anioEscolarId;
    }

    public Long getGradoId() {
        return gradoId;
    }

    public void setGradoId(Long gradoId) {
        this.gradoId = gradoId;
    }

    public Long getSeccionId() {
        return seccionId;
    }

    public void setSeccionId(Long seccionId) {
        this.seccionId = seccionId;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public EstadoAsistencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsistencia estado) {
        this.estado = estado;
    }
}
