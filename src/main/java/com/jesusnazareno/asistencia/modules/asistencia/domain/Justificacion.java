package com.jesusnazareno.asistencia.modules.asistencia.domain;

import com.jesusnazareno.asistencia.common.persistence.BaseEntity;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoJustificacion;
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
 * Entidad JPA que representa la tabla 'justificacion' en la base de datos.
 */
@Entity
@Table(name = "justificacion")
public class Justificacion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asistencia_id", nullable = false)
    private Asistencia asistencia;

    @Column(name = "motivo", nullable = false, length = 100)
    private String motivo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDate fechaSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoJustificacion estado;

    @Column(name = "observacion_revision", length = 255)
    private String observacionRevision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revisado_por")
    private Usuario revisadoPor;

    @Column(name = "fecha_revision")
    private LocalDateTime fechaRevision;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public Justificacion() {
    }

    public Justificacion(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy,
                         Asistencia asistencia, String motivo, String descripcion, LocalDate fechaSolicitud,
                         EstadoJustificacion estado, String observacionRevision, Usuario revisadoPor,
                         LocalDateTime fechaRevision, Boolean activo) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.asistencia = asistencia;
        this.motivo = motivo;
        this.descripcion = descripcion;
        this.fechaSolicitud = fechaSolicitud;
        this.estado = estado;
        this.observacionRevision = observacionRevision;
        this.revisadoPor = revisadoPor;
        this.fechaRevision = fechaRevision;
        this.activo = activo;
    }

    public static JustificacionBuilder builder() {
        return new JustificacionBuilder();
    }

    public Asistencia getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(Asistencia asistencia) {
        this.asistencia = asistencia;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public EstadoJustificacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoJustificacion estado) {
        this.estado = estado;
    }

    public String getObservacionRevision() {
        return observacionRevision;
    }

    public void setObservacionRevision(String observacionRevision) {
        this.observacionRevision = observacionRevision;
    }

    public Usuario getRevisadoPor() {
        return revisadoPor;
    }

    public void setRevisadoPor(Usuario revisadoPor) {
        this.revisadoPor = revisadoPor;
    }

    public LocalDateTime getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(LocalDateTime fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Justificacion that = (Justificacion) o;
        if (getId() != null && that.getId() != null) {
            return Objects.equals(getId(), that.getId());
        }
        return Objects.equals(asistencia, that.asistencia) &&
                Objects.equals(fechaSolicitud, that.fechaSolicitud) &&
                Objects.equals(motivo, that.motivo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(asistencia, fechaSolicitud, motivo);
    }

    @Override
    public String toString() {
        return "Justificacion{" +
                "id=" + getId() +
                ", motivo='" + motivo + '\'' +
                ", fechaSolicitud=" + fechaSolicitud +
                ", estado=" + estado +
                ", revisadoPor=" + (revisadoPor != null ? revisadoPor.getId() : null) +
                ", fechaRevision=" + fechaRevision +
                ", activo=" + activo +
                '}';
    }

    public static class JustificacionBuilder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Asistencia asistencia;
        private String motivo;
        private String descripcion;
        private LocalDate fechaSolicitud;
        private EstadoJustificacion estado;
        private String observacionRevision;
        private Usuario revisadoPor;
        private LocalDateTime fechaRevision;
        private Boolean activo;

        public JustificacionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public JustificacionBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public JustificacionBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public JustificacionBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public JustificacionBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public JustificacionBuilder asistencia(Asistencia asistencia) {
            this.asistencia = asistencia;
            return this;
        }

        public JustificacionBuilder motivo(String motivo) {
            this.motivo = motivo;
            return this;
        }

        public JustificacionBuilder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public JustificacionBuilder fechaSolicitud(LocalDate fechaSolicitud) {
            this.fechaSolicitud = fechaSolicitud;
            return this;
        }

        public JustificacionBuilder estado(EstadoJustificacion estado) {
            this.estado = estado;
            return this;
        }

        public JustificacionBuilder observacionRevision(String observacionRevision) {
            this.observacionRevision = observacionRevision;
            return this;
        }

        public JustificacionBuilder revisadoPor(Usuario revisadoPor) {
            this.revisadoPor = revisadoPor;
            return this;
        }

        public JustificacionBuilder fechaRevision(LocalDateTime fechaRevision) {
            this.fechaRevision = fechaRevision;
            return this;
        }

        public JustificacionBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public Justificacion build() {
            return new Justificacion(id, createdAt, updatedAt, createdBy, updatedBy,
                    asistencia, motivo, descripcion, fechaSolicitud,
                    estado, observacionRevision, revisadoPor, fechaRevision, activo);
        }
    }
}
