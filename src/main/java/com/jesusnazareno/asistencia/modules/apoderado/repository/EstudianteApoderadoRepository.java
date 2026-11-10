package com.jesusnazareno.asistencia.modules.apoderado.repository;

import com.jesusnazareno.asistencia.modules.apoderado.domain.EstudianteApoderado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad EstudianteApoderado.
 */
@Repository
public interface EstudianteApoderadoRepository extends JpaRepository<EstudianteApoderado, Long> {

    @Query("SELECT ea FROM EstudianteApoderado ea JOIN FETCH ea.apoderado WHERE ea.estudiante.id = :estudianteId ORDER BY ea.esPrincipal DESC, ea.activo DESC, ea.fechaInicio DESC")
    List<EstudianteApoderado> findRelacionesByEstudianteId(@Param("estudianteId") Long estudianteId);

    @Query("SELECT ea FROM EstudianteApoderado ea JOIN FETCH ea.estudiante WHERE ea.apoderado.id = :apoderadoId ORDER BY ea.activo DESC, ea.fechaInicio DESC")
    List<EstudianteApoderado> findRelacionesByApoderadoId(@Param("apoderadoId") Long apoderadoId);

    List<EstudianteApoderado> findByEstudianteIdAndActivoTrue(Long estudianteId);

    List<EstudianteApoderado> findByApoderadoIdAndActivoTrue(Long apoderadoId);

    Optional<EstudianteApoderado> findByEstudianteIdAndApoderadoIdAndActivoTrue(Long estudianteId, Long apoderadoId);

    Optional<EstudianteApoderado> findByEstudianteIdAndEsPrincipalTrueAndActivoTrue(Long estudianteId);

    boolean existsByEstudianteIdAndApoderadoIdAndActivoTrue(Long estudianteId, Long apoderadoId);

    boolean existsByEstudianteIdAndEsPrincipalTrueAndActivoTrue(Long estudianteId);

    long countByApoderadoIdAndActivoTrue(Long apoderadoId);

    long countByEstudianteIdAndActivoTrue(Long estudianteId);
}
