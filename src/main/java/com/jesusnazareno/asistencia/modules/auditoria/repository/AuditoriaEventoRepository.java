package com.jesusnazareno.asistencia.modules.auditoria.repository;

import com.jesusnazareno.asistencia.modules.auditoria.domain.AuditoriaEvento;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad AuditoriaEvento.
 * Solo lectura y registro de nuevos eventos (los eventos son inmutables).
 */
@Repository
public interface AuditoriaEventoRepository extends JpaRepository<AuditoriaEvento, Long>, JpaSpecificationExecutor<AuditoriaEvento> {

    List<AuditoriaEvento> findByUsernameOrderByFechaDesc(String username);

    List<AuditoriaEvento> findByEntidadTipoAndEntidadIdOrderByFechaDesc(String entidadTipo, Long entidadId);

    long countByAccion(AccionAuditoria accion);

    long countByResultado(ResultadoAuditoria resultado);

    Page<AuditoriaEvento> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);
}
