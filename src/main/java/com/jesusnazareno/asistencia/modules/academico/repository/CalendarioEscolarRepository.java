package com.jesusnazareno.asistencia.modules.academico.repository;

import com.jesusnazareno.asistencia.modules.academico.domain.CalendarioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.TipoDiaCalendario;
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
 * Repositorio JPA para la entidad CalendarioEscolar.
 */
@Repository
public interface CalendarioEscolarRepository extends JpaRepository<CalendarioEscolar, Long> {

    @Query("""
        SELECT c FROM CalendarioEscolar c
        JOIN FETCH c.anioEscolar a
        WHERE c.anioEscolar.id = :anioEscolarId
          AND c.fecha = :fecha
          AND c.activo = true
    """)
    Optional<CalendarioEscolar> findByAnioEscolarIdAndFechaAndActivoTrue(
            @Param("anioEscolarId") Long anioEscolarId,
            @Param("fecha") LocalDate fecha
    );

    @Query("""
        SELECT c FROM CalendarioEscolar c
        JOIN FETCH c.anioEscolar a
        WHERE c.anioEscolar.id = :anioEscolarId
          AND c.fecha = :fecha
    """)
    Optional<CalendarioEscolar> findByAnioEscolarIdAndFecha(
            @Param("anioEscolarId") Long anioEscolarId,
            @Param("fecha") LocalDate fecha
    );

    boolean existsByAnioEscolarIdAndFechaAndActivoTrue(Long anioEscolarId, LocalDate fecha);

    boolean existsByAnioEscolarIdAndFechaAndActivoTrueAndIdNot(Long anioEscolarId, LocalDate fecha, Long id);

    @Query("""
        SELECT c FROM CalendarioEscolar c
        JOIN FETCH c.anioEscolar a
        WHERE (:anioEscolarId IS NULL OR c.anioEscolar.id = :anioEscolarId)
          AND (:tipoDia IS NULL OR c.tipoDia = :tipoDia)
          AND (:esLectivo IS NULL OR c.esLectivo = :esLectivo)
          AND (:fechaInicio IS NULL OR c.fecha >= :fechaInicio)
          AND (:fechaFin IS NULL OR c.fecha <= :fechaFin)
        ORDER BY c.fecha DESC, c.id DESC
    """)
    Page<CalendarioEscolar> buscarConFiltros(
            @Param("anioEscolarId") Long anioEscolarId,
            @Param("tipoDia") TipoDiaCalendario tipoDia,
            @Param("esLectivo") Boolean esLectivo,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            Pageable pageable
    );

    List<CalendarioEscolar> findByAnioEscolarIdAndActivoTrueOrderByFechaAsc(Long anioEscolarId);

    List<CalendarioEscolar> findByAnioEscolarIdAndFechaBetweenAndActivoTrueOrderByFechaAsc(
            Long anioEscolarId, LocalDate fechaInicio, LocalDate fechaFin
    );
}
