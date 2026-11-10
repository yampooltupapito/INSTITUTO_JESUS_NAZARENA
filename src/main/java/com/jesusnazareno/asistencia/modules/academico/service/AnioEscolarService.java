package com.jesusnazareno.asistencia.modules.academico.service;

import com.jesusnazareno.asistencia.modules.academico.dto.AnioEscolarRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.AnioEscolarResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AnioEscolarService {

    AnioEscolarResponse crearAnioEscolar(AnioEscolarRequest request);

    AnioEscolarResponse buscarPorId(Long id);

    AnioEscolarResponse buscarPorAnio(Integer anio);

    List<AnioEscolarResponse> listarTodos();

    Page<AnioEscolarResponse> listarPaginado(String estado, Pageable pageable);

    AnioEscolarResponse actualizarAnioEscolar(Long id, AnioEscolarRequest request);

    AnioEscolarResponse activarAnioEscolar(Long id);

    void desactivarAnioEscolar(Long id);
}
