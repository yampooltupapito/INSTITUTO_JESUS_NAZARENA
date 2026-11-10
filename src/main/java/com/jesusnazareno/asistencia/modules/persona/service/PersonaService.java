package com.jesusnazareno.asistencia.modules.persona.service;

import com.jesusnazareno.asistencia.modules.persona.dto.PersonaRequest;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaResponse;

/**
 * Interfaz de servicio de dominio para la gestión básica de Persona.
 */
public interface PersonaService {

    PersonaResponse crearPersona(PersonaRequest request);

    PersonaResponse buscarPorId(Long id);

    PersonaResponse buscarPorNumeroDocumento(String numeroDocumento);

    PersonaResponse actualizarPersona(Long id, PersonaRequest request);

    void desactivarPersona(Long id);
}
