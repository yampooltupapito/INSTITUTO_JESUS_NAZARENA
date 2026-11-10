package com.jesusnazareno.asistencia.modules.academico.dto;

import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para la creación y edición de horarios escolares de asistencia.
 */
public class HorarioAsistenciaRequest {

    @NotNull(message = "{validation.required}")
    private Long anioEscolarId;

    @NotNull(message = "{validation.required}")
    private Turno turno;

    private Long gradoId;

    private Long seccionId;

    @NotNull(message = "{validation.required}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime horaEntrada;

    @NotNull(message = "{validation.required}")
    @Min(value = 0, message = "La tolerancia en minutos no puede ser negativa.")
    private Integer minutosTolerancia;

    @NotNull(message = "{validation.required}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime horaSalida;

    @NotNull(message = "{validation.required}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;

    @NotNull(message = "{validation.required}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;

    public HorarioAsistenciaRequest() {
        this.minutosTolerancia = 0;
    }

    public HorarioAsistenciaRequest(Long anioEscolarId, Turno turno, Long gradoId, Long seccionId,
                                  LocalTime horaEntrada, Integer minutosTolerancia, LocalTime horaSalida,
                                  LocalDate fechaInicio, LocalDate fechaFin) {
        this.anioEscolarId = anioEscolarId;
        this.turno = turno;
        this.gradoId = gradoId;
        this.seccionId = seccionId;
        this.horaEntrada = horaEntrada;
        this.minutosTolerancia = minutosTolerancia != null ? minutosTolerancia : 0;
        this.horaSalida = horaSalida;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public static HorarioAsistenciaRequestBuilder builder() {
        return new HorarioAsistenciaRequestBuilder();
    }

    public Long getAnioEscolarId() {
        return anioEscolarId;
    }

    public void setAnioEscolarId(Long anioEscolarId) {
        this.anioEscolarId = anioEscolarId;
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

    public Long getSeccionId() {
        return seccionId;
    }

    public void setSeccionId(Long seccionId) {
        this.seccionId = seccionId;
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

    public static class HorarioAsistenciaRequestBuilder {
        private Long anioEscolarId;
        private Turno turno;
        private Long gradoId;
        private Long seccionId;
        private LocalTime horaEntrada;
        private Integer minutosTolerancia = 0;
        private LocalTime horaSalida;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;

        HorarioAsistenciaRequestBuilder() {
        }

        public HorarioAsistenciaRequestBuilder anioEscolarId(Long anioEscolarId) {
            this.anioEscolarId = anioEscolarId;
            return this;
        }

        public HorarioAsistenciaRequestBuilder turno(Turno turno) {
            this.turno = turno;
            return this;
        }

        public HorarioAsistenciaRequestBuilder gradoId(Long gradoId) {
            this.gradoId = gradoId;
            return this;
        }

        public HorarioAsistenciaRequestBuilder seccionId(Long seccionId) {
            this.seccionId = seccionId;
            return this;
        }

        public HorarioAsistenciaRequestBuilder horaEntrada(LocalTime horaEntrada) {
            this.horaEntrada = horaEntrada;
            return this;
        }

        public HorarioAsistenciaRequestBuilder minutosTolerancia(Integer minutosTolerancia) {
            this.minutosTolerancia = minutosTolerancia;
            return this;
        }

        public HorarioAsistenciaRequestBuilder horaSalida(LocalTime horaSalida) {
            this.horaSalida = horaSalida;
            return this;
        }

        public HorarioAsistenciaRequestBuilder fechaInicio(LocalDate fechaInicio) {
            this.fechaInicio = fechaInicio;
            return this;
        }

        public HorarioAsistenciaRequestBuilder fechaFin(LocalDate fechaFin) {
            this.fechaFin = fechaFin;
            return this;
        }

        public HorarioAsistenciaRequest build() {
            return new HorarioAsistenciaRequest(anioEscolarId, turno, gradoId, seccionId,
                    horaEntrada, minutosTolerancia, horaSalida, fechaInicio, fechaFin);
        }
    }
}
