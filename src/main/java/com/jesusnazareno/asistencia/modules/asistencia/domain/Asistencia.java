package com.jesusnazareno.asistencia.modules.asistencia.domain;

import com.jesusnazareno.asistencia.common.persistence.BaseEntity;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
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
import java.time.LocalTime;
import java.util.Objects;

/**
 * Entidad JPA que representa la tabla 'asistencia' en la base de datos.
 */
@Entity
@Table(name = "asistencia")
public class Asistencia extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoAsistencia estado;

    @Column(name = "hora_ingreso")
    private LocalTime horaIngreso;

    @Column(name = "hora_salida")
    private LocalTime horaSalida;

    @Column(name = "observacion", length = 255)
    private String observacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registrado_por", nullable = false)
    private Usuario registradoPor;

    public Asistencia() {
    }

    public Asistencia(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy,
                      Matricula matricula, LocalDate fecha, EstadoAsistencia estado,
                      LocalTime horaIngreso, LocalTime horaSalida, String observacion, Usuario registradoPor) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.matricula = matricula;
        this.fecha = fecha;
        this.estado = estado;
        this.horaIngreso = horaIngreso;
        this.horaSalida = horaSalida;
        this.observacion = observacion;
        this.registradoPor = registradoPor;
    }

    public static AsistenciaBuilder builder() {
        return new AsistenciaBuilder();
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public EstadoAsistencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsistencia estado) {
        this.estado = estado;
    }

    public LocalTime getHoraIngreso() {
        return horaIngreso;
    }

    public void setHoraIngreso(LocalTime horaIngreso) {
        this.horaIngreso = horaIngreso;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Usuario getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(Usuario registradoPor) {
        this.registradoPor = registradoPor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asistencia that = (Asistencia) o;
        if (getId() != null && that.getId() != null) {
            return Objects.equals(getId(), that.getId());
        }
        return Objects.equals(matricula, that.matricula) &&
                Objects.equals(fecha, that.fecha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matricula, fecha);
    }

    @Override
    public String toString() {
        return "Asistencia{" +
                "id=" + getId() +
                ", fecha=" + fecha +
                ", estado=" + estado +
                ", horaIngreso=" + horaIngreso +
                ", horaSalida=" + horaSalida +
                ", registradoPor=" + (registradoPor != null ? registradoPor.getId() : null) +
                '}';
    }

    public static class AsistenciaBuilder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Matricula matricula;
        private LocalDate fecha;
        private EstadoAsistencia estado;
        private LocalTime horaIngreso;
        private LocalTime horaSalida;
        private String observacion;
        private Usuario registradoPor;

        public AsistenciaBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AsistenciaBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AsistenciaBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public AsistenciaBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public AsistenciaBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public AsistenciaBuilder matricula(Matricula matricula) {
            this.matricula = matricula;
            return this;
        }

        public AsistenciaBuilder fecha(LocalDate fecha) {
            this.fecha = fecha;
            return this;
        }

        public AsistenciaBuilder estado(EstadoAsistencia estado) {
            this.estado = estado;
            return this;
        }

        public AsistenciaBuilder horaIngreso(LocalTime horaIngreso) {
            this.horaIngreso = horaIngreso;
            return this;
        }

        public AsistenciaBuilder horaSalida(LocalTime horaSalida) {
            this.horaSalida = horaSalida;
            return this;
        }

        public AsistenciaBuilder observacion(String observacion) {
            this.observacion = observacion;
            return this;
        }

        public AsistenciaBuilder registradoPor(Usuario registradoPor) {
            this.registradoPor = registradoPor;
            return this;
        }

        public Asistencia build() {
            return new Asistencia(id, createdAt, updatedAt, createdBy, updatedBy,
                    matricula, fecha, estado, horaIngreso, horaSalida, observacion, registradoPor);
        }
    }
}
