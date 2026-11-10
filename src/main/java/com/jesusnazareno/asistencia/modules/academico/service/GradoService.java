package com.jesusnazareno.asistencia.modules.academico.service;

import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.dto.GradoRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.GradoResponse;
import com.jesusnazareno.asistencia.modules.academico.dto.SeccionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GradoService {

    GradoResponse crearGrado(GradoRequest request);

    GradoResponse buscarPorId(Long id);

    List<GradoResponse> listarPorNivel(NivelEducativo nivel);

    List<GradoResponse> listarTodos();

    List<GradoResponse> listarActivos();

    Page<GradoResponse> listarPaginado(NivelEducativo nivel, String estado, Pageable pageable);

    GradoResponse actualizarGrado(Long id, GradoRequest request);

    GradoResponse activarGrado(Long id);

    void desactivarGrado(Long id);

    List<SeccionResponse> obtenerSeccionesPorGrado(Long gradoId);
}
