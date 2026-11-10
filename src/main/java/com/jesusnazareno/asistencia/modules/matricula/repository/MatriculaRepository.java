package com.jesusnazareno.asistencia.modules.matricula.repository;

import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad Matricula.
 */
@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    Optional<Matricula> findByCodigoMatricula(String codigoMatricula);

    boolean existsByCodigoMatricula(String codigoMatricula);

    boolean existsByEstudianteIdAndAnioEscolarId(Long estudianteId, Long anioEscolarId);

    Optional<Matricula> findByEstudianteIdAndAnioEscolarId(Long estudianteId, Long anioEscolarId);

    List<Matricula> findByEstudianteId(Long estudianteId);

    Page<Matricula> findByEstudianteId(Long estudianteId, Pageable pageable);

    List<Matricula> findByAnioEscolarId(Long anioEscolarId);

    Page<Matricula> findByAnioEscolarId(Long anioEscolarId, Pageable pageable);

    long countBySeccionIdAndAnioEscolarIdAndEstado(Long seccionId, Long anioEscolarId, EstadoMatricula estado);

    long countByEstado(EstadoMatricula estado);

    List<Matricula> findByEstado(EstadoMatricula estado);

    @Query("SELECT COUNT(m) FROM Matricula m WHERE m.estado = :estado AND m.anioEscolar.activo = true")
    long countByEstadoAndAnioEscolarActivo(@Param("estado") EstadoMatricula estado);

    @Query(value = """
        SELECT m FROM Matricula m
        JOIN FETCH m.estudiante e
        JOIN FETCH m.anioEscolar a
        JOIN FETCH m.grado g
        JOIN FETCH m.seccion s
        WHERE (:anioEscolarId IS NULL OR a.id = :anioEscolarId)
          AND (:gradoId IS NULL OR g.id = :gradoId)
          AND (:seccionId IS NULL OR s.id = :seccionId)
          AND (:estado IS NULL OR m.estado = :estado)
          AND (
              :q IS NULL OR :q = ''
              OR LOWER(m.codigoMatricula) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.numeroDocumento) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.nombres) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.apellidoPaterno) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(e.apellidoMaterno) LIKE LOWER(CONCAT('%', :q, '%'))
          )
    """,
    countQuery = """
        SELECT COUNT(m) FROM Matricula m
        WHERE (:anioEscolarId IS NULL OR m.anioEscolar.id = :anioEscolarId)
          AND (:gradoId IS NULL OR m.grado.id = :gradoId)
          AND (:seccionId IS NULL OR m.seccion.id = :seccionId)
          AND (:estado IS NULL OR m.estado = :estado)
          AND (
              :q IS NULL OR :q = ''
              OR LOWER(m.codigoMatricula) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(m.estudiante.numeroDocumento) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(m.estudiante.nombres) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(m.estudiante.apellidoPaterno) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(m.estudiante.apellidoMaterno) LIKE LOWER(CONCAT('%', :q, '%'))
          )
    """)
    Page<Matricula> buscarMatriculas(
        @Param("q") String q,
        @Param("anioEscolarId") Long anioEscolarId,
        @Param("gradoId") Long gradoId,
        @Param("seccionId") Long seccionId,
        @Param("estado") EstadoMatricula estado,
        Pageable pageable
    );

    @Query("""
        SELECT COUNT(m) FROM Matricula m
        WHERE m.estado = com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula.MATRICULADO
          AND (:anioEscolarId IS NULL OR m.anioEscolar.id = :anioEscolarId)
          AND (:gradoId IS NULL OR m.grado.id = :gradoId)
          AND (:seccionId IS NULL OR m.seccion.id = :seccionId)
    """)
    long countMatriculasActivasConFiltros(
        @Param("anioEscolarId") Long anioEscolarId,
        @Param("gradoId") Long gradoId,
        @Param("seccionId") Long seccionId
    );

    long countByAnioEscolarId(Long anioEscolarId);

    long countByGradoId(Long gradoId);

    long countBySeccionId(Long seccionId);

    @Query("SELECT COUNT(m) FROM Matricula m WHERE m.seccion.id = :seccionId AND m.estado = com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula.MATRICULADO AND (m.anioEscolar.activo = true OR NOT EXISTS (SELECT a FROM AnioEscolar a WHERE a.activo = true))")
    long countMatriculadosActivosEnSeccion(@Param("seccionId") Long seccionId);
}
