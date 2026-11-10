package com.jesusnazareno.asistencia.modules.persona.repository;

import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad Persona.
 */
@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    Optional<Persona> findByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumentoAndIdNot(String numeroDocumento, Long id);

    List<Persona> findByActivoTrueOrderByApellidoPaternoAscApellidoMaternoAscNombresAsc();

    @Query("SELECT p FROM Persona p WHERE p.activo = true AND NOT EXISTS (SELECT u FROM com.jesusnazareno.asistencia.modules.acceso.domain.Usuario u WHERE u.persona.id = p.id) ORDER BY p.apellidoPaterno ASC, p.apellidoMaterno ASC, p.nombres ASC")
    List<Persona> findPersonasDisponiblesParaUsuario();
}
