package com.jesusnazareno.asistencia.modules.academico.dto;

import com.jesusnazareno.asistencia.modules.academico.domain.enums.TipoDiaCalendario;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO para la creación y edición de excepciones y fechas especiales del calendario escolar.
 */
public class CalendarioEscolarRequest {

    @NotNull(message = "{validation.required}")
    private Long anioEscolarId;

    @NotNull(message = "{validation.required}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @NotNull(message = "{validation.required}")
    private TipoDiaCalendario tipoDia;

    @Size(max = 200, message = "La descripción no puede superar los 200 caracteres.")
    private String descripcion;

    @NotNull(message = "{validation.required}")
    private Boolean esLectivo;

    public CalendarioEscolarRequest() {
        this.esLectivo = false;
    }

    public CalendarioEscolarRequest(Long anioEscolarId, LocalDate fecha, TipoDiaCalendario tipoDia,
                                   String descripcion, Boolean esLectivo) {
        this.anioEscolarId = anioEscolarId;
        this.fecha = fecha;
        this.tipoDia = tipoDia;
        this.descripcion = descripcion;
        this.esLectivo = esLectivo != null ? esLectivo : false;
    }

    public static CalendarioEscolarRequestBuilder builder() {
        return new CalendarioEscolarRequestBuilder();
    }

    public Long getAnioEscolarId() {
        return anioEscolarId;
    }

    public void setAnioEscolarId(Long anioEscolarId) {
        this.anioEscolarId = anioEscolarId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public TipoDiaCalendario getTipoDia() {
        return tipoDia;
    }

    public void setTipoDia(TipoDiaCalendario tipoDia) {
        this.tipoDia = tipoDia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEsLectivo() {
        return esLectivo;
    }

    public void setEsLectivo(Boolean esLectivo) {
        this.esLectivo = esLectivo;
    }

    public static class CalendarioEscolarRequestBuilder {
        private Long anioEscolarId;
        private LocalDate fecha;
        private TipoDiaCalendario tipoDia;
        private String descripcion;
        private Boolean esLectivo = false;

        public CalendarioEscolarRequestBuilder anioEscolarId(Long anioEscolarId) {
            this.anioEscolarId = anioEscolarId;
            return this;
        }

        public CalendarioEscolarRequestBuilder fecha(LocalDate fecha) {
            this.fecha = fecha;
            return this;
        }

        public CalendarioEscolarRequestBuilder tipoDia(TipoDiaCalendario tipoDia) {
            this.tipoDia = tipoDia;
            return this;
        }

        public CalendarioEscolarRequestBuilder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public CalendarioEscolarRequestBuilder esLectivo(Boolean esLectivo) {
            this.esLectivo = esLectivo;
            return this;
        }

        public CalendarioEscolarRequest build() {
            return new CalendarioEscolarRequest(anioEscolarId, fecha, tipoDia, descripcion, esLectivo);
        }
    }
}
