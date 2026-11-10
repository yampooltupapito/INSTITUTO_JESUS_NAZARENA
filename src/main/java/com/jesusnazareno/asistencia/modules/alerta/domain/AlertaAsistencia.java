package com.jesusnazareno.asistencia.modules.alerta.domain;

import com.jesusnazareno.asistencia.common.persistence.BaseEntity;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.alerta.domain.enums.EstadoAlerta;
import com.jesusnazareno.asistencia.modules.alerta.domain.enums.TipoAlertaAsistencia;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
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
 * Entidad JPA que representa una alerta interna de asistencia.
 * Mapea la tabla 'alerta_asistencia'.
 */
@Entity
@Table(name = "alerta_asistencia")
public class AlertaAsistencia extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoAlertaAsistencia tipo;

    @Column(name = "fecha_deteccion", nullable = false)
    private LocalDate fechaDeteccion;

    @Column(name = "fecha_inicio_periodo", nullable = false)
    private LocalDate fechaInicioPeriodo;

    @Column(name = "fecha_fin_periodo", nullable = false)
    private LocalDate fechaFinPeriodo;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoAlerta estado;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revisado_por")
    private Usuario revisadoPor;

    @Column(name = "fecha_revision")
    private LocalDateTime fechaRevision;

    @Column(name = "observacion_revision", length = 500)
    private String observacionRevision;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public AlertaAsistencia() {
    }

    public AlertaAsistencia(Long id, LocalDateTime createdAt, LocalDateTime updatedAt,
                            String createdBy, String updatedBy,
                            Matricula matricula, TipoAlertaAsistencia tipo,
                            LocalDate fechaDeteccion, LocalDate fechaInicioPeriodo,
                            LocalDate fechaFinPeriodo, Integer cantidad,
                            EstadoAlerta estado, String descripcion,
                            Usuario revisadoPor, LocalDateTime fechaRevision,
                            String observacionRevision, Boolean activo) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.matricula = matricula;
        this.tipo = tipo;
        this.fechaDeteccion = fechaDeteccion;
        this.fechaInicioPeriodo = fechaInicioPeriodo;
        this.fechaFinPeriodo = fechaFinPeriodo;
        this.cantidad = cantidad;
        this.estado = estado != null ? estado : EstadoAlerta.PENDIENTE;
        this.descripcion = descripcion;
        this.revisadoPor = revisadoPor;
        this.fechaRevision = fechaRevision;
        this.observacionRevision = observacionRevision;
        this.activo = activo != null ? activo : true;
    }

    public static AlertaAsistenciaBuilder builder() {
        return new AlertaAsistenciaBuilder();
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public TipoAlertaAsistencia getTipo() {
        return tipo;
    }

    public void setTipo(TipoAlertaAsistencia tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFechaDeteccion() {
        return fechaDeteccion;
    }

    public void setFechaDeteccion(LocalDate fechaDeteccion) {
        this.fechaDeteccion = fechaDeteccion;
    }

    public LocalDate getFechaInicioPeriodo() {
        return fechaInicioPeriodo;
    }

    public void setFechaInicioPeriodo(LocalDate fechaInicioPeriodo) {
        this.fechaInicioPeriodo = fechaInicioPeriodo;
    }

    public LocalDate getFechaFinPeriodo() {
        return fechaFinPeriodo;
    }

    public void setFechaFinPeriodo(LocalDate fechaFinPeriodo) {
        this.fechaFinPeriodo = fechaFinPeriodo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public EstadoAlerta getEstado() {
        return estado;
    }

    public void setEstado(EstadoAlerta estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public String getObservacionRevision() {
        return observacionRevision;
    }

    public void setObservacionRevision(String observacionRevision) {
        this.observacionRevision = observacionRevision;
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
        AlertaAsistencia that = (AlertaAsistencia) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "AlertaAsistencia{" +
                "id=" + getId() +
                ", tipo=" + tipo +
                ", fechaDeteccion=" + fechaDeteccion +
                ", cantidad=" + cantidad +
                ", estado=" + estado +
                ", activo=" + activo +
                '}';
    }

    public static class AlertaAsistenciaBuilder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Matricula matricula;
        private TipoAlertaAsistencia tipo;
        private LocalDate fechaDeteccion;
        private LocalDate fechaInicioPeriodo;
        private LocalDate fechaFinPeriodo;
        private Integer cantidad;
        private EstadoAlerta estado = EstadoAlerta.PENDIENTE;
        private String descripcion;
        private Usuario revisadoPor;
        private LocalDateTime fechaRevision;
        private String observacionRevision;
        private Boolean activo = true;

        public AlertaAsistenciaBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AlertaAsistenciaBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AlertaAsistenciaBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public AlertaAsistenciaBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public AlertaAsistenciaBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public AlertaAsistenciaBuilder matricula(Matricula matricula) {
            this.matricula = matricula;
            return this;
        }

        public AlertaAsistenciaBuilder tipo(TipoAlertaAsistencia tipo) {
            this.tipo = tipo;
            return this;
        }

        public AlertaAsistenciaBuilder fechaDeteccion(LocalDate fechaDeteccion) {
            this.fechaDeteccion = fechaDeteccion;
            return this;
        }

        public AlertaAsistenciaBuilder fechaInicioPeriodo(LocalDate fechaInicioPeriodo) {
            this.fechaInicioPeriodo = fechaInicioPeriodo;
            return this;
        }

        public AlertaAsistenciaBuilder fechaFinPeriodo(LocalDate fechaFinPeriodo) {
            this.fechaFinPeriodo = fechaFinPeriodo;
            return this;
        }

        public AlertaAsistenciaBuilder cantidad(Integer cantidad) {
            this.cantidad = cantidad;
            return this;
        }

        public AlertaAsistenciaBuilder estado(EstadoAlerta estado) {
            this.estado = estado;
            return this;
        }

        public AlertaAsistenciaBuilder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public AlertaAsistenciaBuilder revisadoPor(Usuario revisadoPor) {
            this.revisadoPor = revisadoPor;
            return this;
        }

        public AlertaAsistenciaBuilder fechaRevision(LocalDateTime fechaRevision) {
            this.fechaRevision = fechaRevision;
            return this;
        }

        public AlertaAsistenciaBuilder observacionRevision(String observacionRevision) {
            this.observacionRevision = observacionRevision;
            return this;
        }

        public AlertaAsistenciaBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public AlertaAsistencia build() {
            return new AlertaAsistencia(id, createdAt, updatedAt, createdBy, updatedBy,
                    matricula, tipo, fechaDeteccion, fechaInicioPeriodo, fechaFinPeriodo,
                    cantidad, estado, descripcion, revisadoPor, fechaRevision,
                    observacionRevision, activo);
        }
    }
}
