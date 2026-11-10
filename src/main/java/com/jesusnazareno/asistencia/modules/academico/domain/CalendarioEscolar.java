package com.jesusnazareno.asistencia.modules.academico.domain;

import com.jesusnazareno.asistencia.common.persistence.BaseEntity;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.TipoDiaCalendario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad JPA para la configuración de excepciones y fechas especiales del calendario escolar.
 * Mapea la tabla 'calendario_escolar'.
 */
@Entity
@Table(name = "calendario_escolar")
public class CalendarioEscolar extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anio_escolar_id", nullable = false)
    private AnioEscolar anioEscolar;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_dia", nullable = false, length = 30)
    private TipoDiaCalendario tipoDia;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Column(name = "es_lectivo", nullable = false)
    private Boolean esLectivo;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public CalendarioEscolar() {
    }

    public CalendarioEscolar(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy,
                             AnioEscolar anioEscolar, LocalDate fecha, TipoDiaCalendario tipoDia,
                             String descripcion, Boolean esLectivo, Boolean activo) {
        super(id, createdAt, updatedAt, createdBy, updatedBy);
        this.anioEscolar = anioEscolar;
        this.fecha = fecha;
        this.tipoDia = tipoDia;
        this.descripcion = descripcion;
        this.esLectivo = esLectivo != null ? esLectivo : false;
        this.activo = activo != null ? activo : true;
    }

    public static CalendarioEscolarBuilder builder() {
        return new CalendarioEscolarBuilder();
    }

    public AnioEscolar getAnioEscolar() {
        return anioEscolar;
    }

    public void setAnioEscolar(AnioEscolar anioEscolar) {
        this.anioEscolar = anioEscolar;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public TipoDiaCalendario getTipoDia() {
        return tipoDia;
    }

    public void setTipoDia(TipoDiaCalendario tipoDia) {
        this.tipoDia = tipoDia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getEsLectivo() {
        return esLectivo;
    }

    public void setEsLectivo(Boolean esLectivo) {
        this.esLectivo = esLectivo;
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
        CalendarioEscolar that = (CalendarioEscolar) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "CalendarioEscolar{" +
                "id=" + getId() +
                ", fecha=" + fecha +
                ", tipoDia=" + tipoDia +
                ", esLectivo=" + esLectivo +
                ", activo=" + activo +
                '}';
    }

    public static class CalendarioEscolarBuilder {
        private Long id;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private String updatedBy;
        private AnioEscolar anioEscolar;
        private LocalDate fecha;
        private TipoDiaCalendario tipoDia;
        private String descripcion;
        private Boolean esLectivo;
        private Boolean activo = true;

        public CalendarioEscolarBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CalendarioEscolarBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CalendarioEscolarBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public CalendarioEscolarBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public CalendarioEscolarBuilder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public CalendarioEscolarBuilder anioEscolar(AnioEscolar anioEscolar) {
            this.anioEscolar = anioEscolar;
            return this;
        }

        public CalendarioEscolarBuilder fecha(LocalDate fecha) {
            this.fecha = fecha;
            return this;
        }

        public CalendarioEscolarBuilder tipoDia(TipoDiaCalendario tipoDia) {
            this.tipoDia = tipoDia;
            return this;
        }

        public CalendarioEscolarBuilder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public CalendarioEscolarBuilder esLectivo(Boolean esLectivo) {
            this.esLectivo = esLectivo;
            return this;
        }

        public CalendarioEscolarBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public CalendarioEscolar build() {
            return new CalendarioEscolar(id, createdAt, updatedAt, createdBy, updatedBy,
                    anioEscolar, fecha, tipoDia, descripcion, esLectivo, activo);
        }
    }
}
