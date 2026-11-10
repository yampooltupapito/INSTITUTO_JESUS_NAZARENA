package com.jesusnazareno.asistencia.modules.auditoria.dto;

import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO para la captura y binding de filtros de búsqueda de auditoría desde la interfaz web.
 */
public class AuditoriaFiltroDTO {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;

    private String username;
    private AccionAuditoria accion;
    private ModuloAuditoria modulo;
    private ResultadoAuditoria resultado;
    private String entidadTipo;
    private Long entidadId;

    public AuditoriaFiltroDTO() {
    }

    public AuditoriaFiltroDTO(LocalDate fechaInicio, LocalDate fechaFin, String username,
                              AccionAuditoria accion, ModuloAuditoria modulo,
                              ResultadoAuditoria resultado, String entidadTipo, Long entidadId) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.username = username;
        this.accion = accion;
        this.modulo = modulo;
        this.resultado = resultado;
        this.entidadTipo = entidadTipo;
        this.entidadId = entidadId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private String username;
        private AccionAuditoria accion;
        private ModuloAuditoria modulo;
        private ResultadoAuditoria resultado;
        private String entidadTipo;
        private Long entidadId;

        public Builder fechaInicio(LocalDate fechaInicio) {
            this.fechaInicio = fechaInicio;
            return this;
        }

        public Builder fechaFin(LocalDate fechaFin) {
            this.fechaFin = fechaFin;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder accion(AccionAuditoria accion) {
            this.accion = accion;
            return this;
        }

        public Builder modulo(ModuloAuditoria modulo) {
            this.modulo = modulo;
            return this;
        }

        public Builder resultado(ResultadoAuditoria resultado) {
            this.resultado = resultado;
            return this;
        }

        public Builder entidadTipo(String entidadTipo) {
            this.entidadTipo = entidadTipo;
            return this;
        }

        public Builder entidadId(Long entidadId) {
            this.entidadId = entidadId;
            return this;
        }

        public AuditoriaFiltroDTO build() {
            return new AuditoriaFiltroDTO(fechaInicio, fechaFin, username, accion, modulo, resultado, entidadTipo, entidadId);
        }
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public AccionAuditoria getAccion() {
        return accion;
    }

    public void setAccion(AccionAuditoria accion) {
        this.accion = accion;
    }

    public ModuloAuditoria getModulo() {
        return modulo;
    }

    public void setModulo(ModuloAuditoria modulo) {
        this.modulo = modulo;
    }

    public ResultadoAuditoria getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoAuditoria resultado) {
        this.resultado = resultado;
    }

    public String getEntidadTipo() {
        return entidadTipo;
    }

    public void setEntidadTipo(String entidadTipo) {
        this.entidadTipo = entidadTipo;
    }

    public Long getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(Long entidadId) {
        this.entidadId = entidadId;
    }
}
