package com.jesusnazareno.asistencia.modules.academico.dto;

import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;

import java.time.LocalDateTime;

public class SeccionResponse {

    private Long id;
    private Long gradoId;
    private String gradoNombre;
    private NivelEducativo nivel;
    private String nombre;
    private Turno turno;
    private Integer capacidad;
    private Boolean activo;
    private Integer matriculados;
    private Integer vacantes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public SeccionResponse() {
    }

    public SeccionResponse(Long id, Long gradoId, String gradoNombre, String nombre, Turno turno, Integer capacidad,
                           Boolean activo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, gradoId, gradoNombre, null, nombre, turno, capacidad, activo, 0, capacidad != null ? capacidad : 0, createdAt, updatedAt, null, null);
    }

    public SeccionResponse(Long id, Long gradoId, String gradoNombre, NivelEducativo nivel, String nombre, Turno turno, Integer capacidad,
                           Boolean activo, Integer matriculados, Integer vacantes, LocalDateTime createdAt, LocalDateTime updatedAt,
                           String createdBy, String updatedBy) {
        this.id = id;
        this.gradoId = gradoId;
        this.gradoNombre = gradoNombre;
        this.nivel = nivel;
        this.nombre = nombre;
        this.turno = turno;
        this.capacidad = capacidad;
        this.activo = activo;
        this.matriculados = matriculados;
        this.vacantes = vacantes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public static SeccionResponseBuilder builder() {
        return new SeccionResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
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

    public NivelEducativo getNivel() {
        return nivel;
    }

    public void setNivel(NivelEducativo nivel) {
        this.nivel = nivel;
    }

    public Integer getMatriculados() {
        return matriculados;
    }

    public void setMatriculados(Integer matriculados) {
        this.matriculados = matriculados;
    }

    public Integer getVacantes() {
        return vacantes;
    }

    public void setVacantes(Integer vacantes) {
        this.vacantes = vacantes;
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

    public static class SeccionResponseBuilder {
        private Long id;
        private Long gradoId;
        private String gradoNombre;
        private NivelEducativo nivel;
        private String nombre;
        private Turno turno;
        private Integer capacidad;
        private Boolean activo;
        private Integer matriculados = 0;
        private Integer vacantes = 0;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;

        SeccionResponseBuilder() {
        }

        public SeccionResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SeccionResponseBuilder gradoId(Long gradoId) {
            this.gradoId = gradoId;
            return this;
        }

        public SeccionResponseBuilder gradoNombre(String gradoNombre) {
            this.gradoNombre = gradoNombre;
            return this;
        }

        public SeccionResponseBuilder nivel(NivelEducativo nivel) {
            this.nivel = nivel;
            return this;
        }

        public SeccionResponseBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public SeccionResponseBuilder turno(Turno turno) {
            this.turno = turno;
            return this;
        }

        public SeccionResponseBuilder capacidad(Integer capacidad) {
            this.capacidad = capacidad;
            return this;
        }

        public SeccionResponseBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public SeccionResponseBuilder matriculados(Integer matriculados) {
            this.matriculados = matriculados;
            return this;
        }

        public SeccionResponseBuilder vacantes(Integer vacantes) {
            this.vacantes = vacantes;
            return this;
        }

        public SeccionResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public SeccionResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public SeccionResponseBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public SeccionResponseBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public SeccionResponse build() {
            return new SeccionResponse(id, gradoId, gradoNombre, nivel, nombre, turno, capacidad, activo, matriculados, vacantes, createdAt, updatedAt, createdBy, updatedBy);
        }
    }
}
