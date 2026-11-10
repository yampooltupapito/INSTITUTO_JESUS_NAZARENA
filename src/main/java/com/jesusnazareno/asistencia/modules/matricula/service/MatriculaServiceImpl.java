package com.jesusnazareno.asistencia.modules.matricula.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.repository.AnioEscolarRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.GradoRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.SeccionRepository;
import com.jesusnazareno.asistencia.modules.estudiante.service.EstudianteService;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.dto.MatriculaRequest;
import com.jesusnazareno.asistencia.modules.matricula.dto.MatriculaResponse;
import com.jesusnazareno.asistencia.modules.matricula.mapper.MatriculaMapper;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de la capa de servicio para la gestión de matrículas.
 */
@Service
public class MatriculaServiceImpl implements MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final MatriculaMapper matriculaMapper;
    private final EstudianteService estudianteService;
    private final AnioEscolarRepository anioEscolarRepository;
    private final GradoRepository gradoRepository;
    private final SeccionRepository seccionRepository;
    private final com.jesusnazareno.asistencia.modules.auditoria.service.AuditoriaService auditoriaService;

    public MatriculaServiceImpl(MatriculaRepository matriculaRepository,
                                MatriculaMapper matriculaMapper,
                                EstudianteService estudianteService,
                                AnioEscolarRepository anioEscolarRepository,
                                GradoRepository gradoRepository,
                                SeccionRepository seccionRepository) {
        this(matriculaRepository, matriculaMapper, estudianteService, anioEscolarRepository, gradoRepository, seccionRepository, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public MatriculaServiceImpl(MatriculaRepository matriculaRepository,
                                MatriculaMapper matriculaMapper,
                                EstudianteService estudianteService,
                                AnioEscolarRepository anioEscolarRepository,
                                GradoRepository gradoRepository,
                                SeccionRepository seccionRepository,
                                com.jesusnazareno.asistencia.modules.auditoria.service.AuditoriaService auditoriaService) {
        this.matriculaRepository = matriculaRepository;
        this.matriculaMapper = matriculaMapper;
        this.estudianteService = estudianteService;
        this.anioEscolarRepository = anioEscolarRepository;
        this.gradoRepository = gradoRepository;
        this.seccionRepository = seccionRepository;
        this.auditoriaService = auditoriaService;
    }

    @Override
    @Transactional
    public MatriculaResponse crearMatricula(MatriculaRequest request) {
        if (request == null) {
            throw new BusinessException("La solicitud de matrícula no puede ser nula.");
        }

        // 1 y 2. Verificar existencia de Persona y perfil activo de ESTUDIANTE
        Persona estudiante = estudianteService.obtenerEntidadEstudiantePorId(request.getPersonaEstudianteId());
        if (!Boolean.TRUE.equals(estudiante.getActivo())) {
            throw new BusinessException("La persona seleccionada no se encuentra activa en el sistema.");
        }

        // 3. Verificar existencia y estado del Año Escolar
        AnioEscolar anioEscolar = anioEscolarRepository.findById(request.getAnioEscolarId())
                .orElseThrow(() -> new ResourceNotFoundException("Año escolar no encontrado con ID: " + request.getAnioEscolarId()));
        if (!Boolean.TRUE.equals(anioEscolar.getActivo())) {
            throw new BusinessException("El año escolar seleccionado no está activo.");
        }

        // 4. Verificar existencia y estado del Grado
        Grado grado = gradoRepository.findById(request.getGradoId())
                .orElseThrow(() -> new ResourceNotFoundException("Grado no encontrado con ID: " + request.getGradoId()));
        if (!Boolean.TRUE.equals(grado.getActivo())) {
            throw new BusinessException("El grado seleccionado no está activo.");
        }

        // 5. Verificar existencia y estado de la Sección
        Seccion seccion = seccionRepository.findById(request.getSeccionId())
                .orElseThrow(() -> new ResourceNotFoundException("Sección no encontrada con ID: " + request.getSeccionId()));
        if (!Boolean.TRUE.equals(seccion.getActivo())) {
            throw new BusinessException("La sección seleccionada no está activa.");
        }

        // 6. Verificar que la Sección pertenezca al Grado indicado (usando equals de Long)
        if (seccion.getGrado() == null || !seccion.getGrado().getId().equals(grado.getId())) {
            throw new BusinessException("La sección no pertenece al grado seleccionado.");
        }

        // 7. No permitir doble matrícula del mismo estudiante en el mismo año escolar
        if (matriculaRepository.existsByEstudianteIdAndAnioEscolarId(estudiante.getId(), anioEscolar.getId())) {
            throw new BusinessException("El estudiante ya tiene una matrícula para este año escolar.");
        }

        // 8. Verificar que el código de matrícula sea único
        String codigoMatricula = request.getCodigoMatricula().trim();
        if (matriculaRepository.existsByCodigoMatricula(codigoMatricula)) {
            throw new BusinessException("El código de matrícula ya existe.");
        }

        // 9. Verificar capacidad de la sección para el año escolar
        EstadoMatricula estado = request.getEstado() != null ? request.getEstado() : EstadoMatricula.MATRICULADO;
        if (estado == EstadoMatricula.MATRICULADO) {
            validarCapacidadSeccion(seccion, anioEscolar.getId(), null);
        }

        Matricula matricula = matriculaMapper.toEntity(request, estudiante, anioEscolar, grado, seccion);
        try {
            Matricula guardada = matriculaRepository.save(matricula);
            if (auditoriaService != null) {
                auditoriaService.registrarEventoConContexto(
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.MATRICULA_CREAR,
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.MATRICULAS,
                        "MATRICULA",
                        guardada.getId(),
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                        "Se registró matrícula: " + guardada.getCodigoMatricula(),
                        "Estudiante ID: " + estudiante.getId() + ", Año: " + anioEscolar.getAnio()
                );
            }
            return matriculaMapper.toResponse(guardada);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Error de concurrencia o duplicidad al registrar la matrícula.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MatriculaResponse buscarPorId(Long id) {
        if (id == null) {
            throw new ResourceNotFoundException("Identificador de matrícula no puede ser nulo.");
        }
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula no encontrada con ID: " + id));
        return matriculaMapper.toResponse(matricula);
    }

    @Override
    @Transactional
    public MatriculaResponse actualizarMatricula(Long id, MatriculaRequest request) {
        if (id == null) {
            throw new ResourceNotFoundException("Identificador de matrícula no puede ser nulo.");
        }
        if (request == null) {
            throw new BusinessException("La solicitud de actualización de matrícula no puede ser nula.");
        }

        Matricula matriculaExistente = matriculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula no encontrada con ID: " + id));

        // 1 y 2. Verificar existencia y rol de estudiante
        Persona estudiante = estudianteService.obtenerEntidadEstudiantePorId(request.getPersonaEstudianteId());
        if (!Boolean.TRUE.equals(estudiante.getActivo())) {
            throw new BusinessException("La persona seleccionada no se encuentra activa en el sistema.");
        }

        // 3. Año Escolar
        AnioEscolar anioEscolar = anioEscolarRepository.findById(request.getAnioEscolarId())
                .orElseThrow(() -> new ResourceNotFoundException("Año escolar no encontrado con ID: " + request.getAnioEscolarId()));
        if (!Boolean.TRUE.equals(anioEscolar.getActivo())) {
            throw new BusinessException("El año escolar seleccionado no está activo.");
        }

        // 4. Grado
        Grado grado = gradoRepository.findById(request.getGradoId())
                .orElseThrow(() -> new ResourceNotFoundException("Grado no encontrado con ID: " + request.getGradoId()));
        if (!Boolean.TRUE.equals(grado.getActivo())) {
            throw new BusinessException("El grado seleccionado no está activo.");
        }

        // 5. Sección
        Seccion seccion = seccionRepository.findById(request.getSeccionId())
                .orElseThrow(() -> new ResourceNotFoundException("Sección no encontrada con ID: " + request.getSeccionId()));
        if (!Boolean.TRUE.equals(seccion.getActivo())) {
            throw new BusinessException("La sección seleccionada no está activa.");
        }

        // 6. Sección pertenece al Grado
        if (seccion.getGrado() == null || !seccion.getGrado().getId().equals(grado.getId())) {
            throw new BusinessException("La sección no pertenece al grado seleccionado.");
        }

        // 7. No permitir doble matrícula para estudiante y año escolar en otro registro
        matriculaRepository.findByEstudianteIdAndAnioEscolarId(estudiante.getId(), anioEscolar.getId())
                .ifPresent(otra -> {
                    if (!otra.getId().equals(id)) {
                        throw new BusinessException("El estudiante ya tiene una matrícula para este año escolar.");
                    }
                });

        // 8. Código de matrícula único excluyendo el registro actual
        String codigoMatricula = request.getCodigoMatricula().trim();
        matriculaRepository.findByCodigoMatricula(codigoMatricula)
                .ifPresent(otra -> {
                    if (!otra.getId().equals(id)) {
                        throw new BusinessException("El código de matrícula ya existe.");
                    }
                });

        // 9. Verificar capacidad de la sección
        EstadoMatricula nuevoEstado = request.getEstado() != null ? request.getEstado() : matriculaExistente.getEstado();
        if (nuevoEstado == EstadoMatricula.MATRICULADO) {
            validarCapacidadSeccion(seccion, anioEscolar.getId(), id);
        }

        matriculaMapper.updateEntity(request, matriculaExistente, estudiante, anioEscolar, grado, seccion);
        try {
            Matricula guardada = matriculaRepository.save(matriculaExistente);
            if (auditoriaService != null) {
                auditoriaService.registrarEventoConContexto(
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.MATRICULA_EDITAR,
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.MATRICULAS,
                        "MATRICULA",
                        guardada.getId(),
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                        "Se actualizó matrícula: " + guardada.getCodigoMatricula(),
                        null
                );
            }
            return matriculaMapper.toResponse(guardada);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Error de concurrencia o duplicidad al actualizar la matrícula.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MatriculaResponse> listarPaginado(Pageable pageable) {
        return matriculaRepository.findAll(pageable)
                .map(matriculaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatriculaResponse> listarPorEstudiante(Long personaEstudianteId) {
        if (personaEstudianteId == null) {
            throw new ResourceNotFoundException("Identificador de estudiante no puede ser nulo.");
        }
        return matriculaRepository.findByEstudianteId(personaEstudianteId).stream()
                .map(matriculaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MatriculaResponse> listarPorEstudiante(Long personaEstudianteId, Pageable pageable) {
        if (personaEstudianteId == null) {
            throw new ResourceNotFoundException("Identificador de estudiante no puede ser nulo.");
        }
        return matriculaRepository.findByEstudianteId(personaEstudianteId, pageable)
                .map(matriculaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MatriculaResponse> listarPorAnioEscolar(Long anioEscolarId, Pageable pageable) {
        if (anioEscolarId == null) {
            throw new ResourceNotFoundException("Identificador de año escolar no puede ser nulo.");
        }
        return matriculaRepository.findByAnioEscolarId(anioEscolarId, pageable)
                .map(matriculaMapper::toResponse);
    }

    @Override
    @Transactional
    public MatriculaResponse cambiarEstado(Long id, EstadoMatricula nuevoEstado) {
        if (id == null) {
            throw new ResourceNotFoundException("Identificador de matrícula no puede ser nulo.");
        }
        if (nuevoEstado == null) {
            throw new BusinessException("El nuevo estado de matrícula no puede ser nulo.");
        }

        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula no encontrada con ID: " + id));

        if (nuevoEstado == EstadoMatricula.MATRICULADO && matricula.getEstado() != EstadoMatricula.MATRICULADO) {
            validarCapacidadSeccion(matricula.getSeccion(), matricula.getAnioEscolar().getId(), matricula.getId());
        }

        matricula.setEstado(nuevoEstado);
        Matricula guardada = matriculaRepository.save(matricula);
        if (auditoriaService != null) {
            auditoriaService.registrarEventoConContexto(
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.MATRICULA_CAMBIAR_ESTADO,
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.MATRICULAS,
                    "MATRICULA",
                    guardada.getId(),
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                    "Se cambió estado de matrícula a " + nuevoEstado + ": " + guardada.getCodigoMatricula(),
                    null
            );
        }
        return matriculaMapper.toResponse(guardada);
    }

    private void validarCapacidadSeccion(Seccion seccion, Long anioEscolarId, Long matriculaIdActual) {
        int capacidad = seccion.getCapacidad() != null ? seccion.getCapacidad() : 0;
        long matriculados = matriculaRepository.countBySeccionIdAndAnioEscolarIdAndEstado(
                seccion.getId(), anioEscolarId, EstadoMatricula.MATRICULADO);

        if (matriculaIdActual != null) {
            Matricula actual = matriculaRepository.findById(matriculaIdActual).orElse(null);
            boolean yaContabilizado = actual != null
                    && actual.getSeccion() != null && actual.getSeccion().getId().equals(seccion.getId())
                    && actual.getAnioEscolar() != null && actual.getAnioEscolar().getId().equals(anioEscolarId)
                    && actual.getEstado() == EstadoMatricula.MATRICULADO;

            if (yaContabilizado) {
                matriculados = matriculados - 1;
            }
        }

        if (matriculados >= capacidad) {
            throw new BusinessException("La sección no tiene capacidad disponible.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MatriculaResponse> buscarMatriculas(String q, Long anioEscolarId, Long gradoId, Long seccionId, EstadoMatricula estado, Pageable pageable) {
        String query = (q != null && !q.trim().isEmpty()) ? q.trim() : null;
        Page<Matricula> matriculas = matriculaRepository.buscarMatriculas(query, anioEscolarId, gradoId, seccionId, estado, pageable);
        return matriculas.map(matriculaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.jesusnazareno.asistencia.modules.matricula.dto.SeccionCapacidadDTO> listarSeccionesConCapacidad(Long gradoId, Long anioEscolarId) {
        if (gradoId == null) {
            return java.util.Collections.emptyList();
        }
        List<Seccion> secciones = seccionRepository.findByGradoIdAndActivoTrue(gradoId);
        return secciones.stream().map(s -> {
            int capacidad = s.getCapacidad() != null ? s.getCapacidad() : 0;
            long matriculados = 0;
            if (anioEscolarId != null) {
                matriculados = matriculaRepository.countBySeccionIdAndAnioEscolarIdAndEstado(s.getId(), anioEscolarId, EstadoMatricula.MATRICULADO);
            }
            long vacantes = Math.max(0, capacidad - matriculados);
            return new com.jesusnazareno.asistencia.modules.matricula.dto.SeccionCapacidadDTO(
                    s.getId(), s.getNombre(), s.getTurno(), capacidad, matriculados, vacantes
            );
        }).toList();
    }
}
