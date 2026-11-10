package com.jesusnazareno.asistencia.modules.asistencia.repository;

import com.jesusnazareno.asistencia.modules.asistencia.domain.Justificacion;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoJustificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad Justificacion.
 */
@Repository
public interface JustificacionRepository extends JpaRepository<Justificacion, Long> {

    List<Justificacion> findByAsistenciaId(Long asistenciaId);

    Page<Justificacion> findByAsistenciaId(Long asistenciaId, Pageable pageable);

    Optional<Justificacion> findByAsistenciaIdAndActivoTrue(Long asistenciaId);

    boolean existsByAsistenciaIdAndActivoTrue(Long asistenciaId);

    List<Justificacion> findByEstado(EstadoJustificacion estado);

    Page<Justificacion> findByEstado(EstadoJustificacion estado, Pageable pageable);

    long countByEstadoAndActivoTrue(EstadoJustificacion estado);

    /**
     * Búsqueda paginada de justificaciones con filtros avanzados en base de datos.
     * Utiliza JOIN FETCH para optimizar la carga evitando problemas N+1.
     */
    @Query(value = """
        SELECT j FROM Justificacion j
        JOIN FETCH j.asistencia a
        JOIN FETCH a.matricula m
        JOIN FETCH m.estudiante e
        JOIN FETCH m.grado g
        JOIN FETCH m.seccion s
        JOIN FETCH m.anioEscolar ae
        LEFT JOIN FETCH j.revisadoPor u
        WHERE (:estado IS NULL OR j.estado = :estado)
          AND (:fechaSolicitud IS NULL OR j.fechaSolicitud = :fechaSolicitud)
          AND (:fechaAsistencia IS NULL OR a.fecha = :fechaAsistencia)
          AND (:anioEscolarId IS NULL OR ae.id = :anioEscolarId)
          AND (:gradoId IS NULL OR g.id = :gradoId)
          AND (:seccionId IS NULL OR s.id = :seccionId)
          AND (
              :q IS NULL OR :q = ''
              OR LOWER(e.numeroDocumento) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.nombres) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.apellidoPaterno) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.apellidoMaterno) LIKE LOWER(CONCAT('%', :q, '%'))
          )
    """,
    countQuery = """
        SELECT COUNT(j) FROM Justificacion j
        JOIN j.asistencia a
        JOIN a.matricula m
        JOIN m.estudiante e
        JOIN m.grado g
        JOIN m.seccion s
        JOIN m.anioEscolar ae
        WHERE (:estado IS NULL OR j.estado = :estado)
          AND (:fechaSolicitud IS NULL OR j.fechaSolicitud = :fechaSolicitud)
          AND (:fechaAsistencia IS NULL OR a.fecha = :fechaAsistencia)
          AND (:anioEscolarId IS NULL OR ae.id = :anioEscolarId)
          AND (:gradoId IS NULL OR g.id = :gradoId)
          AND (:seccionId IS NULL OR s.id = :seccionId)
          AND (
              :q IS NULL OR :q = ''
              OR LOWER(e.numeroDocumento) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.nombres) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.apellidoPaterno) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.apellidoMaterno) LIKE LOWER(CONCAT('%', :q, '%'))
          )
    """)
    Page<Justificacion> buscarJustificaciones(
        @Param("q") String q,
        @Param("estado") EstadoJustificacion estado,
        @Param("fechaSolicitud") LocalDate fechaSolicitud,
        @Param("fechaAsistencia") LocalDate fechaAsistencia,
        @Param("anioEscolarId") Long anioEscolarId,
        @Param("gradoId") Long gradoId,
        @Param("seccionId") Long seccionId,
        Pageable pageable
    );
}
