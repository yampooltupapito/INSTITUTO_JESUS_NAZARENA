package com.jesusnazareno.asistencia.modules.apoderado.service;

import com.jesusnazareno.asistencia.modules.apoderado.dto.ApoderadoCreateRequest;
import com.jesusnazareno.asistencia.modules.apoderado.dto.ApoderadoResponse;
import com.jesusnazareno.asistencia.modules.apoderado.dto.ApoderadoUpdateRequest;
import com.jesusnazareno.asistencia.modules.apoderado.dto.EstudianteApoderadoRequest;
import com.jesusnazareno.asistencia.modules.apoderado.dto.EstudianteApoderadoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EstudianteApoderadoService {

    Page<ApoderadoResponse> buscarApoderados(String q, Boolean activo, Pageable pageable);

    ApoderadoResponse obtenerApoderadoPorId(Long id);

    ApoderadoResponse registrarApoderado(ApoderadoCreateRequest request);

    void actualizarApoderado(Long id, ApoderadoUpdateRequest request);

    void activarApoderado(Long id);

    void desactivarApoderado(Long id);

    EstudianteApoderadoResponse asignarApoderadoAEstudiante(EstudianteApoderadoRequest request);

    void marcarComoPrincipal(Long estudianteId, Long relacionId);

    void desactivarRelacion(Long estudianteId, Long relacionId);

    List<EstudianteApoderadoResponse> obtenerApoderadosPorEstudiante(Long estudianteId);

    List<EstudianteApoderadoResponse> obtenerRelacionesPorApoderado(Long apoderadoId);
}
