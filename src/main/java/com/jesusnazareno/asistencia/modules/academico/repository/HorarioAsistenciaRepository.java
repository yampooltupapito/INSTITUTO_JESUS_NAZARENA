package com.jesusnazareno.asistencia.modules.academico.repository;

import com.jesusnazareno.asistencia.modules.academico.domain.HorarioAsistencia;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio JPA para la entidad HorarioAsistencia.
 */
@Repository
public interface HorarioAsistenciaRepository extends JpaRepository<HorarioAsistencia, Long> {

    @Query("""
        SELECT h FROM HorarioAsistencia h
        LEFT JOIN FETCH h.anioEscolar a
        LEFT JOIN FETCH h.grado g
        LEFT JOIN FETCH h.seccion s
        WHERE (:anioEscolarId IS NULL OR h.anioEscolar.id = :anioEscolarId)
          AND (:turno IS NULL OR h.turno = :turno)
          AND (:activo IS NULL OR h.activo = :activo)
        ORDER BY h.fechaInicio DESC, h.horaEntrada ASC
    """)
    Page<HorarioAsistencia> buscarConFiltros(
            @Param("anioEscolarId") Long anioEscolarId,
            @Param("turno") Turno turno,
            @Param("activo") Boolean activo,
            Pageable pageable
    );

    @Query("""
        SELECT h FROM HorarioAsistencia h
        JOIN FETCH h.anioEscolar a
        LEFT JOIN FETCH h.grado g
        LEFT JOIN FETCH h.seccion s
        WHERE h.activo = true
          AND h.anioEscolar.id = :anioEscolarId
          AND h.turno = :turno
          AND h.seccion.id = :seccionId
          AND :fecha BETWEEN h.fechaInicio AND h.fechaFin
        ORDER BY h.id DESC
    """)
    List<HorarioAsistencia> findActivosPorSeccion(
            @Param("anioEscolarId") Long anioEscolarId,
            @Param("turno") Turno turno,
            @Param("seccionId") Long seccionId,
            @Param("fecha") LocalDate fecha
    );

    @Query("""
        SELECT h FROM HorarioAsistencia h
        JOIN FETCH h.anioEscolar a
        LEFT JOIN FETCH h.grado g
        WHERE h.activo = true
          AND h.anioEscolar.id = :anioEscolarId
          AND h.turno = :turno
          AND h.grado.id = :gradoId
          AND h.seccion IS NULL
          AND :fecha BETWEEN h.fechaInicio AND h.fechaFin
        ORDER BY h.id DESC
    """)
    List<HorarioAsistencia> findActivosPorGrado(
            @Param("anioEscolarId") Long anioEscolarId,
            @Param("turno") Turno turno,
            @Param("gradoId") Long gradoId,
            @Param("fecha") LocalDate fecha
    );

    @Query("""
        SELECT h FROM HorarioAsistencia h
        JOIN FETCH h.anioEscolar a
        WHERE h.activo = true
          AND h.anioEscolar.id = :anioEscolarId
          AND h.turno = :turno
          AND h.grado IS NULL
          AND h.seccion IS NULL
          AND :fecha BETWEEN h.fechaInicio AND h.fechaFin
        ORDER BY h.id DESC
    """)
    List<HorarioAsistencia> findActivosPorTurnoGeneral(
            @Param("anioEscolarId") Long anioEscolarId,
            @Param("turno") Turno turno,
            @Param("fecha") LocalDate fecha
    );

    @Query("""
        SELECT h FROM HorarioAsistencia h
        WHERE h.activo = true
          AND h.anioEscolar.id = :anioEscolarId
          AND h.turno = :turno
          AND (:excludeId IS NULL OR h.id <> :excludeId)
          AND (h.fechaInicio <= :fechaFin AND h.fechaFin >= :fechaInicio)
          AND (
            (:seccionId IS NOT NULL AND h.seccion.id = :seccionId) OR
            (:seccionId IS NULL AND :gradoId IS NOT NULL AND h.grado.id = :gradoId AND h.seccion IS NULL) OR
            (:seccionId IS NULL AND :gradoId IS NULL AND h.grado IS NULL AND h.seccion IS NULL)
          )
    """)
    List<HorarioAsistencia> findSolapamientos(
            @Param("anioEscolarId") Long anioEscolarId,
            @Param("turno") Turno turno,
            @Param("gradoId") Long gradoId,
            @Param("seccionId") Long seccionId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("excludeId") Long excludeId
    );

    List<HorarioAsistencia> findByAnioEscolarId(Long anioEscolarId);
}
