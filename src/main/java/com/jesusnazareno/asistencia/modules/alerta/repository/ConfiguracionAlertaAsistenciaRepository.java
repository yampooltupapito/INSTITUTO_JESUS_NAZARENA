package com.jesusnazareno.asistencia.modules.alerta.repository;

import com.jesusnazareno.asistencia.modules.alerta.domain.ConfiguracionAlertaAsistencia;
import com.jesusnazareno.asistencia.modules.alerta.domain.enums.TipoAlertaAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad ConfiguracionAlertaAsistencia.
 */
@Repository
public interface ConfiguracionAlertaAsistenciaRepository extends JpaRepository<ConfiguracionAlertaAsistencia, Long> {

    Optional<ConfiguracionAlertaAsistencia> findByTipoAndActivoTrue(TipoAlertaAsistencia tipo);

    Optional<ConfiguracionAlertaAsistencia> findByTipo(TipoAlertaAsistencia tipo);

    List<ConfiguracionAlertaAsistencia> findAllByOrderByTipoAsc();
}
