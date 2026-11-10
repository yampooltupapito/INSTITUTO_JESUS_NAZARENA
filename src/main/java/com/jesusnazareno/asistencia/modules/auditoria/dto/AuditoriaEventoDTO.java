package com.jesusnazareno.asistencia.modules.auditoria.dto;

import com.jesusnazareno.asistencia.modules.auditoria.domain.AuditoriaEvento;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria;

import java.time.LocalDateTime;

/**
 * DTO para la visualización y transferencia segura de información de auditoría a la vista web.
 */
public class AuditoriaEventoDTO {

    private Long id;
    private LocalDateTime fecha;
    private Long usuarioId;
    private String username;
    private String nombreCompletoUsuario;
    private AccionAuditoria accion;
    private ModuloAuditoria modulo;
    private String entidadTipo;
    private Long entidadId;
    private ResultadoAuditoria resultado;
    private String descripcion;
    private String direccionIp;
    private String detalles;

    public AuditoriaEventoDTO() {
    }

    public AuditoriaEventoDTO(Long id, LocalDateTime fecha, Long usuarioId, String username,
                              String nombreCompletoUsuario, AccionAuditoria accion, ModuloAuditoria modulo,
                              String entidadTipo, Long entidadId, ResultadoAuditoria resultado,
                              String descripcion, String direccionIp, String detalles) {
        this.id = id;
        this.fecha = fecha;
        this.usuarioId = usuarioId;
        this.username = username;
        this.nombreCompletoUsuario = nombreCompletoUsuario;
        this.accion = accion;
        this.modulo = modulo;
        this.entidadTipo = entidadTipo;
        this.entidadId = entidadId;
        this.resultado = resultado;
        this.descripcion = descripcion;
        this.direccionIp = direccionIp;
        this.detalles = detalles;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private LocalDateTime fecha;
        private Long usuarioId;
        private String username;
        private String nombreCompletoUsuario;
        private AccionAuditoria accion;
        private ModuloAuditoria modulo;
        private String entidadTipo;
        private Long entidadId;
        private ResultadoAuditoria resultado;
        private String descripcion;
        private String direccionIp;
        private String detalles;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder fecha(LocalDateTime fecha) {
            this.fecha = fecha;
            return this;
        }

        public Builder usuarioId(Long usuarioId) {
            this.usuarioId = usuarioId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder nombreCompletoUsuario(String nombreCompletoUsuario) {
            this.nombreCompletoUsuario = nombreCompletoUsuario;
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

        public Builder entidadTipo(String entidadTipo) {
            this.entidadTipo = entidadTipo;
            return this;
        }

        public Builder entidadId(Long entidadId) {
            this.entidadId = entidadId;
            return this;
        }

        public Builder resultado(ResultadoAuditoria resultado) {
            this.resultado = resultado;
            return this;
        }

        public Builder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public Builder direccionIp(String direccionIp) {
            this.direccionIp = direccionIp;
            return this;
        }

        public Builder detalles(String detalles) {
            this.detalles = detalles;
            return this;
        }

        public AuditoriaEventoDTO build() {
            return new AuditoriaEventoDTO(id, fecha, usuarioId, username, nombreCompletoUsuario,
                    accion, modulo, entidadTipo, entidadId, resultado, descripcion, direccionIp, detalles);
        }
    }

    public static AuditoriaEventoDTO fromEntity(AuditoriaEvento entity) {
        if (entity == null) return null;
        AuditoriaEventoDTO dto = new AuditoriaEventoDTO();
        dto.setId(entity.getId());
        dto.setFecha(entity.getFecha());
        if (entity.getUsuario() != null) {
            dto.setUsuarioId(entity.getUsuario().getId());
            if (entity.getUsuario().getPersona() != null) {
                var p = entity.getUsuario().getPersona();
                String nom = p.getNombres() != null ? p.getNombres() : "";
                String ape = p.getApellidoPaterno() != null ? p.getApellidoPaterno() : "";
                dto.setNombreCompletoUsuario((nom + " " + ape).trim());
            }
        }
        dto.setUsername(entity.getUsername());
        dto.setAccion(entity.getAccion());
        dto.setModulo(entity.getModulo());
        dto.setEntidadTipo(entity.getEntidadTipo());
        dto.setEntidadId(entity.getEntidadId());
        dto.setResultado(entity.getResultado());
        dto.setDescripcion(entity.getDescripcion());
        dto.setDireccionIp(entity.getDireccionIp());
        dto.setDetalles(entity.getDetalles());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNombreCompletoUsuario() {
        return nombreCompletoUsuario;
    }

    public void setNombreCompletoUsuario(String nombreCompletoUsuario) {
        this.nombreCompletoUsuario = nombreCompletoUsuario;
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

    public ResultadoAuditoria getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoAuditoria resultado) {
        this.resultado = resultado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }
}
