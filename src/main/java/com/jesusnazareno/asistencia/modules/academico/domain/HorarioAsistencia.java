package com.jesusnazareno.asistencia.modules.academico.domain;

import com.jesusnazareno.asistencia.common.persistence.BaseEntity;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
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
 * Entidad JPA para la configuración de horarios escolares utilizados en el control de asistencia.
 * Mapea la tabla 'horario_asistencia'.
 */
@Entity
@Table(name = "horario_asistencia")
public class HorarioAsistencia extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anio_escolar_id", nullable = false)
    private AnioEscolar anioEscolar;

    @Enumerated(EnumType.STRING)
    @Column(name = "turno", nullable = false, length = 20)
    private Turno turno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grado_id")
    private Grado grado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seccion_id")
    private Seccion seccion;

    @Column(name = "hora_entrada", nullable = false)
    private LocalTime horaEntrada;

    @Column(name = "minutos_tolerancia", nullable = false)
    private Integer minutosTolerancia;

    @Column(name = "hora_salida", nullable = false)
    private LocalTime horaSalida;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public HorarioAsistencia() {
    }

    public HorarioAsistencia(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy,
                             AnioEscolar anioEscolar, Turno turno, Grado grado, Seccion seccion,
                             LocalTime horaEntrada, Integer minutosTolerancia, LocalTime horaSalida,
                             LocalDate fechaInicio, LocalDate fechaFin, Boolean activo) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.anioEscolar = anioEscolar;
        this.turno = turno;
        this.grado = grado;
        this.seccion = seccion;
        this.horaEntrada = horaEntrada;
        this.minutosTolerancia = minutosTolerancia != null ? minutosTolerancia : 0;
        this.horaSalida = horaSalida;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activo = activo != null ? activo : true;
    }

    public static HorarioAsistenciaBuilder builder() {
        return new HorarioAsistenciaBuilder();
    }

    public AnioEscolar getAnioEscolar() {
        return anioEscolar;
    }

    public void setAnioEscolar(AnioEscolar anioEscolar) {
        this.anioEscolar = anioEscolar;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
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

    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public Integer getMinutosTolerancia() {
        return minutosTolerancia;
    }

    public void setMinutosTolerancia(Integer minutosTolerancia) {
        this.minutosTolerancia = minutosTolerancia;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalTime horaSalida) {
        this.horaSalida = horaSalida;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalTime getHoraLimite() {
        if (horaEntrada == null) {
            return null;
        }
        int tol = minutosTolerancia != null ? minutosTolerancia : 0;
        return horaEntrada.plusMinutes(tol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HorarioAsistencia that = (HorarioAsistencia) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "HorarioAsistencia{" +
                "id=" + getId() +
                ", turno=" + turno +
                ", horaEntrada=" + horaEntrada +
                ", minutosTolerancia=" + minutosTolerancia +
                ", horaSalida=" + horaSalida +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", activo=" + activo +
                '}';
    }

    public static class HorarioAsistenciaBuilder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private AnioEscolar anioEscolar;
        private Turno turno;
        private Grado grado;
        private Seccion seccion;
        private LocalTime horaEntrada;
        private Integer minutosTolerancia = 0;
        private LocalTime horaSalida;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private Boolean activo = true;

        HorarioAsistenciaBuilder() {
        }

        public HorarioAsistenciaBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public HorarioAsistenciaBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public HorarioAsistenciaBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public HorarioAsistenciaBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public HorarioAsistenciaBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public HorarioAsistenciaBuilder anioEscolar(AnioEscolar anioEscolar) {
            this.anioEscolar = anioEscolar;
            return this;
        }

        public HorarioAsistenciaBuilder turno(Turno turno) {
            this.turno = turno;
            return this;
        }

        public HorarioAsistenciaBuilder grado(Grado grado) {
            this.grado = grado;
            return this;
        }

        public HorarioAsistenciaBuilder seccion(Seccion seccion) {
            this.seccion = seccion;
            return this;
        }

        public HorarioAsistenciaBuilder horaEntrada(LocalTime horaEntrada) {
            this.horaEntrada = horaEntrada;
            return this;
        }

        public HorarioAsistenciaBuilder minutosTolerancia(Integer minutosTolerancia) {
            this.minutosTolerancia = minutosTolerancia;
            return this;
        }

        public HorarioAsistenciaBuilder horaSalida(LocalTime horaSalida) {
            this.horaSalida = horaSalida;
            return this;
        }

        public HorarioAsistenciaBuilder fechaInicio(LocalDate fechaInicio) {
            this.fechaInicio = fechaInicio;
            return this;
        }

        public HorarioAsistenciaBuilder fechaFin(LocalDate fechaFin) {
            this.fechaFin = fechaFin;
            return this;
        }

        public HorarioAsistenciaBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public HorarioAsistencia build() {
            return new HorarioAsistencia(id, createdAt, updatedAt, createdBy, updatedBy,
                    anioEscolar, turno, grado, seccion, horaEntrada, minutosTolerancia, horaSalida, fechaInicio, fechaFin, activo);
        }
    }
}
