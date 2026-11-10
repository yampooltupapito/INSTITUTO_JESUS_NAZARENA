package com.jesusnazareno.asistencia.modules.acceso.repository;

import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol JOIN FETCH u.persona WHERE u.username = :username")
    Optional<Usuario> findByUsernameWithDetails(@Param("username") String username);

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<Usuario> findByPersonaId(Long personaId);

    boolean existsByPersonaId(Long personaId);

    List<Usuario> findByRolId(Long rolId);

    List<Usuario> findByActivoTrue();

    @Query("SELECT u FROM Usuario u WHERE u.rol.codigo IN :codigos AND u.activo = true")
    List<Usuario> findByRolCodigoInAndActivoTrue(@Param("codigos") java.util.Collection<String> codigos);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol.codigo = :rolCodigo AND u.activo = true")
    long countByRolCodigoAndActivoTrue(@Param("rolCodigo") String rolCodigo);

    @Query(value = """
        SELECT u FROM Usuario u
        JOIN FETCH u.persona p
        JOIN FETCH u.rol r
        WHERE (:rolId IS NULL OR r.id = :rolId)
          AND (:activo IS NULL OR u.activo = :activo)
          AND (
              :q IS NULL OR :q = ''
              OR LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.numeroDocumento) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.nombres) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.apellidoPaterno) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.apellidoMaterno) LIKE LOWER(CONCAT('%', :q, '%'))
          )
    """,
    countQuery = """
        SELECT COUNT(u) FROM Usuario u
        JOIN u.persona p
        JOIN u.rol r
        WHERE (:rolId IS NULL OR r.id = :rolId)
          AND (:activo IS NULL OR u.activo = :activo)
          AND (
              :q IS NULL OR :q = ''
              OR LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.numeroDocumento) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.nombres) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.apellidoPaterno) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(p.apellidoMaterno) LIKE LOWER(CONCAT('%', :q, '%'))
          )
    """)
    org.springframework.data.domain.Page<Usuario> buscarUsuarios(
        @Param("q") String q,
        @Param("rolId") Long rolId,
        @Param("activo") Boolean activo,
        org.springframework.data.domain.Pageable pageable
    );
}
