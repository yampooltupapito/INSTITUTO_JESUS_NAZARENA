package com.jesusnazareno.asistencia.modules.academico.service;

import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import com.jesusnazareno.asistencia.modules.academico.dto.SeccionRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.SeccionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SeccionService {

    SeccionResponse crearSeccion(SeccionRequest request);

    SeccionResponse buscarPorId(Long id);

    List<SeccionResponse> listarPorGrado(Long gradoId);

    List<SeccionResponse> listarTodas();

    Page<SeccionResponse> buscarPaginado(NivelEducativo nivel, Long gradoId, Turno turno, String estado, Pageable pageable);

    SeccionResponse actualizarSeccion(Long id, SeccionRequest request);

    SeccionResponse activarSeccion(Long id);

    void desactivarSeccion(Long id);
}
