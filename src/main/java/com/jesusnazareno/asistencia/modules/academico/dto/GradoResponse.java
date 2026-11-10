package com.jesusnazareno.asistencia.modules.academico.dto;

import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;

import java.time.LocalDateTime;

public class GradoResponse {

    private Long id;
    private NivelEducativo nivel;
    private Integer numero;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long cantidadSecciones;

    public GradoResponse() {
    }

    public GradoResponse(Long id, NivelEducativo nivel, Integer numero, String nombre, String descripcion,
                         Boolean activo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, nivel, numero, nombre, descripcion, activo, createdAt, updatedAt, null, null, 0L);
    }

    public GradoResponse(Long id, NivelEducativo nivel, Integer numero, String nombre, String descripcion,
                         Boolean activo, LocalDateTime createdAt, LocalDateTime updatedAt,
                         String createdBy, String updatedBy, Long cantidadSecciones) {
        this.id = id;
        this.nivel = nivel;
        this.numero = numero;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.cantidadSecciones = cantidadSecciones;
    }

    public static GradoResponseBuilder builder() {
        return new GradoResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getCantidadSecciones() {
        return cantidadSecciones;
    }

    public void setCantidadSecciones(Long cantidadSecciones) {
        this.cantidadSecciones = cantidadSecciones;
    }

    public static class GradoResponseBuilder {
        private Long id;
        private NivelEducativo nivel;
        private Integer numero;
        private String nombre;
        private String descripcion;
        private Boolean activo;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Long cantidadSecciones;

        GradoResponseBuilder() {
        }

        public GradoResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public GradoResponseBuilder nivel(NivelEducativo nivel) {
            this.nivel = nivel;
            return this;
        }

        public GradoResponseBuilder numero(Integer numero) {
            this.numero = numero;
            return this;
        }

        public GradoResponseBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public GradoResponseBuilder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public GradoResponseBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public GradoResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public GradoResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public GradoResponseBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public GradoResponseBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public GradoResponseBuilder cantidadSecciones(Long cantidadSecciones) {
            this.cantidadSecciones = cantidadSecciones;
            return this;
        }

        public GradoResponse build() {
            return new GradoResponse(id, nivel, numero, nombre, descripcion, activo, createdAt, updatedAt, createdBy, updatedBy, cantidadSecciones);
        }
    }
}
