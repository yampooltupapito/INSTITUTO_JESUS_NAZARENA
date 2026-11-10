package com.jesusnazareno.asistencia.modules.persona.repository;

import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.PersonaActor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad PersonaActor.
 */
@Repository
public interface PersonaActorRepository extends JpaRepository<PersonaActor, Long> {

    List<PersonaActor> findByPersonaId(Long personaId);

    List<PersonaActor> findByPersonaIdAndActivoTrue(Long personaId);

    Optional<PersonaActor> findByPersonaIdAndTipoActorId(Long personaId, Long tipoActorId);

    Optional<PersonaActor> findByPersonaIdAndTipoActorCodigo(Long personaId, String codigo);

    boolean existsByPersonaIdAndTipoActorCodigoAndActivoTrue(Long personaId, String codigo);

    @Query("SELECT pa.persona FROM PersonaActor pa WHERE pa.tipoActor.codigo = :codigo AND pa.activo = true AND pa.persona.activo = true")
    Page<Persona> findPersonasActivasByTipoActorCodigo(@Param("codigo") String codigo, Pageable pageable);

    long countByTipoActorCodigoAndActivoTrueAndPersonaActivoTrue(String codigo);

    @Query("SELECT pa.persona FROM PersonaActor pa WHERE pa.tipoActor.codigo = :codigo AND pa.activo = true AND pa.persona.activo = true ORDER BY pa.persona.apellidoPaterno ASC, pa.persona.apellidoMaterno ASC, pa.persona.nombres ASC")
    List<Persona> findPersonasActivasByTipoActorCodigoOrderByApellidos(@Param("codigo") String codigo);

    @Query(value = """
        SELECT DISTINCT pa.persona FROM PersonaActor pa
        WHERE pa.tipoActor.codigo = 'ESTUDIANTE'
          AND (
              :activo IS NULL
              OR (:activo = true AND pa.activo = true AND pa.persona.activo = true)
              OR (:activo = false AND (pa.activo = false OR pa.persona.activo = false))
          )
          AND (
              :q IS NULL OR :q = ''
              OR LOWER(pa.persona.numeroDocumento) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(pa.persona.nombres) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(pa.persona.apellidoPaterno) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(pa.persona.apellidoMaterno) LIKE LOWER(CONCAT('%', :q, '%'))
          )
    """,
    countQuery = """
        SELECT COUNT(DISTINCT pa.persona.id) FROM PersonaActor pa
        WHERE pa.tipoActor.codigo = 'ESTUDIANTE'
          AND (
              :activo IS NULL
              OR (:activo = true AND pa.activo = true AND pa.persona.activo = true)
              OR (:activo = false AND (pa.activo = false OR pa.persona.activo = false))
          )
          AND (
              :q IS NULL OR :q = ''
              OR LOWER(pa.persona.numeroDocumento) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(pa.persona.nombres) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(pa.persona.apellidoPaterno) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(pa.persona.apellidoMaterno) LIKE LOWER(CONCAT('%', :q, '%'))
          )
    """)
    Page<Persona> buscarEstudiantes(
        @Param("q") String q,
        @Param("activo") Boolean activo,
        Pageable pageable
    );

    long countByPersonaIdAndActivoTrue(Long personaId);

    List<PersonaActor> findByPersonaIdOrderByFechaInicioDesc(Long personaId);

    List<PersonaActor> findByPersonaIdAndTipoActorCodigoIn(Long personaId, List<String> codigos);

    @Query(value = """
        SELECT DISTINCT p FROM Persona p
        JOIN PersonaActor pa ON pa.persona.id = p.id
        JOIN pa.tipoActor ta
        WHERE ta.codigo IN ('DIRECTOR', 'DOCENTE', 'AUXILIAR')
          AND (:tipoActorCodigo IS NULL OR :tipoActorCodigo = '' OR ta.codigo = :tipoActorCodigo)
          AND (
              :activo IS NULL
              OR (:activo = true AND pa.activo = true AND p.activo = true)
              OR (:activo = false AND (pa.activo = false OR p.activo = false))
          )
          AND (
              :q IS NULL OR :q = ''
              OR LOWER(p.numeroDocumento) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.nombres) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.apellidoPaterno) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.apellidoMaterno) LIKE LOWER(CONCAT('%', :q, '%'))
          )
        ORDER BY p.apellidoPaterno ASC, p.apellidoMaterno ASC, p.nombres ASC
    """,
    countQuery = """
        SELECT COUNT(DISTINCT p.id) FROM Persona p
        JOIN PersonaActor pa ON pa.persona.id = p.id
        JOIN pa.tipoActor ta
        WHERE ta.codigo IN ('DIRECTOR', 'DOCENTE', 'AUXILIAR')
          AND (:tipoActorCodigo IS NULL OR :tipoActorCodigo = '' OR ta.codigo = :tipoActorCodigo)
          AND (
              :activo IS NULL
              OR (:activo = true AND pa.activo = true AND p.activo = true)
              OR (:activo = false AND (pa.activo = false OR p.activo = false))
          )
          AND (
              :q IS NULL OR :q = ''
              OR LOWER(p.numeroDocumento) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.nombres) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.apellidoPaterno) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.apellidoMaterno) LIKE LOWER(CONCAT('%', :q, '%'))
          )
    """)
    Page<Persona> buscarPersonal(
        @Param("q") String q,
        @Param("tipoActorCodigo") String tipoActorCodigo,
        @Param("activo") Boolean activo,
        Pageable pageable
    );

    @Query(value = """
        SELECT DISTINCT p FROM Persona p
        JOIN PersonaActor pa ON pa.persona.id = p.id
        JOIN pa.tipoActor ta
        WHERE ta.codigo = 'PADRE_APODERADO'
          AND (
              :activo IS NULL
              OR (:activo = true AND pa.activo = true AND p.activo = true)
              OR (:activo = false AND (pa.activo = false OR p.activo = false))
          )
          AND (
              :q IS NULL OR :q = ''
              OR LOWER(p.numeroDocumento) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.nombres) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.apellidoPaterno) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.apellidoMaterno) LIKE LOWER(CONCAT('%', :q, '%'))
          )
        ORDER BY p.apellidoPaterno ASC, p.apellidoMaterno ASC, p.nombres ASC
    """,
    countQuery = """
        SELECT COUNT(DISTINCT p.id) FROM Persona p
        JOIN PersonaActor pa ON pa.persona.id = p.id
        JOIN pa.tipoActor ta
        WHERE ta.codigo = 'PADRE_APODERADO'
          AND (
              :activo IS NULL
              OR (:activo = true AND pa.activo = true AND p.activo = true)
              OR (:activo = false AND (pa.activo = false OR p.activo = false))
          )
          AND (
              :q IS NULL OR :q = ''
              OR LOWER(p.numeroDocumento) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.nombres) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.apellidoPaterno) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.apellidoMaterno) LIKE LOWER(CONCAT('%', :q, '%'))
          )
    """)
    Page<Persona> buscarApoderados(
        @Param("q") String q,
        @Param("activo") Boolean activo,
        Pageable pageable
    );
}
