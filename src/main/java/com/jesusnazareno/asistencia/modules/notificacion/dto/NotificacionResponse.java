package com.jesusnazareno.asistencia.modules.notificacion.dto;

import com.jesusnazareno.asistencia.modules.notificacion.domain.enums.TipoNotificacion;

import java.time.LocalDateTime;

/**
 * DTO para la visualización de una notificación interna.
 */
public class NotificacionResponse {

    private Long id;
    private Long usuarioId;
    private TipoNotificacion tipo;
    private String tipoDescripcion;
    private String titulo;
    private String mensaje;
    private String referenciaTipo;
    private Long referenciaId;
    private String urlDestino;
    private Boolean leida;
    private LocalDateTime fechaLectura;
    private String fechaLecturaFormateada;
    private LocalDateTime createdAt;
    private String fechaCreacionFormateada;
    private String tiempoRelativo;

    public NotificacionResponse() {
    }

    public static NotificacionResponseBuilder builder() {
        return new NotificacionResponseBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoNotificacion tipo) {
        this.tipo = tipo;
    }

    public String getTipoDescripcion() {
        return tipoDescripcion;
    }

    public void setTipoDescripcion(String tipoDescripcion) {
        this.tipoDescripcion = tipoDescripcion;
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

    public String getFechaLecturaFormateada() {
        return fechaLecturaFormateada;
    }

    public void setFechaLecturaFormateada(String fechaLecturaFormateada) {
        this.fechaLecturaFormateada = fechaLecturaFormateada;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getFechaCreacionFormateada() {
        return fechaCreacionFormateada;
    }

    public void setFechaCreacionFormateada(String fechaCreacionFormateada) {
        this.fechaCreacionFormateada = fechaCreacionFormateada;
    }

    public String getTiempoRelativo() {
        return tiempoRelativo;
    }

    public void setTiempoRelativo(String tiempoRelativo) {
        this.tiempoRelativo = tiempoRelativo;
    }

    public static class NotificacionResponseBuilder {
        private Long id;
        private Long usuarioId;
        private TipoNotificacion tipo;
        private String tipoDescripcion;
        private String titulo;
        private String mensaje;
        private String referenciaTipo;
        private Long referenciaId;
        private String urlDestino;
        private Boolean leida;
        private LocalDateTime fechaLectura;
        private String fechaLecturaFormateada;
        private LocalDateTime createdAt;
        private String fechaCreacionFormateada;
        private String tiempoRelativo;

        NotificacionResponseBuilder() {
        }

        public NotificacionResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public NotificacionResponseBuilder usuarioId(Long usuarioId) {
            this.usuarioId = usuarioId;
            return this;
        }

        public NotificacionResponseBuilder tipo(TipoNotificacion tipo) {
            this.tipo = tipo;
            return this;
        }

        public NotificacionResponseBuilder tipoDescripcion(String tipoDescripcion) {
            this.tipoDescripcion = tipoDescripcion;
            return this;
        }

        public NotificacionResponseBuilder titulo(String titulo) {
            this.titulo = titulo;
            return this;
        }

        public NotificacionResponseBuilder mensaje(String mensaje) {
            this.mensaje = mensaje;
            return this;
        }

        public NotificacionResponseBuilder referenciaTipo(String referenciaTipo) {
            this.referenciaTipo = referenciaTipo;
            return this;
        }

        public NotificacionResponseBuilder referenciaId(Long referenciaId) {
            this.referenciaId = referenciaId;
            return this;
        }

        public NotificacionResponseBuilder urlDestino(String urlDestino) {
            this.urlDestino = urlDestino;
            return this;
        }

        public NotificacionResponseBuilder leida(Boolean leida) {
            this.leida = leida;
            return this;
        }

        public NotificacionResponseBuilder fechaLectura(LocalDateTime fechaLectura) {
            this.fechaLectura = fechaLectura;
            return this;
        }

        public NotificacionResponseBuilder fechaLecturaFormateada(String fechaLecturaFormateada) {
            this.fechaLecturaFormateada = fechaLecturaFormateada;
            return this;
        }

        public NotificacionResponseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public NotificacionResponseBuilder fechaCreacionFormateada(String fechaCreacionFormateada) {
            this.fechaCreacionFormateada = fechaCreacionFormateada;
            return this;
        }

        public NotificacionResponseBuilder tiempoRelativo(String tiempoRelativo) {
            this.tiempoRelativo = tiempoRelativo;
            return this;
        }

        public NotificacionResponse build() {
            NotificacionResponse response = new NotificacionResponse();
            response.setId(id);
            response.setUsuarioId(usuarioId);
            response.setTipo(tipo);
            response.setTipoDescripcion(tipoDescripcion);
            response.setTitulo(titulo);
            response.setMensaje(mensaje);
            response.setReferenciaTipo(referenciaTipo);
            response.setReferenciaId(referenciaId);
            response.setUrlDestino(urlDestino);
            response.setLeida(leida);
            response.setFechaLectura(fechaLectura);
            response.setFechaLecturaFormateada(fechaLecturaFormateada);
            response.setCreatedAt(createdAt);
            response.setFechaCreacionFormateada(fechaCreacionFormateada);
            response.setTiempoRelativo(tiempoRelativo);
            return response;
        }
    }
}
