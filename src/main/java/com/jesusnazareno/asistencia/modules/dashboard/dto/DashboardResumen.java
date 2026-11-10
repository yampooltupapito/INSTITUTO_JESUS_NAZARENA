package com.jesusnazareno.asistencia.modules.dashboard.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * DTO consolidado de métricas, indicadores y resúmenes para el Dashboard principal.
 */
public class DashboardResumen {

    private static final DateTimeFormatter FORMATTER_CORTO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private long totalEstudiantes;
    private long totalMatriculas;
    private long presentesHoy;
    private long tardanzasHoy;
    private long ausentesHoy;
    private long justificadosHoy;
    private long justificacionesPendientes;
    private long totalRegistrosHoy;
    private long asistenciaEfectiva;
    private double porcentajeAsistencia;
    private String porcentajeAsistenciaFormatted;
    private long esperadosHoy;
    private long pendientesHoy;
    private LocalDate fecha;
    private String fechaFormateada;
    private String fechaCorta;

    private Long anioEscolarId;
    private String anioEscolarNombre;
    private Long gradoId;
    private String gradoNombre;
    private Long seccionId;
    private String seccionNombre;

    private boolean esDiaLectivo = true;
    private String mensajeDiaLectivo;

    private long totalAlertasPendientes;

    private List<DashboardResumenGrado> resumenGrados = new ArrayList<>();
    private List<DashboardResumenSeccion> resumenSecciones = new ArrayList<>();
    private List<DashboardTendenciaDia> tendenciaDias = new ArrayList<>();

    public DashboardResumen() {
    }

    public DashboardResumen(long totalEstudiantes, long totalMatriculas, long presentesHoy,
                            long tardanzasHoy, long ausentesHoy, long justificacionesPendientes,
                            LocalDate fecha, String fechaFormateada) {
        this.totalEstudiantes = totalEstudiantes;
        this.totalMatriculas = totalMatriculas;
        this.presentesHoy = presentesHoy;
        this.tardanzasHoy = tardanzasHoy;
        this.ausentesHoy = ausentesHoy;
        this.justificadosHoy = 0L;
        this.justificacionesPendientes = justificacionesPendientes;
        this.fecha = fecha;
        this.fechaFormateada = fechaFormateada;
        this.fechaCorta = fecha != null ? fecha.format(FORMATTER_CORTO) : "";
        recalcularTotales();
    }

    public void recalcularTotales() {
        this.totalRegistrosHoy = this.presentesHoy + this.tardanzasHoy + this.ausentesHoy + this.justificadosHoy;
        this.asistenciaEfectiva = this.presentesHoy + this.tardanzasHoy;
        if (this.totalRegistrosHoy == 0) {
            this.porcentajeAsistencia = 0.0;
            this.porcentajeAsistenciaFormatted = "0.0%";
        } else {
            this.porcentajeAsistencia = Math.round(((double) this.asistenciaEfectiva * 100.0 / this.totalRegistrosHoy) * 10.0) / 10.0;
            this.porcentajeAsistenciaFormatted = String.format(Locale.US, "%.1f%%", this.porcentajeAsistencia);
        }
        if (!this.esDiaLectivo) {
            this.esperadosHoy = 0;
            this.pendientesHoy = 0;
            if (this.mensajeDiaLectivo == null || this.mensajeDiaLectivo.isBlank()) {
                this.mensajeDiaLectivo = "Hoy no es un día lectivo.";
            }
        } else {
            this.esperadosHoy = this.totalMatriculas;
            this.pendientesHoy = Math.max(0, this.esperadosHoy - this.totalRegistrosHoy);
        }
        if (this.fecha != null && (this.fechaCorta == null || this.fechaCorta.isBlank())) {
            this.fechaCorta = this.fecha.format(FORMATTER_CORTO);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long totalEstudiantes;
        private long totalMatriculas;
        private long presentesHoy;
        private long tardanzasHoy;
        private long ausentesHoy;
        private long justificadosHoy;
        private long justificacionesPendientes;
        private LocalDate fecha;
        private String fechaFormateada;
        private Long anioEscolarId;
        private String anioEscolarNombre;
        private Long gradoId;
        private String gradoNombre;
        private Long seccionId;
        private String seccionNombre;
        private List<DashboardResumenGrado> resumenGrados = new ArrayList<>();
        private List<DashboardResumenSeccion> resumenSecciones = new ArrayList<>();
        private List<DashboardTendenciaDia> tendenciaDias = new ArrayList<>();

        public Builder totalEstudiantes(long totalEstudiantes) {
            this.totalEstudiantes = totalEstudiantes;
            return this;
        }

        public Builder totalMatriculas(long totalMatriculas) {
            this.totalMatriculas = totalMatriculas;
            return this;
        }

        public Builder presentesHoy(long presentesHoy) {
            this.presentesHoy = presentesHoy;
            return this;
        }

        public Builder tardanzasHoy(long tardanzasHoy) {
            this.tardanzasHoy = tardanzasHoy;
            return this;
        }

        public Builder ausentesHoy(long ausentesHoy) {
            this.ausentesHoy = ausentesHoy;
            return this;
        }

        public Builder justificadosHoy(long justificadosHoy) {
            this.justificadosHoy = justificadosHoy;
            return this;
        }

        public Builder justificacionesPendientes(long justificacionesPendientes) {
            this.justificacionesPendientes = justificacionesPendientes;
            return this;
        }

        public Builder fecha(LocalDate fecha) {
            this.fecha = fecha;
            return this;
        }

        public Builder fechaFormateada(String fechaFormateada) {
            this.fechaFormateada = fechaFormateada;
            return this;
        }

        public Builder anioEscolarId(Long anioEscolarId) {
            this.anioEscolarId = anioEscolarId;
            return this;
        }

        public Builder anioEscolarNombre(String anioEscolarNombre) {
            this.anioEscolarNombre = anioEscolarNombre;
            return this;
        }

        public Builder gradoId(Long gradoId) {
            this.gradoId = gradoId;
            return this;
        }

        public Builder gradoNombre(String gradoNombre) {
            this.gradoNombre = gradoNombre;
            return this;
        }

        public Builder seccionId(Long seccionId) {
            this.seccionId = seccionId;
            return this;
        }

        public Builder seccionNombre(String seccionNombre) {
            this.seccionNombre = seccionNombre;
            return this;
        }

        public Builder resumenGrados(List<DashboardResumenGrado> resumenGrados) {
            this.resumenGrados = resumenGrados;
            return this;
        }

        public Builder resumenSecciones(List<DashboardResumenSeccion> resumenSecciones) {
            this.resumenSecciones = resumenSecciones;
            return this;
        }

        public Builder tendenciaDias(List<DashboardTendenciaDia> tendenciaDias) {
            this.tendenciaDias = tendenciaDias;
            return this;
        }

        private boolean esDiaLectivo = true;
        private String mensajeDiaLectivo;
        private long totalAlertasPendientes;

        public Builder esDiaLectivo(boolean esDiaLectivo) {
            this.esDiaLectivo = esDiaLectivo;
            return this;
        }

        public Builder mensajeDiaLectivo(String mensajeDiaLectivo) {
            this.mensajeDiaLectivo = mensajeDiaLectivo;
            return this;
        }

        public Builder totalAlertasPendientes(long totalAlertasPendientes) {
            this.totalAlertasPendientes = totalAlertasPendientes;
            return this;
        }

        public DashboardResumen build() {
            DashboardResumen r = new DashboardResumen(totalEstudiantes, totalMatriculas, presentesHoy,
                    tardanzasHoy, ausentesHoy, justificacionesPendientes, fecha, fechaFormateada);
            r.setJustificadosHoy(justificadosHoy);
            r.setAnioEscolarId(anioEscolarId);
            r.setAnioEscolarNombre(anioEscolarNombre);
            r.setGradoId(gradoId);
            r.setGradoNombre(gradoNombre);
            r.setSeccionId(seccionId);
            r.setSeccionNombre(seccionNombre);
            r.setEsDiaLectivo(esDiaLectivo);
            r.setMensajeDiaLectivo(mensajeDiaLectivo);
            r.setTotalAlertasPendientes(totalAlertasPendientes);
            if (resumenGrados != null) r.setResumenGrados(resumenGrados);
            if (resumenSecciones != null) r.setResumenSecciones(resumenSecciones);
            if (tendenciaDias != null) r.setTendenciaDias(tendenciaDias);
            r.recalcularTotales();
            return r;
        }
    }

    public long getTotalEstudiantes() {
        return totalEstudiantes;
    }

    public void setTotalEstudiantes(long totalEstudiantes) {
        this.totalEstudiantes = totalEstudiantes;
    }

    public long getTotalMatriculas() {
        return totalMatriculas;
    }

    public void setTotalMatriculas(long totalMatriculas) {
        this.totalMatriculas = totalMatriculas;
        this.esperadosHoy = totalMatriculas;
        this.pendientesHoy = Math.max(0, this.esperadosHoy - this.totalRegistrosHoy);
    }

    public long getPresentesHoy() {
        return presentesHoy;
    }

    public void setPresentesHoy(long presentesHoy) {
        this.presentesHoy = presentesHoy;
        recalcularTotales();
    }

    public long getTardanzasHoy() {
        return tardanzasHoy;
    }

    public void setTardanzasHoy(long tardanzasHoy) {
        this.tardanzasHoy = tardanzasHoy;
        recalcularTotales();
    }

    public long getAusentesHoy() {
        return ausentesHoy;
    }

    public void setAusentesHoy(long ausentesHoy) {
        this.ausentesHoy = ausentesHoy;
        recalcularTotales();
    }

    public long getJustificadosHoy() {
        return justificadosHoy;
    }

    public void setJustificadosHoy(long justificadosHoy) {
        this.justificadosHoy = justificadosHoy;
        recalcularTotales();
    }

    public long getJustificacionesPendientes() {
        return justificacionesPendientes;
    }

    public void setJustificacionesPendientes(long justificacionesPendientes) {
        this.justificacionesPendientes = justificacionesPendientes;
    }

    public long getTotalRegistrosHoy() {
        return totalRegistrosHoy;
    }

    public void setTotalRegistrosHoy(long totalRegistrosHoy) {
        this.totalRegistrosHoy = totalRegistrosHoy;
    }

    public long getAsistenciaEfectiva() {
        return asistenciaEfectiva;
    }

    public void setAsistenciaEfectiva(long asistenciaEfectiva) {
        this.asistenciaEfectiva = asistenciaEfectiva;
    }

    public double getPorcentajeAsistencia() {
        return porcentajeAsistencia;
    }

    public void setPorcentajeAsistencia(double porcentajeAsistencia) {
        this.porcentajeAsistencia = porcentajeAsistencia;
    }

    public String getPorcentajeAsistenciaFormatted() {
        return porcentajeAsistenciaFormatted;
    }

    public void setPorcentajeAsistenciaFormatted(String porcentajeAsistenciaFormatted) {
        this.porcentajeAsistenciaFormatted = porcentajeAsistenciaFormatted;
    }

    public long getEsperadosHoy() {
        return esperadosHoy;
    }

    public void setEsperadosHoy(long esperadosHoy) {
        this.esperadosHoy = esperadosHoy;
    }

    public long getPendientesHoy() {
        return pendientesHoy;
    }

    public void setPendientesHoy(long pendientesHoy) {
        this.pendientesHoy = pendientesHoy;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
        if (fecha != null) {
            this.fechaCorta = fecha.format(FORMATTER_CORTO);
        }
    }

    public String getFechaFormateada() {
        return fechaFormateada;
    }

    public void setFechaFormateada(String fechaFormateada) {
        this.fechaFormateada = fechaFormateada;
    }

    public String getFechaCorta() {
        return fechaCorta;
    }

    public void setFechaCorta(String fechaCorta) {
        this.fechaCorta = fechaCorta;
    }

    public Long getAnioEscolarId() {
        return anioEscolarId;
    }

    public void setAnioEscolarId(Long anioEscolarId) {
        this.anioEscolarId = anioEscolarId;
    }

    public String getAnioEscolarNombre() {
        return anioEscolarNombre;
    }

    public void setAnioEscolarNombre(String anioEscolarNombre) {
        this.anioEscolarNombre = anioEscolarNombre;
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

    public List<DashboardResumenGrado> getResumenGrados() {
        return resumenGrados;
    }

    public void setResumenGrados(List<DashboardResumenGrado> resumenGrados) {
        this.resumenGrados = resumenGrados;
    }

    public List<DashboardResumenSeccion> getResumenSecciones() {
        return resumenSecciones;
    }

    public void setResumenSecciones(List<DashboardResumenSeccion> resumenSecciones) {
        this.resumenSecciones = resumenSecciones;
    }

    public List<DashboardTendenciaDia> getTendenciaDias() {
        return tendenciaDias;
    }

    public void setTendenciaDias(List<DashboardTendenciaDia> tendenciaDias) {
        this.tendenciaDias = tendenciaDias;
    }

    public boolean isEsDiaLectivo() {
        return esDiaLectivo;
    }

    public void setEsDiaLectivo(boolean esDiaLectivo) {
        this.esDiaLectivo = esDiaLectivo;
    }

    public String getMensajeDiaLectivo() {
        return mensajeDiaLectivo;
    }

    public void setMensajeDiaLectivo(String mensajeDiaLectivo) {
        this.mensajeDiaLectivo = mensajeDiaLectivo;
    }

    public long getTotalAlertasPendientes() {
        return totalAlertasPendientes;
    }

    public void setTotalAlertasPendientes(long totalAlertasPendientes) {
        this.totalAlertasPendientes = totalAlertasPendientes;
    }
}
