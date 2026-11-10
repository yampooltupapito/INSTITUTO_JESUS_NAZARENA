package com.jesusnazareno.asistencia.modules.notificacion.domain;

import com.jesusnazareno.asistencia.common.persistence.BaseEntity;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.notificacion.domain.enums.TipoNotificacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad JPA que representa una notificación interna del sistema.
 * Mapea la tabla 'notificacion'.
 */
@Entity
@Table(name = "notificacion")
public class Notificacion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoNotificacion tipo;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "mensaje", nullable = false, length = 500)
    private String mensaje;

    @Column(name = "referencia_tipo", length = 50)
    private String referenciaTipo;

    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(name = "url_destino", length = 255)
    private String urlDestino;

    @Column(name = "leida", nullable = false)
    private Boolean leida = false;

    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    public Notificacion() {
    }

    public Notificacion(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy,
                        Usuario usuario, TipoNotificacion tipo, String titulo, String mensaje,
                        String referenciaTipo, Long referenciaId, String urlDestino,
                        Boolean leida, LocalDateTime fechaLectura, Boolean activo) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.usuario = usuario;
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.referenciaTipo = referenciaTipo;
        this.referenciaId = referenciaId;
        this.urlDestino = urlDestino;
        this.leida = leida != null ? leida : false;
        this.fechaLectura = fechaLectura;
        this.activo = activo != null ? activo : true;
    }

    public static NotificacionBuilder builder() {
        return new NotificacionBuilder();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoNotificacion tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getReferenciaTipo() {
        return referenciaTipo;
    }

    public void setReferenciaTipo(String referenciaTipo) {
        this.referenciaTipo = referenciaTipo;
    }

    public Long getReferenciaId() {
        return referenciaId;
    }

    public void setReferenciaId(Long referenciaId) {
        this.referenciaId = referenciaId;
    }

    public String getUrlDestino() {
        return urlDestino;
    }

    public void setUrlDestino(String urlDestino) {
        this.urlDestino = urlDestino;
    }

    public Boolean getLeida() {
        return leida;
    }

    public void setLeida(Boolean leida) {
        this.leida = leida;
    }

    public LocalDateTime getFechaLectura() {
        return fechaLectura;
    }

    public void setFechaLectura(LocalDateTime fechaLectura) {
        this.fechaLectura = fechaLectura;
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
        if (!(o instanceof Notificacion that)) return false;
        if (getId() == null || that.getId() == null) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public static class NotificacionBuilder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private Usuario usuario;
        private TipoNotificacion tipo;
        private String titulo;
        private String mensaje;
        private String referenciaTipo;
        private Long referenciaId;
        private String urlDestino;
        private Boolean leida = false;
        private LocalDateTime fechaLectura;
        private Boolean activo = true;

        NotificacionBuilder() {
        }

        public NotificacionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public NotificacionBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public NotificacionBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public NotificacionBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public NotificacionBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public NotificacionBuilder usuario(Usuario usuario) {
            this.usuario = usuario;
            return this;
        }

        public NotificacionBuilder tipo(TipoNotificacion tipo) {
            this.tipo = tipo;
            return this;
        }

        public NotificacionBuilder titulo(String titulo) {
            this.titulo = titulo;
            return this;
        }

        public NotificacionBuilder mensaje(String mensaje) {
            this.mensaje = mensaje;
            return this;
        }

        public NotificacionBuilder referenciaTipo(String referenciaTipo) {
            this.referenciaTipo = referenciaTipo;
            return this;
        }

        public NotificacionBuilder referenciaId(Long referenciaId) {
            this.referenciaId = referenciaId;
            return this;
        }

        public NotificacionBuilder urlDestino(String urlDestino) {
            this.urlDestino = urlDestino;
            return this;
        }

        public NotificacionBuilder leida(Boolean leida) {
            this.leida = leida;
            return this;
        }

        public NotificacionBuilder fechaLectura(LocalDateTime fechaLectura) {
            this.fechaLectura = fechaLectura;
            return this;
        }

        public NotificacionBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public Notificacion build() {
            return new Notificacion(id, createdAt, updatedAt, createdBy, updatedBy,
                    usuario, tipo, titulo, mensaje, referenciaTipo, referenciaId, urlDestino,
                    leida, fechaLectura, activo);
        }
    }
}
