package com.jesusnazareno.asistencia.modules.academico.repository;

import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnioEscolarRepository extends JpaRepository<AnioEscolar, Long> {

    Optional<AnioEscolar> findByAnio(Integer anio);

    boolean existsByAnio(Integer anio);

    Optional<AnioEscolar> findByActivoTrue();

    List<AnioEscolar> findByActivoTrueOrderByAnioDesc();

    Optional<AnioEscolar> findFirstByActivoTrueOrderByAnioDesc();

    @Query("SELECT a FROM AnioEscolar a WHERE (:activo IS NULL OR a.activo = :activo) ORDER BY a.anio DESC")
    Page<AnioEscolar> buscarConFiltro(@Param("activo") Boolean activo, Pageable pageable);
}
