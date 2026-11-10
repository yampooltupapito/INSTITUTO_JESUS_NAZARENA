package com.jesusnazareno.asistencia.modules.matricula.dto;

import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para la respuesta estructurada de una matrícula escolar.
 */
public class MatriculaResponse {

    private Long id;
    private Long personaEstudianteId;
    private String estudianteNombreCompleto;
    private String estudianteNumeroDocumento;
    private Long anioEscolarId;
    private Integer anioEscolarAnio;
    private Long gradoId;
    private String gradoNombre;
    private Long seccionId;
    private String seccionNombre;
    private String seccionTurno;
    private String codigoMatricula;
    private LocalDate fechaMatricula;
    private EstadoMatricula estado;
    private String observaciones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public MatriculaResponse() {
    }

    public MatriculaResponse(Long id, Long personaEstudianteId, String estudianteNombreCompleto,
                             String estudianteNumeroDocumento, Long anioEscolarId, Integer anioEscolarAnio,
                             Long gradoId, String gradoNombre, Long seccionId, String seccionNombre,
                             String codigoMatricula, LocalDate fechaMatricula, EstadoMatricula estado,
                             String observaciones, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, personaEstudianteId, estudianteNombreCompleto, estudianteNumeroDocumento,
                anioEscolarId, anioEscolarAnio, gradoId, gradoNombre, seccionId, seccionNombre, null,
                codigoMatricula, fechaMatricula, estado, observaciones, createdAt, updatedAt, null, null);
    }

    public MatriculaResponse(Long id, Long personaEstudianteId, String estudianteNombreCompleto,
                             String estudianteNumeroDocumento, Long anioEscolarId, Integer anioEscolarAnio,
                             Long gradoId, String gradoNombre, Long seccionId, String seccionNombre,
                             String seccionTurno, String codigoMatricula, LocalDate fechaMatricula,
                             EstadoMatricula estado, String observaciones, LocalDateTime createdAt,
                             LocalDateTime updatedAt, String createdBy, String updatedBy) {
        this.id = id;
        this.personaEstudianteId = personaEstudianteId;
        this.estudianteNombreCompleto = estudianteNombreCompleto;
        this.estudianteNumeroDocumento = estudianteNumeroDocumento;
        this.anioEscolarId = anioEscolarId;
        this.anioEscolarAnio = anioEscolarAnio;
        this.gradoId = gradoId;
        this.gradoNombre = gradoNombre;
        this.seccionId = seccionId;
        this.seccionNombre = seccionNombre;
        this.seccionTurno = seccionTurno;
        this.codigoMatricula = codigoMatricula;
        this.fechaMatricula = fechaMatricula;
        this.estado = estado;
        this.observaciones = observaciones;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public static MatriculaResponseBuilder builder() {
        return new MatriculaResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonaEstudianteId() {
        return personaEstudianteId;
    }

    public void setPersonaEstudianteId(Long personaEstudianteId) {
        this.personaEstudianteId = personaEstudianteId;
    }

    public String getEstudianteNombreCompleto() {
        return estudianteNombreCompleto;
    }

    public void setEstudianteNombreCompleto(String estudianteNombreCompleto) {
        this.estudianteNombreCompleto = estudianteNombreCompleto;
    }

    public String getEstudianteNumeroDocumento() {
        return estudianteNumeroDocumento;
    }

    public void setEstudianteNumeroDocumento(String estudianteNumeroDocumento) {
        this.estudianteNumeroDocumento = estudianteNumeroDocumento;
    }

    public Long getAnioEscolarId() {
        return anioEscolarId;
    }

    public void setAnioEscolarId(Long anioEscolarId) {
        this.anioEscolarId = anioEscolarId;
    }

    public Integer getAnioEscolarAnio() {
        return anioEscolarAnio;
    }

    public void setAnioEscolarAnio(Integer anioEscolarAnio) {
        this.anioEscolarAnio = anioEscolarAnio;
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

    public String getCodigoMatricula() {
        return codigoMatricula;
    }

    public void setCodigoMatricula(String codigoMatricula) {
        this.codigoMatricula = codigoMatricula;
    }

    public LocalDate getFechaMatricula() {
        return fechaMatricula;
    }

    public void setFechaMatricula(LocalDate fechaMatricula) {
        this.fechaMatricula = fechaMatricula;
    }

    public EstadoMatricula getEstado() {
        return estado;
    }

    public void setEstado(EstadoMatricula estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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

    public String getSeccionTurno() {
        return seccionTurno;
    }

    public void setSeccionTurno(String seccionTurno) {
        this.seccionTurno = seccionTurno;
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

    public static class MatriculaResponseBuilder {
        private Long id;
        private Long personaEstudianteId;
        private String estudianteNombreCompleto;
        private String estudianteNumeroDocumento;
        private Long anioEscolarId;
        private Integer anioEscolarAnio;
        private Long gradoId;
        private String gradoNombre;
        private Long seccionId;
        private String seccionNombre;
        private String seccionTurno;
        private String codigoMatricula;
        private LocalDate fechaMatricula;
        private EstadoMatricula estado;
        private String observaciones;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;

        public MatriculaResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public MatriculaResponseBuilder personaEstudianteId(Long personaEstudianteId) {
            this.personaEstudianteId = personaEstudianteId;
            return this;
        }

        public MatriculaResponseBuilder estudianteNombreCompleto(String estudianteNombreCompleto) {
            this.estudianteNombreCompleto = estudianteNombreCompleto;
            return this;
        }

        public MatriculaResponseBuilder estudianteNumeroDocumento(String estudianteNumeroDocumento) {
            this.estudianteNumeroDocumento = estudianteNumeroDocumento;
            return this;
        }

        public MatriculaResponseBuilder anioEscolarId(Long anioEscolarId) {
            this.anioEscolarId = anioEscolarId;
            return this;
        }

        public MatriculaResponseBuilder anioEscolarAnio(Integer anioEscolarAnio) {
            this.anioEscolarAnio = anioEscolarAnio;
            return this;
        }

        public MatriculaResponseBuilder gradoId(Long gradoId) {
            this.gradoId = gradoId;
            return this;
        }

        public MatriculaResponseBuilder gradoNombre(String gradoNombre) {
            this.gradoNombre = gradoNombre;
            return this;
        }

        public MatriculaResponseBuilder seccionId(Long seccionId) {
            this.seccionId = seccionId;
            return this;
        }

        public MatriculaResponseBuilder seccionNombre(String seccionNombre) {
            this.seccionNombre = seccionNombre;
            return this;
        }

        public MatriculaResponseBuilder seccionTurno(String seccionTurno) {
            this.seccionTurno = seccionTurno;
            return this;
        }

        public MatriculaResponseBuilder codigoMatricula(String codigoMatricula) {
            this.codigoMatricula = codigoMatricula;
            return this;
        }

        public MatriculaResponseBuilder fechaMatricula(LocalDate fechaMatricula) {
            this.fechaMatricula = fechaMatricula;
            return this;
        }

        public MatriculaResponseBuilder estado(EstadoMatricula estado) {
            this.estado = estado;
            return this;
        }

        public MatriculaResponseBuilder observaciones(String observaciones) {
            this.observaciones = observaciones;
            return this;
        }

        public MatriculaResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public MatriculaResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public MatriculaResponseBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public MatriculaResponseBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public MatriculaResponse build() {
            return new MatriculaResponse(id, personaEstudianteId, estudianteNombreCompleto, estudianteNumeroDocumento,
                    anioEscolarId, anioEscolarAnio, gradoId, gradoNombre, seccionId, seccionNombre, seccionTurno,
                    codigoMatricula, fechaMatricula, estado, observaciones, createdAt, updatedAt, createdBy, updatedBy);
        }
    }
}
