package com.jesusnazareno.asistencia.modules.academico.repository;

import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradoRepository extends JpaRepository<Grado, Long> {

    List<Grado> findByNivel(NivelEducativo nivel);

    Optional<Grado> findByNivelAndNumero(NivelEducativo nivel, Integer numero);

    boolean existsByNivelAndNumero(NivelEducativo nivel, Integer numero);

    List<Grado> findByActivoTrueOrderByNivelAscNumeroAsc();

    List<Grado> findAllByOrderByNivelAscNumeroAsc();

    @Query("SELECT g FROM Grado g WHERE (:nivel IS NULL OR g.nivel = :nivel) AND (:activo IS NULL OR g.activo = :activo) ORDER BY g.nivel ASC, g.numero ASC")
    Page<Grado> buscarConFiltros(@Param("nivel") NivelEducativo nivel, @Param("activo") Boolean activo, Pageable pageable);
}
