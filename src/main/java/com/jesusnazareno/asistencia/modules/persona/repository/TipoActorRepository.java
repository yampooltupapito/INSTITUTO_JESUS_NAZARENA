package com.jesusnazareno.asistencia.modules.persona.repository;

import com.jesusnazareno.asistencia.modules.persona.domain.TipoActor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad TipoActor.
 */
@Repository
public interface TipoActorRepository extends JpaRepository<TipoActor, Long> {

    Optional<TipoActor> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}
