package com.jesusnazareno.asistencia.modules.academico.dto;

import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO de respuesta para la consulta de horarios escolares de asistencia.
 */
public class HorarioAsistenciaResponse {

    private Long id;
    private Long anioEscolarId;
    private Integer anio;
    private String anioNombre;
    private Turno turno;
    private Long gradoId;
    private String gradoNombre;
    private Long seccionId;
    private String seccionNombre;
    private LocalTime horaEntrada;
    private Integer minutosTolerancia;
    private LocalTime horaLimite;
    private LocalTime horaSalida;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activo;
    private String alcance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public HorarioAsistenciaResponse() {
    }

    public HorarioAsistenciaResponse(Long id, Long anioEscolarId, Integer anio, String anioNombre,
                                   Turno turno, Long gradoId, String gradoNombre,
                                   Long seccionId, String seccionNombre,
                                   LocalTime horaEntrada, Integer minutosTolerancia,
                                   LocalTime horaLimite, LocalTime horaSalida,
                                   LocalDate fechaInicio, LocalDate fechaFin,
                                   Boolean activo, String alcance,
                                   LocalDateTime createdAt, LocalDateTime updatedAt,
                                   String createdBy, String updatedBy) {
        this.id = id;
        this.anioEscolarId = anioEscolarId;
        this.anio = anio;
        this.anioNombre = anioNombre;
        this.turno = turno;
        this.gradoId = gradoId;
        this.gradoNombre = gradoNombre;
        this.seccionId = seccionId;
        this.seccionNombre = seccionNombre;
        this.horaEntrada = horaEntrada;
        this.minutosTolerancia = minutosTolerancia;
        this.horaLimite = horaLimite;
        this.horaSalida = horaSalida;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.activo = activo;
        this.alcance = alcance;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public static HorarioAsistenciaResponseBuilder builder() {
        return new HorarioAsistenciaResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAnioEscolarId() {
        return anioEscolarId;
    }

    public void setAnioEscolarId(Long anioEscolarId) {
        this.anioEscolarId = anioEscolarId;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getAnioNombre() {
        return anioNombre;
    }

    public void setAnioNombre(String anioNombre) {
        this.anioNombre = anioNombre;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
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

    public LocalTime getHoraLimite() {
        return horaLimite;
    }

    public void setHoraLimite(LocalTime horaLimite) {
        this.horaLimite = horaLimite;
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

    public String getAlcance() {
        return alcance;
    }

    public void setAlcance(String alcance) {
        this.alcance = alcance;
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

    public static class HorarioAsistenciaResponseBuilder {
        private Long id;
        private Long anioEscolarId;
        private Integer anio;
        private String anioNombre;
        private Turno turno;
        private Long gradoId;
        private String gradoNombre;
        private Long seccionId;
        private String seccionNombre;
        private LocalTime horaEntrada;
        private Integer minutosTolerancia;
        private LocalTime horaLimite;
        private LocalTime horaSalida;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private Boolean activo;
        private String alcance;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;

        HorarioAsistenciaResponseBuilder() {
        }

        public HorarioAsistenciaResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public HorarioAsistenciaResponseBuilder anioEscolarId(Long anioEscolarId) {
            this.anioEscolarId = anioEscolarId;
            return this;
        }

        public HorarioAsistenciaResponseBuilder anio(Integer anio) {
            this.anio = anio;
            return this;
        }

        public HorarioAsistenciaResponseBuilder anioNombre(String anioNombre) {
            this.anioNombre = anioNombre;
            return this;
        }

        public HorarioAsistenciaResponseBuilder turno(Turno turno) {
            this.turno = turno;
            return this;
        }

        public HorarioAsistenciaResponseBuilder gradoId(Long gradoId) {
            this.gradoId = gradoId;
            return this;
        }

        public HorarioAsistenciaResponseBuilder gradoNombre(String gradoNombre) {
            this.gradoNombre = gradoNombre;
            return this;
        }

        public HorarioAsistenciaResponseBuilder seccionId(Long seccionId) {
            this.seccionId = seccionId;
            return this;
        }

        public HorarioAsistenciaResponseBuilder seccionNombre(String seccionNombre) {
            this.seccionNombre = seccionNombre;
            return this;
        }

        public HorarioAsistenciaResponseBuilder horaEntrada(LocalTime horaEntrada) {
            this.horaEntrada = horaEntrada;
            return this;
        }

        public HorarioAsistenciaResponseBuilder minutosTolerancia(Integer minutosTolerancia) {
            this.minutosTolerancia = minutosTolerancia;
            return this;
        }

        public HorarioAsistenciaResponseBuilder horaLimite(LocalTime horaLimite) {
            this.horaLimite = horaLimite;
            return this;
        }

        public HorarioAsistenciaResponseBuilder horaSalida(LocalTime horaSalida) {
            this.horaSalida = horaSalida;
            return this;
        }

        public HorarioAsistenciaResponseBuilder fechaInicio(LocalDate fechaInicio) {
            this.fechaInicio = fechaInicio;
            return this;
        }

        public HorarioAsistenciaResponseBuilder fechaFin(LocalDate fechaFin) {
            this.fechaFin = fechaFin;
            return this;
        }

        public HorarioAsistenciaResponseBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public HorarioAsistenciaResponseBuilder alcance(String alcance) {
            this.alcance = alcance;
            return this;
        }

        public HorarioAsistenciaResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public HorarioAsistenciaResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public HorarioAsistenciaResponseBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public HorarioAsistenciaResponseBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public HorarioAsistenciaResponse build() {
            return new HorarioAsistenciaResponse(id, anioEscolarId, anio, anioNombre, turno,
                    gradoId, gradoNombre, seccionId, seccionNombre, horaEntrada, minutosTolerancia,
                    horaLimite, horaSalida, fechaInicio, fechaFin, activo, alcance,
                    createdAt, updatedAt, createdBy, updatedBy);
        }
    }
}
