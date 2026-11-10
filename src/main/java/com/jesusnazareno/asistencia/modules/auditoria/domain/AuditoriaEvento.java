package com.jesusnazareno.asistencia.modules.auditoria.domain;

import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un registro inmutable de auditoría funcional y trazabilidad.
 * Almacena el qué, quién, cuándo, dónde y resultado de operaciones críticas del sistema.
 */
@Entity
@Table(name = "auditoria_evento")
public class AuditoriaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "username", length = 100)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "accion", nullable = false, length = 50)
    private AccionAuditoria accion;

    @Enumerated(EnumType.STRING)
    @Column(name = "modulo", nullable = false, length = 50)
    private ModuloAuditoria modulo;

    @Column(name = "entidad_tipo", length = 50)
    private String entidadTipo;

    @Column(name = "entidad_id")
    private Long entidadId;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado", nullable = false, length = 20)
    private ResultadoAuditoria resultado;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "direccion_ip", length = 50)
    private String direccionIp;

    @Column(name = "detalles", columnDefinition = "TEXT")
    private String detalles;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AuditoriaEvento() {
    }

    private AuditoriaEvento(Builder builder) {
        this.id = builder.id;
        this.fecha = builder.fecha != null ? builder.fecha : LocalDateTime.now();
        this.usuario = builder.usuario;
        this.username = builder.username;
        this.accion = builder.accion;
        this.modulo = builder.modulo;
        this.entidadTipo = builder.entidadTipo;
        this.entidadId = builder.entidadId;
        this.resultado = builder.resultado != null ? builder.resultado : ResultadoAuditoria.EXITO;
        this.descripcion = builder.descripcion;
        this.direccionIp = builder.direccionIp;
        this.detalles = builder.detalles;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters inmutables (sin setters para proteger la integridad histórica)
    public Long getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getUsername() {
        return username;
    }

    public AccionAuditoria getAccion() {
        return accion;
    }

    public ModuloAuditoria getModulo() {
        return modulo;
    }

    public String getEntidadTipo() {
        return entidadTipo;
    }

    public Long getEntidadId() {
        return entidadId;
    }

    public ResultadoAuditoria getResultado() {
        return resultado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public String getDetalles() {
        return detalles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public static class Builder {
        private Long id;
        private LocalDateTime fecha;
        private Usuario usuario;
        private String username;
        private AccionAuditoria accion;
        private ModuloAuditoria modulo;
        private String entidadTipo;
        private Long entidadId;
        private ResultadoAuditoria resultado = ResultadoAuditoria.EXITO;
        private String descripcion;
        private String direccionIp;
        private String detalles;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder fecha(LocalDateTime fecha) {
            this.fecha = fecha;
            return this;
        }

        public Builder usuario(Usuario usuario) {
            this.usuario = usuario;
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

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AuditoriaEvento build() {
            return new AuditoriaEvento(this);
        }
    }
}
