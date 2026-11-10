package com.jesusnazareno.asistencia.modules.asistencia.repository;

import com.jesusnazareno.asistencia.modules.asistencia.domain.Asistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
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
 * Repositorio Spring Data JPA para la entidad Asistencia.
 */
@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    Optional<Asistencia> findByMatriculaIdAndFecha(Long matriculaId, LocalDate fecha);

    boolean existsByMatriculaIdAndFecha(Long matriculaId, LocalDate fecha);

    List<Asistencia> findByFecha(LocalDate fecha);

    Page<Asistencia> findByFecha(LocalDate fecha, Pageable pageable);

    List<Asistencia> findByMatriculaId(Long matriculaId);

    List<Asistencia> findByMatriculaIdOrderByFechaAsc(Long matriculaId);

    Page<Asistencia> findByMatriculaId(Long matriculaId, Pageable pageable);

    List<Asistencia> findByEstado(EstadoAsistencia estado);

    Page<Asistencia> findByEstado(EstadoAsistencia estado, Pageable pageable);

    List<Asistencia> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    Page<Asistencia> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable);

    List<Asistencia> findByMatriculaIdAndFechaBetween(Long matriculaId, LocalDate fechaInicio, LocalDate fechaFin);

    List<Asistencia> findByMatriculaIdAndFechaBetweenOrderByFechaAsc(Long matriculaId, LocalDate fechaInicio, LocalDate fechaFin);

    Page<Asistencia> findByMatriculaIdAndFechaBetween(Long matriculaId, LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable);

    long countByFechaAndEstado(LocalDate fecha, EstadoAsistencia estado);

    /**
     * Búsqueda avanzada paginada de asistencias con múltiples filtros ejecutados en BD.
     * Usa JOIN FETCH para evitar N+1 al cargar matrícula y estudiante.
     */
    @Query(value = """
        SELECT a FROM Asistencia a
        JOIN FETCH a.matricula m
        JOIN FETCH m.estudiante e
        JOIN FETCH m.grado g
        JOIN FETCH m.seccion s
        WHERE (:seccionId IS NULL OR s.id = :seccionId)
          AND (:gradoId IS NULL OR g.id = :gradoId)
          AND (:anioEscolarId IS NULL OR m.anioEscolar.id = :anioEscolarId)
          AND (:estado IS NULL OR a.estado = :estado)
          AND (:fechaInicio IS NULL OR a.fecha >= :fechaInicio)
          AND (:fechaFin IS NULL OR a.fecha <= :fechaFin)
          AND (
              :q IS NULL OR :q = ''
              OR LOWER(e.numeroDocumento) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.nombres) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.apellidoPaterno) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.apellidoMaterno) LIKE LOWER(CONCAT('%', :q, '%'))
          )
    """,
    countQuery = """
        SELECT COUNT(a) FROM Asistencia a
        JOIN a.matricula m
        JOIN m.estudiante e
        JOIN m.grado g
        JOIN m.seccion s
        WHERE (:seccionId IS NULL OR s.id = :seccionId)
          AND (:gradoId IS NULL OR g.id = :gradoId)
          AND (:anioEscolarId IS NULL OR m.anioEscolar.id = :anioEscolarId)
          AND (:estado IS NULL OR a.estado = :estado)
          AND (:fechaInicio IS NULL OR a.fecha >= :fechaInicio)
          AND (:fechaFin IS NULL OR a.fecha <= :fechaFin)
          AND (
              :q IS NULL OR :q = ''
              OR LOWER(e.numeroDocumento) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.nombres) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.apellidoPaterno) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.apellidoMaterno) LIKE LOWER(CONCAT('%', :q, '%'))
          )
    """)
    Page<Asistencia> buscarAsistencias(
        @Param("q") String q,
        @Param("anioEscolarId") Long anioEscolarId,
        @Param("gradoId") Long gradoId,
        @Param("seccionId") Long seccionId,
        @Param("estado") EstadoAsistencia estado,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin,
        Pageable pageable
    );

    /**
     * Carga las asistencias de una sección en una fecha específica (registro masivo).
     */
    @Query("""
        SELECT a FROM Asistencia a
        JOIN FETCH a.matricula m
        JOIN FETCH m.estudiante e
        WHERE m.seccion.id = :seccionId AND a.fecha = :fecha
    """)
    List<Asistencia> findBySeccionIdAndFecha(@Param("seccionId") Long seccionId,
                                              @Param("fecha") LocalDate fecha);

    /**
     * Consulta matrículas válidas de una sección para mostrar en registro de asistencia.
     * Se hace directamente aquí para evitar N+1 al cargar la grilla.
     */
    @Query("""
        SELECT m FROM com.jesusnazareno.asistencia.modules.matricula.domain.Matricula m
        JOIN FETCH m.estudiante e
        JOIN FETCH m.seccion s
        JOIN FETCH m.grado g
        JOIN FETCH m.anioEscolar a
        WHERE s.id = :seccionId
          AND a.id = :anioEscolarId
          AND m.estado = com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula.MATRICULADO
        ORDER BY e.apellidoPaterno ASC, e.apellidoMaterno ASC, e.nombres ASC
    """)
    List<com.jesusnazareno.asistencia.modules.matricula.domain.Matricula> findMatriculasValidasParaAsistencia(
        @Param("seccionId") Long seccionId,
        @Param("anioEscolarId") Long anioEscolarId
    );

    /**
     * Consulta asistencias para reportes con JOIN FETCH para evitar N+1.
     */
    @Query("""
        SELECT a FROM Asistencia a
        JOIN FETCH a.matricula m
        JOIN FETCH m.estudiante e
        JOIN FETCH m.grado g
        JOIN FETCH m.seccion s
        JOIN FETCH m.anioEscolar anio
        WHERE (:estudianteId IS NULL OR e.id = :estudianteId)
          AND (:seccionId IS NULL OR s.id = :seccionId)
          AND (:gradoId IS NULL OR g.id = :gradoId)
          AND (:anioEscolarId IS NULL OR anio.id = :anioEscolarId)
          AND (:estado IS NULL OR a.estado = :estado)
          AND (:fechaInicio IS NULL OR a.fecha >= :fechaInicio)
          AND (:fechaFin IS NULL OR a.fecha <= :fechaFin)
        ORDER BY a.fecha DESC, e.apellidoPaterno ASC, e.apellidoMaterno ASC, e.nombres ASC
    """)
    List<Asistencia> buscarAsistenciasParaReporte(
        @Param("estudianteId") Long estudianteId,
        @Param("seccionId") Long seccionId,
        @Param("gradoId") Long gradoId,
        @Param("anioEscolarId") Long anioEscolarId,
        @Param("estado") EstadoAsistencia estado,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );

    /**
     * Consulta asistencias paginadas para reportes web.
     */
    @Query(value = """
        SELECT a FROM Asistencia a
        JOIN FETCH a.matricula m
        JOIN FETCH m.estudiante e
        JOIN FETCH m.grado g
        JOIN FETCH m.seccion s
        JOIN FETCH m.anioEscolar anio
        WHERE (:estudianteId IS NULL OR e.id = :estudianteId)
          AND (:seccionId IS NULL OR s.id = :seccionId)
          AND (:gradoId IS NULL OR g.id = :gradoId)
          AND (:anioEscolarId IS NULL OR anio.id = :anioEscolarId)
          AND (:estado IS NULL OR a.estado = :estado)
          AND (:fechaInicio IS NULL OR a.fecha >= :fechaInicio)
          AND (:fechaFin IS NULL OR a.fecha <= :fechaFin)
        ORDER BY a.fecha DESC, e.apellidoPaterno ASC, e.apellidoMaterno ASC, e.nombres ASC
    """,
    countQuery = """
        SELECT COUNT(a) FROM Asistencia a
        JOIN a.matricula m
        JOIN m.estudiante e
        JOIN m.grado g
        JOIN m.seccion s
        JOIN m.anioEscolar anio
        WHERE (:estudianteId IS NULL OR e.id = :estudianteId)
          AND (:seccionId IS NULL OR s.id = :seccionId)
          AND (:gradoId IS NULL OR g.id = :gradoId)
          AND (:anioEscolarId IS NULL OR anio.id = :anioEscolarId)
          AND (:estado IS NULL OR a.estado = :estado)
          AND (:fechaInicio IS NULL OR a.fecha >= :fechaInicio)
          AND (:fechaFin IS NULL OR a.fecha <= :fechaFin)
    """)
    Page<Asistencia> buscarAsistenciasPaginadasParaReporte(
        @Param("estudianteId") Long estudianteId,
        @Param("seccionId") Long seccionId,
        @Param("gradoId") Long gradoId,
        @Param("anioEscolarId") Long anioEscolarId,
        @Param("estado") EstadoAsistencia estado,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin,
        Pageable pageable
    );

    /**
     * Conteo agrupado por estado para cálculo rápido de indicadores en reportes sin instanciar entidades.
     */
    @Query("""
        SELECT a.estado, COUNT(a) FROM Asistencia a
        JOIN a.matricula m
        JOIN m.estudiante e
        JOIN m.grado g
        JOIN m.seccion s
        JOIN m.anioEscolar anio
        WHERE (:estudianteId IS NULL OR e.id = :estudianteId)
          AND (:seccionId IS NULL OR s.id = :seccionId)
          AND (:gradoId IS NULL OR g.id = :gradoId)
          AND (:anioEscolarId IS NULL OR anio.id = :anioEscolarId)
          AND (:estado IS NULL OR a.estado = :estado)
          AND (:fechaInicio IS NULL OR a.fecha >= :fechaInicio)
          AND (:fechaFin IS NULL OR a.fecha <= :fechaFin)
        GROUP BY a.estado
    """)
    List<Object[]> contarPorEstadoParaReporte(
        @Param("estudianteId") Long estudianteId,
        @Param("seccionId") Long seccionId,
        @Param("gradoId") Long gradoId,
        @Param("anioEscolarId") Long anioEscolarId,
        @Param("estado") EstadoAsistencia estado,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );

    /**
     * Conteo agrupado de asistencias de una fecha agrupado por Grado y Estado.
     */
    @Query("""
        SELECT g.id, g.nombre, g.numero, a.estado, COUNT(a)
        FROM Asistencia a
        JOIN a.matricula m
        JOIN m.grado g
        WHERE a.fecha = :fecha
          AND (:anioEscolarId IS NULL OR m.anioEscolar.id = :anioEscolarId)
          AND (:gradoId IS NULL OR g.id = :gradoId)
          AND (:seccionId IS NULL OR m.seccion.id = :seccionId)
        GROUP BY g.id, g.nombre, g.numero, a.estado
        ORDER BY g.numero ASC, g.nombre ASC
    """)
    List<Object[]> contarAsistenciaPorGrado(
        @Param("fecha") LocalDate fecha,
        @Param("anioEscolarId") Long anioEscolarId,
        @Param("gradoId") Long gradoId,
        @Param("seccionId") Long seccionId
    );

    /**
     * Conteo agrupado de asistencias de una fecha agrupado por Sección y Estado.
     */
    @Query("""
        SELECT g.id, g.nombre, s.id, s.nombre, a.estado, COUNT(a)
        FROM Asistencia a
        JOIN a.matricula m
        JOIN m.grado g
        JOIN m.seccion s
        WHERE a.fecha = :fecha
          AND (:anioEscolarId IS NULL OR m.anioEscolar.id = :anioEscolarId)
          AND (:gradoId IS NULL OR g.id = :gradoId)
          AND (:seccionId IS NULL OR s.id = :seccionId)
        GROUP BY g.id, g.nombre, s.id, s.nombre, a.estado
        ORDER BY g.numero ASC, g.nombre ASC, s.nombre ASC
    """)
    List<Object[]> contarAsistenciaPorSeccion(
        @Param("fecha") LocalDate fecha,
        @Param("anioEscolarId") Long anioEscolarId,
        @Param("gradoId") Long gradoId,
        @Param("seccionId") Long seccionId
    );

    /**
     * Conteo agrupado de asistencias para tendencia semanal por Fecha y Estado.
     */
    @Query("""
        SELECT a.fecha, a.estado, COUNT(a)
        FROM Asistencia a
        JOIN a.matricula m
        WHERE a.fecha BETWEEN :fechaInicio AND :fechaFin
          AND (:anioEscolarId IS NULL OR m.anioEscolar.id = :anioEscolarId)
          AND (:gradoId IS NULL OR m.grado.id = :gradoId)
          AND (:seccionId IS NULL OR m.seccion.id = :seccionId)
        GROUP BY a.fecha, a.estado
        ORDER BY a.fecha ASC
    """)
    List<Object[]> contarAsistenciaTendencia(
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin,
        @Param("anioEscolarId") Long anioEscolarId,
        @Param("gradoId") Long gradoId,
        @Param("seccionId") Long seccionId
    );
}

