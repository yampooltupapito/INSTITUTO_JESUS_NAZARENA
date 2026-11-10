package com.jesusnazareno.asistencia.modules.alerta.repository;

import com.jesusnazareno.asistencia.modules.alerta.domain.AlertaAsistencia;
import com.jesusnazareno.asistencia.modules.alerta.domain.enums.EstadoAlerta;
import com.jesusnazareno.asistencia.modules.alerta.domain.enums.TipoAlertaAsistencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad AlertaAsistencia.
 */
@Repository
public interface AlertaAsistenciaRepository extends JpaRepository<AlertaAsistencia, Long> {

    Optional<AlertaAsistencia> findFirstByMatriculaIdAndTipoAndFechaInicioPeriodoAndEstadoInAndActivoTrue(
            Long matriculaId,
            TipoAlertaAsistencia tipo,
            LocalDate fechaInicioPeriodo,
            Collection<EstadoAlerta> estados
    );

    List<AlertaAsistencia> findByMatriculaIdAndActivoTrue(Long matriculaId);

    List<AlertaAsistencia> findByMatriculaIdAndTipoAndActivoTrue(Long matriculaId, TipoAlertaAsistencia tipo);

    long countByEstadoAndActivoTrue(EstadoAlerta estado);

    @Query(value = """
        SELECT a FROM AlertaAsistencia a
        JOIN FETCH a.matricula m
        JOIN FETCH m.estudiante e
        JOIN FETCH m.anioEscolar anio
        JOIN FETCH m.grado g
        JOIN FETCH m.seccion s
        LEFT JOIN FETCH a.revisadoPor u
        WHERE (:tipo IS NULL OR a.tipo = :tipo)
          AND (:estado IS NULL OR a.estado = :estado)
          AND (:anioEscolarId IS NULL OR anio.id = :anioEscolarId)
          AND (:gradoId IS NULL OR g.id = :gradoId)
          AND (:seccionId IS NULL OR s.id = :seccionId)
          AND (:fechaInicio IS NULL OR a.fechaDeteccion >= :fechaInicio)
          AND (:fechaFin IS NULL OR a.fechaDeteccion <= :fechaFin)
          AND (
              :q IS NULL OR :q = ''
              OR LOWER(e.numeroDocumento) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.nombres) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.apellidoPaterno) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.apellidoMaterno) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(m.codigoMatricula) LIKE LOWER(CONCAT('%', :q, '%'))
          )
        ORDER BY a.fechaDeteccion DESC, a.id DESC
    """,
    countQuery = """
        SELECT COUNT(a) FROM AlertaAsistencia a
        JOIN a.matricula m
        JOIN m.estudiante e
        JOIN m.anioEscolar anio
        JOIN m.grado g
        JOIN m.seccion s
        WHERE (:tipo IS NULL OR a.tipo = :tipo)
          AND (:estado IS NULL OR a.estado = :estado)
          AND (:anioEscolarId IS NULL OR anio.id = :anioEscolarId)
          AND (:gradoId IS NULL OR g.id = :gradoId)
          AND (:seccionId IS NULL OR s.id = :seccionId)
          AND (:fechaInicio IS NULL OR a.fechaDeteccion >= :fechaInicio)
          AND (:fechaFin IS NULL OR a.fechaDeteccion <= :fechaFin)
          AND (
              :q IS NULL OR :q = ''
              OR LOWER(e.numeroDocumento) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.nombres) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.apellidoPaterno) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.apellidoMaterno) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(m.codigoMatricula) LIKE LOWER(CONCAT('%', :q, '%'))
          )
    """)
    Page<AlertaAsistencia> buscarAlertas(
            @Param("q") String q,
            @Param("tipo") TipoAlertaAsistencia tipo,
            @Param("estado") EstadoAlerta estado,
            @Param("anioEscolarId") Long anioEscolarId,
            @Param("gradoId") Long gradoId,
            @Param("seccionId") Long seccionId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            Pageable pageable
    );
}
