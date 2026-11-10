package com.jesusnazareno.asistencia.modules.estudiante.service;

import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.PersonaActor;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaRequest;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Servicio para consultar y gestionar la condición de estudiante de una Persona.
 */
public interface EstudianteService {

    /**
     * Verifica si una persona tiene asignado y activo el rol de estudiante.
     *
     * @param personaId Identificador de la persona
     * @return true si es estudiante activo, false en caso contrario
     */
    boolean esEstudiante(Long personaId);

    /**
     * Obtiene los datos de una persona con perfil de estudiante en formato DTO.
     *
     * @param personaId Identificador de la persona
     * @return PersonaResponse con los datos del estudiante
     */
    PersonaResponse obtenerEstudiantePorId(Long personaId);

    /**
     * Obtiene la entidad Persona garantizando que exista y tenga el perfil activo de estudiante.
     *
     * @param personaId Identificador de la persona
     * @return Entidad Persona
     */
    Persona obtenerEntidadEstudiantePorId(Long personaId);

    /**
     * Lista estudiantes activos con paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de estudiantes en formato PersonaResponse
     */
    Page<PersonaResponse> listarEstudiantesActivos(Pageable pageable);

    /**
     * Lista todos los estudiantes activos sin paginación (para selects).
     */
    List<PersonaResponse> listarTodosEstudiantesActivos();

    /**
     * Asigna el tipo de actor ESTUDIANTE a una persona existente.
     *
     * @param personaId Identificador de la persona
     * @return Relación PersonaActor creada o reactivada
     */
    PersonaActor asignarActorEstudiante(Long personaId);

    /**
     * Búsqueda paginada de estudiantes por término (documento, nombres, apellidos) y filtro de estado (TODOS, ACTIVOS, INACTIVOS).
     */
    Page<PersonaResponse> buscarEstudiantes(String q, String estado, Pageable pageable);

    /**
     * Registra una persona y le asigna el actor ESTUDIANTE en una transacción atómica.
     */
    PersonaResponse registrarEstudiante(PersonaRequest request);

    /**
     * Actualiza los datos personales de un estudiante existente.
     */
    PersonaResponse actualizarEstudiante(Long id, PersonaRequest request);

    /**
     * Desactiva lógicamente al estudiante (activo = false) preservando su historial.
     */
    void desactivarEstudiante(Long id);

    /**
     * Activa al estudiante (activo = true).
     */
    void activarEstudiante(Long id);
}
