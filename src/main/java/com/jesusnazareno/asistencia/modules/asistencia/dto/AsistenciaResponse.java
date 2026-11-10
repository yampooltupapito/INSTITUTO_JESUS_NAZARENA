package com.jesusnazareno.asistencia.modules.asistencia.dto;

import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO para la respuesta estructurada de un registro de asistencia.
 */
public class AsistenciaResponse {

    private Long id;
    private Long matriculaId;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private String estudianteNumeroDocumento;
    private String gradoNombre;
    private String seccionNombre;
    private LocalDate fecha;
    private EstadoAsistencia estado;
    private LocalTime horaIngreso;
    private LocalTime horaSalida;
    private String observacion;
    private Long registradoPor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AsistenciaResponse() {
    }

    public AsistenciaResponse(Long id, Long matriculaId, Long estudianteId, String estudianteNombreCompleto,
                              String estudianteNumeroDocumento, String gradoNombre, String seccionNombre,
                              LocalDate fecha, EstadoAsistencia estado, LocalTime horaIngreso, LocalTime horaSalida,
                              String observacion, Long registradoPor, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.matriculaId = matriculaId;
        this.estudianteId = estudianteId;
        this.estudianteNombreCompleto = estudianteNombreCompleto;
        this.estudianteNumeroDocumento = estudianteNumeroDocumento;
        this.gradoNombre = gradoNombre;
        this.seccionNombre = seccionNombre;
        this.fecha = fecha;
        this.estado = estado;
        this.horaIngreso = horaIngreso;
        this.horaSalida = horaSalida;
        this.observacion = observacion;
        this.registradoPor = registradoPor;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AsistenciaResponseBuilder builder() {
        return new AsistenciaResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
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

    public String getGradoNombre() {
        return gradoNombre;
    }

    public void setGradoNombre(String gradoNombre) {
        this.gradoNombre = gradoNombre;
    }

    public String getSeccionNombre() {
        return seccionNombre;
    }

    public void setSeccionNombre(String seccionNombre) {
        this.seccionNombre = seccionNombre;
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

    public static class AsistenciaResponseBuilder {
        private Long id;
        private Long matriculaId;
        private Long estudianteId;
        private String estudianteNombreCompleto;
        private String estudianteNumeroDocumento;
        private String gradoNombre;
        private String seccionNombre;
        private LocalDate fecha;
        private EstadoAsistencia estado;
        private LocalTime horaIngreso;
        private LocalTime horaSalida;
        private String observacion;
        private Long registradoPor;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public AsistenciaResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AsistenciaResponseBuilder matriculaId(Long matriculaId) {
            this.matriculaId = matriculaId;
            return this;
        }

        public AsistenciaResponseBuilder estudianteId(Long estudianteId) {
            this.estudianteId = estudianteId;
            return this;
        }

        public AsistenciaResponseBuilder estudianteNombreCompleto(String estudianteNombreCompleto) {
            this.estudianteNombreCompleto = estudianteNombreCompleto;
            return this;
        }

        public AsistenciaResponseBuilder estudianteNumeroDocumento(String estudianteNumeroDocumento) {
            this.estudianteNumeroDocumento = estudianteNumeroDocumento;
            return this;
        }

        public AsistenciaResponseBuilder gradoNombre(String gradoNombre) {
            this.gradoNombre = gradoNombre;
            return this;
        }

        public AsistenciaResponseBuilder seccionNombre(String seccionNombre) {
            this.seccionNombre = seccionNombre;
            return this;
        }

        public AsistenciaResponseBuilder fecha(LocalDate fecha) {
            this.fecha = fecha;
            return this;
        }

        public AsistenciaResponseBuilder estado(EstadoAsistencia estado) {
            this.estado = estado;
            return this;
        }

        public AsistenciaResponseBuilder horaIngreso(LocalTime horaIngreso) {
            this.horaIngreso = horaIngreso;
            return this;
        }

        public AsistenciaResponseBuilder horaSalida(LocalTime horaSalida) {
            this.horaSalida = horaSalida;
            return this;
        }

        public AsistenciaResponseBuilder observacion(String observacion) {
            this.observacion = observacion;
            return this;
        }

        public AsistenciaResponseBuilder registradoPor(Long registradoPor) {
            this.registradoPor = registradoPor;
            return this;
        }

        public AsistenciaResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AsistenciaResponseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public AsistenciaResponse build() {
            return new AsistenciaResponse(id, matriculaId, estudianteId, estudianteNombreCompleto,
                    estudianteNumeroDocumento, gradoNombre, seccionNombre,
                    fecha, estado, horaIngreso, horaSalida, observacion, registradoPor, createdAt, updatedAt);
        }
    }
}
