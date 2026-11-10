package com.jesusnazareno.asistencia.modules.alerta.domain;

import com.jesusnazareno.asistencia.common.persistence.BaseEntity;
import com.jesusnazareno.asistencia.modules.alerta.domain.enums.TipoAlertaAsistencia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad JPA para la configuración persistente de umbrales de alertas de asistencia.
 * Mapea la tabla 'configuracion_alerta_asistencia'.
 */
@Entity
@Table(name = "configuracion_alerta_asistencia")
public class ConfiguracionAlertaAsistencia extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, unique = true, length = 30)
    private TipoAlertaAsistencia tipo;

    @Column(name = "umbral", nullable = false)
    private Integer umbral;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public ConfiguracionAlertaAsistencia() {
    }

    public ConfiguracionAlertaAsistencia(Long id, LocalDateTime createdAt, LocalDateTime updatedAt,
                                         String createdBy, String updatedBy,
                                         TipoAlertaAsistencia tipo, Integer umbral, Boolean activo) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.tipo = tipo;
        this.umbral = umbral;
        this.activo = activo != null ? activo : true;
    }

    public static ConfiguracionAlertaAsistenciaBuilder builder() {
        return new ConfiguracionAlertaAsistenciaBuilder();
    }

    public TipoAlertaAsistencia getTipo() {
        return tipo;
    }

    public void setTipo(TipoAlertaAsistencia tipo) {
        this.tipo = tipo;
    }

    public Integer getUmbral() {
        return umbral;
    }

    public void setUmbral(Integer umbral) {
        this.umbral = umbral;
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
        if (o == null || getClass() != o.getClass()) return false;
        ConfiguracionAlertaAsistencia that = (ConfiguracionAlertaAsistencia) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ConfiguracionAlertaAsistencia{" +
                "id=" + getId() +
                ", tipo=" + tipo +
                ", umbral=" + umbral +
                ", activo=" + activo +
                '}';
    }

    public static class ConfiguracionAlertaAsistenciaBuilder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private TipoAlertaAsistencia tipo;
        private Integer umbral;
        private Boolean activo = true;

        public ConfiguracionAlertaAsistenciaBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ConfiguracionAlertaAsistenciaBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ConfiguracionAlertaAsistenciaBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public ConfiguracionAlertaAsistenciaBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public ConfiguracionAlertaAsistenciaBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public ConfiguracionAlertaAsistenciaBuilder tipo(TipoAlertaAsistencia tipo) {
            this.tipo = tipo;
            return this;
        }

        public ConfiguracionAlertaAsistenciaBuilder umbral(Integer umbral) {
            this.umbral = umbral;
            return this;
        }

        public ConfiguracionAlertaAsistenciaBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public ConfiguracionAlertaAsistencia build() {
            return new ConfiguracionAlertaAsistencia(id, createdAt, updatedAt, createdBy, updatedBy, tipo, umbral, activo);
        }
    }
}
