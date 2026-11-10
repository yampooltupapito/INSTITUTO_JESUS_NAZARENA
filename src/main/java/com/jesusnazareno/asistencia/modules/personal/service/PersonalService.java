package com.jesusnazareno.asistencia.modules.personal.service;

import com.jesusnazareno.asistencia.modules.personal.dto.PersonalCreateRequest;
import com.jesusnazareno.asistencia.modules.personal.dto.PersonalResponse;
import com.jesusnazareno.asistencia.modules.personal.dto.PersonalUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Servicio para la gestión institucional del personal (Director, Docente, Auxiliar).
 */
public interface PersonalService {

    Page<PersonalResponse> listarPersonal(String q, String tipoActor, String estado, Pageable pageable);

    PersonalResponse buscarPorId(Long personaId);

    PersonalResponse registrarPersonal(PersonalCreateRequest request);

    PersonalResponse actualizarPersonal(Long personaId, PersonalUpdateRequest request);

    void activarPersonal(Long personaId, String tipoActorCodigo);

    void desactivarPersonal(Long personaId, String tipoActorCodigo);

    void asignarNuevoActor(Long personaId, String tipoActorCodigo);

    List<String> obtenerTiposPersonalPermitidos();
}
