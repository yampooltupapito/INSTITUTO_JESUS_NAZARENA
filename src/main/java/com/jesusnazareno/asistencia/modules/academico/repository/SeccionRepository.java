package com.jesusnazareno.asistencia.modules.academico.repository;

import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeccionRepository extends JpaRepository<Seccion, Long> {

    List<Seccion> findByGradoId(Long gradoId);

    List<Seccion> findByGradoIdOrderByNombreAsc(Long gradoId);

    List<Seccion> findByGradoIdAndActivoTrue(Long gradoId);

    Optional<Seccion> findByGradoIdAndNombre(Long gradoId, String nombre);

    boolean existsByGradoIdAndNombre(Long gradoId, String nombre);

    long countByGradoId(Long gradoId);

    @Query(value = """
        SELECT s FROM Seccion s
        JOIN FETCH s.grado g
        WHERE (:nivel IS NULL OR g.nivel = :nivel)
          AND (:gradoId IS NULL OR g.id = :gradoId)
          AND (:turno IS NULL OR s.turno = :turno)
          AND (:activo IS NULL OR s.activo = :activo)
        ORDER BY g.nivel ASC, g.numero ASC, s.nombre ASC
    """,
    countQuery = """
        SELECT COUNT(s) FROM Seccion s
        WHERE (:nivel IS NULL OR s.grado.nivel = :nivel)
          AND (:gradoId IS NULL OR s.grado.id = :gradoId)
          AND (:turno IS NULL OR s.turno = :turno)
          AND (:activo IS NULL OR s.activo = :activo)
    """)
    Page<Seccion> buscarConFiltros(
        @Param("nivel") NivelEducativo nivel,
        @Param("gradoId") Long gradoId,
        @Param("turno") Turno turno,
        @Param("activo") Boolean activo,
        Pageable pageable
    );
}
