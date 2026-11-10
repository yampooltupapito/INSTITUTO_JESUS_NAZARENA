package com.jesusnazareno.asistencia.modules.matricula.dto;

import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO para la creación y actualización de una matrícula escolar.
 */
public class MatriculaRequest {

    @NotNull(message = "{validation.required}")
    private Long personaEstudianteId;

    @NotNull(message = "{validation.required}")
    private Long anioEscolarId;

    @NotNull(message = "{validation.required}")
    private Long gradoId;

    @NotNull(message = "{validation.required}")
    private Long seccionId;

    @NotBlank(message = "{validation.required}")
    @Size(max = 50, message = "{validation.invalid}")
    private String codigoMatricula;

    @NotNull(message = "{validation.required}")
    private LocalDate fechaMatricula;

    private EstadoMatricula estado;

    @Size(max = 1000, message = "{validation.invalid}")
    private String observaciones;

    public MatriculaRequest() {
    }

    public MatriculaRequest(Long personaEstudianteId, Long anioEscolarId, Long gradoId, Long seccionId,
                            String codigoMatricula, LocalDate fechaMatricula, EstadoMatricula estado, String observaciones) {
        this.personaEstudianteId = personaEstudianteId;
        this.anioEscolarId = anioEscolarId;
        this.gradoId = gradoId;
        this.seccionId = seccionId;
        this.codigoMatricula = codigoMatricula;
        this.fechaMatricula = fechaMatricula;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public static MatriculaRequestBuilder builder() {
        return new MatriculaRequestBuilder();
    }

    public Long getPersonaEstudianteId() {
        return personaEstudianteId;
    }

    public void setPersonaEstudianteId(Long personaEstudianteId) {
        this.personaEstudianteId = personaEstudianteId;
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

    public static class MatriculaRequestBuilder {
        private Long personaEstudianteId;
        private Long anioEscolarId;
        private Long gradoId;
        private Long seccionId;
        private String codigoMatricula;
        private LocalDate fechaMatricula;
        private EstadoMatricula estado;
        private String observaciones;

        public MatriculaRequestBuilder personaEstudianteId(Long personaEstudianteId) {
            this.personaEstudianteId = personaEstudianteId;
            return this;
        }

        public MatriculaRequestBuilder anioEscolarId(Long anioEscolarId) {
            this.anioEscolarId = anioEscolarId;
            return this;
        }

        public MatriculaRequestBuilder gradoId(Long gradoId) {
            this.gradoId = gradoId;
            return this;
        }

        public MatriculaRequestBuilder seccionId(Long seccionId) {
            this.seccionId = seccionId;
            return this;
        }

        public MatriculaRequestBuilder codigoMatricula(String codigoMatricula) {
            this.codigoMatricula = codigoMatricula;
            return this;
        }

        public MatriculaRequestBuilder fechaMatricula(LocalDate fechaMatricula) {
            this.fechaMatricula = fechaMatricula;
            return this;
        }

        public MatriculaRequestBuilder estado(EstadoMatricula estado) {
            this.estado = estado;
            return this;
        }

        public MatriculaRequestBuilder observaciones(String observaciones) {
            this.observaciones = observaciones;
            return this;
        }

        public MatriculaRequest build() {
            return new MatriculaRequest(personaEstudianteId, anioEscolarId, gradoId, seccionId,
                    codigoMatricula, fechaMatricula, estado, observaciones);
        }
    }
}
