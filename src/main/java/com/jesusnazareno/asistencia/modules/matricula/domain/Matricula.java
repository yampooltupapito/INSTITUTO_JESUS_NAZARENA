package com.jesusnazareno.asistencia.modules.matricula.domain;

import com.jesusnazareno.asistencia.common.persistence.BaseEntity;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad JPA que representa la tabla 'matricula' en la base de datos.
 */
@Entity
@Table(name = "matricula")
public class Matricula extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "persona_estudiante_id", nullable = false)
    private Persona estudiante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anio_escolar_id", nullable = false)
    private AnioEscolar anioEscolar;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grado_id", nullable = false)
    private Grado grado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seccion_id", nullable = false)
    private Seccion seccion;

    @Column(name = "codigo_matricula", nullable = false, unique = true, length = 50)
    private String codigoMatricula;

    @Column(name = "fecha_matricula", nullable = false)
    private LocalDate fechaMatricula;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoMatricula estado;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    public Matricula() {
    }

    public Matricula(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy,
                     Persona estudiante, AnioEscolar anioEscolar, Grado grado, Seccion seccion,
                     String codigoMatricula, LocalDate fechaMatricula, EstadoMatricula estado, String observaciones) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.estudiante = estudiante;
        this.anioEscolar = anioEscolar;
        this.grado = grado;
        this.seccion = seccion;
        this.codigoMatricula = codigoMatricula;
        this.fechaMatricula = fechaMatricula;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public static MatriculaBuilder builder() {
        return new MatriculaBuilder();
    }

    public Persona getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Persona estudiante) {
        this.estudiante = estudiante;
    }

    public AnioEscolar getAnioEscolar() {
        return anioEscolar;
    }

    public void setAnioEscolar(AnioEscolar anioEscolar) {
        this.anioEscolar = anioEscolar;
    }

    public Grado getGrado() {
        return grado;
    }

    public void setGrado(Grado grado) {
        this.grado = grado;
    }

    public Seccion getSeccion() {
        return seccion;
    }

    public void setSeccion(Seccion seccion) {
        this.seccion = seccion;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matricula matricula = (Matricula) o;
        return getId() != null && Objects.equals(getId(), matricula.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Matricula{" +
                "id=" + getId() +
                ", codigoMatricula='" + codigoMatricula + '\'' +
                ", fechaMatricula=" + fechaMatricula +
                ", estado=" + estado +
                '}';
    }

    public static class MatriculaBuilder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Persona estudiante;
        private AnioEscolar anioEscolar;
        private Grado grado;
        private Seccion seccion;
        private String codigoMatricula;
        private LocalDate fechaMatricula;
        private EstadoMatricula estado;
        private String observaciones;

        public MatriculaBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public MatriculaBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public MatriculaBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public MatriculaBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public MatriculaBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public MatriculaBuilder estudiante(Persona estudiante) {
            this.estudiante = estudiante;
            return this;
        }

        public MatriculaBuilder anioEscolar(AnioEscolar anioEscolar) {
            this.anioEscolar = anioEscolar;
            return this;
        }

        public MatriculaBuilder grado(Grado grado) {
            this.grado = grado;
            return this;
        }

        public MatriculaBuilder seccion(Seccion seccion) {
            this.seccion = seccion;
            return this;
        }

        public MatriculaBuilder codigoMatricula(String codigoMatricula) {
            this.codigoMatricula = codigoMatricula;
            return this;
        }

        public MatriculaBuilder fechaMatricula(LocalDate fechaMatricula) {
            this.fechaMatricula = fechaMatricula;
            return this;
        }

        public MatriculaBuilder estado(EstadoMatricula estado) {
            this.estado = estado;
            return this;
        }

        public MatriculaBuilder observaciones(String observaciones) {
            this.observaciones = observaciones;
            return this;
        }

        public Matricula build() {
            return new Matricula(id, createdAt, updatedAt, createdBy, updatedBy,
                    estudiante, anioEscolar, grado, seccion,
                    codigoMatricula, fechaMatricula, estado, observaciones);
        }
    }
}
