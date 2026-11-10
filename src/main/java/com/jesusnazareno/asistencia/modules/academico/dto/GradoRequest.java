package com.jesusnazareno.asistencia.modules.academico.dto;

import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class GradoRequest {

    @NotNull(message = "{validation.required}")
    private NivelEducativo nivel;

    @NotNull(message = "{validation.required}")
    @Min(value = 1, message = "El número debe ser mayor o igual a 1.")
    private Integer numero;

    @NotBlank(message = "{validation.required}")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres.")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres.")
    private String descripcion;

    public GradoRequest() {
    }

    public GradoRequest(NivelEducativo nivel, Integer numero, String nombre, String descripcion) {
        this.nivel = nivel;
        this.numero = numero;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public static GradoRequestBuilder builder() {
        return new GradoRequestBuilder();
    }

    public NivelEducativo getNivel() {
        return nivel;
    }

    public void setNivel(NivelEducativo nivel) {
        this.nivel = nivel;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public static class GradoRequestBuilder {
        private NivelEducativo nivel;
        private Integer numero;
        private String nombre;
        private String descripcion;

        GradoRequestBuilder() {
        }

        public GradoRequestBuilder nivel(NivelEducativo nivel) {
            this.nivel = nivel;
            return this;
        }

        public GradoRequestBuilder numero(Integer numero) {
            this.numero = numero;
            return this;
        }

        public GradoRequestBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public GradoRequestBuilder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public GradoRequest build() {
            return new GradoRequest(nivel, numero, nombre, descripcion);
        }
    }
}
