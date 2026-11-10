package com.jesusnazareno.asistencia.modules.asistencia.dto;

import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO para la solicitud de registro o actualización de asistencia.
 */
public class AsistenciaRequest {

    @NotNull(message = "{validation.required}")
    private Long matriculaId;

    @NotNull(message = "{validation.required}")
    private LocalDate fecha;

    @NotNull(message = "{validation.required}")
    private EstadoAsistencia estado;

    private LocalTime horaIngreso;

    private LocalTime horaSalida;

    @Size(max = 255, message = "{validation.invalid}")
    private String observacion;

    @NotNull(message = "{validation.required}")
    private Long registradoPor;

    public AsistenciaRequest() {
    }

    public AsistenciaRequest(Long matriculaId, LocalDate fecha, EstadoAsistencia estado,
                             LocalTime horaIngreso, LocalTime horaSalida, String observacion, Long registradoPor) {
        this.matriculaId = matriculaId;
        this.fecha = fecha;
        this.estado = estado;
        this.horaIngreso = horaIngreso;
        this.horaSalida = horaSalida;
        this.observacion = observacion;
        this.registradoPor = registradoPor;
    }

    public static AsistenciaRequestBuilder builder() {
        return new AsistenciaRequestBuilder();
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
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

    public Long getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(Long registradoPor) {
        this.registradoPor = registradoPor;
    }

    public static class AsistenciaRequestBuilder {
        private Long matriculaId;
        private LocalDate fecha;
        private EstadoAsistencia estado;
        private LocalTime horaIngreso;
        private LocalTime horaSalida;
        private String observacion;
        private Long registradoPor;

        public AsistenciaRequestBuilder matriculaId(Long matriculaId) {
            this.matriculaId = matriculaId;
            return this;
        }

        public AsistenciaRequestBuilder fecha(LocalDate fecha) {
            this.fecha = fecha;
            return this;
        }

        public AsistenciaRequestBuilder estado(EstadoAsistencia estado) {
            this.estado = estado;
            return this;
        }

        public AsistenciaRequestBuilder horaIngreso(LocalTime horaIngreso) {
            this.horaIngreso = horaIngreso;
            return this;
        }

        public AsistenciaRequestBuilder horaSalida(LocalTime horaSalida) {
            this.horaSalida = horaSalida;
            return this;
        }

        public AsistenciaRequestBuilder observacion(String observacion) {
            this.observacion = observacion;
            return this;
        }

        public AsistenciaRequestBuilder registradoPor(Long registradoPor) {
            this.registradoPor = registradoPor;
            return this;
        }

        public AsistenciaRequest build() {
            return new AsistenciaRequest(matriculaId, fecha, estado, horaIngreso, horaSalida, observacion, registradoPor);
        }
    }
}
