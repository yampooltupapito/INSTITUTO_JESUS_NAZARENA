package com.jesusnazareno.asistencia.modules.academico.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class AnioEscolarRequest {

    @NotNull(message = "{validation.required}")
    @Min(value = 2000, message = "El año debe ser mayor o igual a 2000.")
    private Integer anio;

    @NotNull(message = "{validation.required}")
    private LocalDate fechaInicio;

    @NotNull(message = "{validation.required}")
    private LocalDate fechaFin;

    @NotBlank(message = "{validation.required}")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres.")
    private String nombre;

    public AnioEscolarRequest() {
    }

    public AnioEscolarRequest(Integer anio, LocalDate fechaInicio, LocalDate fechaFin, String nombre) {
        this.anio = anio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.nombre = nombre;
    }

    public static AnioEscolarRequestBuilder builder() {
        return new AnioEscolarRequestBuilder();
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public static class AnioEscolarRequestBuilder {
        private Integer anio;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private String nombre;

        AnioEscolarRequestBuilder() {
        }

        public AnioEscolarRequestBuilder anio(Integer anio) {
            this.anio = anio;
            return this;
        }

        public AnioEscolarRequestBuilder fechaInicio(LocalDate fechaInicio) {
            this.fechaInicio = fechaInicio;
            return this;
        }

        public AnioEscolarRequestBuilder fechaFin(LocalDate fechaFin) {
            this.fechaFin = fechaFin;
            return this;
        }

        public AnioEscolarRequestBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public AnioEscolarRequest build() {
            return new AnioEscolarRequest(anio, fechaInicio, fechaFin, nombre);
        }
    }
}
