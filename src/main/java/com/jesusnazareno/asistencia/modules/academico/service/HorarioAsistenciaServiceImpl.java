package com.jesusnazareno.asistencia.modules.academico.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.HorarioAsistencia;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import com.jesusnazareno.asistencia.modules.academico.dto.HorarioAsistenciaRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.HorarioAsistenciaResponse;
import com.jesusnazareno.asistencia.modules.academico.mapper.HorarioAsistenciaMapper;
import com.jesusnazareno.asistencia.modules.academico.repository.AnioEscolarRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.GradoRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.HorarioAsistenciaRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.SeccionRepository;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación de la lógica de negocio para la gestión y resolución de horarios de asistencia.
 */
@Service
public class HorarioAsistenciaServiceImpl implements HorarioAsistenciaService {

    private final HorarioAsistenciaRepository horarioAsistenciaRepository;
    private final AnioEscolarRepository anioEscolarRepository;
    private final GradoRepository gradoRepository;
    private final SeccionRepository seccionRepository;
    private final HorarioAsistenciaMapper horarioAsistenciaMapper;

    public HorarioAsistenciaServiceImpl(HorarioAsistenciaRepository horarioAsistenciaRepository,
                                       AnioEscolarRepository anioEscolarRepository,
                                       GradoRepository gradoRepository,
                                       SeccionRepository seccionRepository,
                                       HorarioAsistenciaMapper horarioAsistenciaMapper) {
        this.horarioAsistenciaRepository = horarioAsistenciaRepository;
        this.anioEscolarRepository = anioEscolarRepository;
        this.gradoRepository = gradoRepository;
        this.seccionRepository = seccionRepository;
        this.horarioAsistenciaMapper = horarioAsistenciaMapper;
    }

    @Override
    @Transactional
    public HorarioAsistenciaResponse crearHorario(HorarioAsistenciaRequest request) {
        ContextoAcademicoHorario contexto = validarReglasNegocio(request, null);

        HorarioAsistencia entity = horarioAsistenciaMapper.toEntity(
                request, contexto.anioEscolar(), contexto.grado(), contexto.seccion()
        );
        HorarioAsistencia guardado = horarioAsistenciaRepository.save(entity);
        return horarioAsistenciaMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public HorarioAsistenciaResponse buscarPorId(Long id) {
        HorarioAsistencia entity = horarioAsistenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario de asistencia no encontrado con ID: " + id));
        return horarioAsistenciaMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioAsistenciaResponse> listarTodos() {
        return horarioAsistenciaRepository.findAll().stream()
                .map(horarioAsistenciaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HorarioAsistenciaResponse> listarPaginado(Long anioEscolarId, Turno turno, String estado, Pageable pageable) {
        Boolean activo = null;
        if ("ACTIVOS".equalsIgnoreCase(estado)) {
            activo = true;
        } else if ("INACTIVOS".equalsIgnoreCase(estado)) {
            activo = false;
        }
        return horarioAsistenciaRepository.buscarConFiltros(anioEscolarId, turno, activo, pageable)
                .map(horarioAsistenciaMapper::toResponse);
    }

    @Override
    @Transactional
    public HorarioAsistenciaResponse actualizarHorario(Long id, HorarioAsistenciaRequest request) {
        HorarioAsistencia entity = horarioAsistenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario de asistencia no encontrado con ID: " + id));

        ContextoAcademicoHorario contexto = validarReglasNegocio(request, id);

        horarioAsistenciaMapper.updateEntity(
                request, entity, contexto.anioEscolar(), contexto.grado(), contexto.seccion()
        );
        HorarioAsistencia actualizado = horarioAsistenciaRepository.save(entity);
        return horarioAsistenciaMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public HorarioAsistenciaResponse activarHorario(Long id) {
        HorarioAsistencia entity = horarioAsistenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario de asistencia no encontrado con ID: " + id));

        Long gradoId = entity.getGrado() != null ? entity.getGrado().getId() : null;
        Long seccionId = entity.getSeccion() != null ? entity.getSeccion().getId() : null;

        List<HorarioAsistencia> solapamientos = horarioAsistenciaRepository.findSolapamientos(
                entity.getAnioEscolar().getId(),
                entity.getTurno(),
                gradoId,
                seccionId,
                entity.getFechaInicio(),
                entity.getFechaFin(),
                id
        );
        if (!solapamientos.isEmpty()) {
            throw new BusinessException("No se puede activar el horario porque genera solapamiento con otro horario activo en el mismo periodo.");
        }

        entity.setActivo(true);
        HorarioAsistencia actualizado = horarioAsistenciaRepository.save(entity);
        return horarioAsistenciaMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public void desactivarHorario(Long id) {
        HorarioAsistencia entity = horarioAsistenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario de asistencia no encontrado con ID: " + id));
        entity.setActivo(false);
        horarioAsistenciaRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HorarioAsistencia> resolverHorario(LocalDate fecha, Long anioEscolarId, Long gradoId, Long seccionId, Turno turno) {
        if (fecha == null || anioEscolarId == null || turno == null) {
            return Optional.empty();
        }

        // 1. Prioridad 1: Sección específica
        if (seccionId != null) {
            List<HorarioAsistencia> porSeccion = horarioAsistenciaRepository.findActivosPorSeccion(
                    anioEscolarId, turno, seccionId, fecha
            );
            if (!porSeccion.isEmpty()) {
                return Optional.of(porSeccion.get(0));
            }
        }

        // 2. Prioridad 2: Grado específico (sin sección asignada)
        if (gradoId != null) {
            List<HorarioAsistencia> porGrado = horarioAsistenciaRepository.findActivosPorGrado(
                    anioEscolarId, turno, gradoId, fecha
            );
            if (!porGrado.isEmpty()) {
                return Optional.of(porGrado.get(0));
            }
        }

        // 3. Prioridad 3: Turno general del año escolar (sin grado ni sección)
        List<HorarioAsistencia> generales = horarioAsistenciaRepository.findActivosPorTurnoGeneral(
                anioEscolarId, turno, fecha
        );
        if (!generales.isEmpty()) {
            return Optional.of(generales.get(0));
        }

        return Optional.empty();
    }

    @Override
    public EstadoAsistencia determinarEstado(HorarioAsistencia horario, LocalTime horaIngreso) {
        if (horario == null) {
            throw new BusinessException("No existe un horario de asistencia configurado para el contexto seleccionado.");
        }
        if (horaIngreso == null) {
            throw new BusinessException("La hora de ingreso es requerida para evaluar el horario de asistencia.");
        }

        LocalTime horaLimite = calcularHoraLimite(horario);
        if (horaLimite == null) {
            throw new BusinessException("El horario configurado no tiene una hora límite válida.");
        }

        // horaIngreso <= horaLimite -> PRESENTE
        // horaIngreso > horaLimite -> TARDANZA
        if (horaIngreso.compareTo(horaLimite) <= 0) {
            return EstadoAsistencia.PRESENTE;
        } else {
            return EstadoAsistencia.TARDANZA;
        }
    }

    @Override
    public LocalTime calcularHoraLimite(HorarioAsistencia horario) {
        if (horario == null || horario.getHoraEntrada() == null) {
            return null;
        }
        int tol = horario.getMinutosTolerancia() != null ? horario.getMinutosTolerancia() : 0;
        return horario.getHoraEntrada().plusMinutes(tol);
    }

    private ContextoAcademicoHorario validarReglasNegocio(HorarioAsistenciaRequest request, Long idActual) {
        if (request == null) {
            throw new BusinessException("La solicitud de horario no puede ser nula.");
        }

        // Validar vigencia de fechas
        if (request.getFechaFin() != null && request.getFechaInicio() != null
                && request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new BusinessException("El periodo de vigencia no es válido: la fecha de fin no puede ser anterior a la de inicio.");
        }

        // Validar coherencia de horas
        if (request.getHoraEntrada() != null && request.getHoraSalida() != null) {
            if (!request.getHoraSalida().isAfter(request.getHoraEntrada())) {
                throw new BusinessException("La hora de salida debe ser posterior a la hora de entrada.");
            }
        }

        // Validar tolerancia
        if (request.getMinutosTolerancia() != null && request.getMinutosTolerancia() < 0) {
            throw new BusinessException("La tolerancia en minutos no puede ser negativa.");
        }

        // Validar año escolar
        AnioEscolar anioEscolar = anioEscolarRepository.findById(request.getAnioEscolarId())
                .orElseThrow(() -> new ResourceNotFoundException("Año escolar no encontrado con ID: " + request.getAnioEscolarId()));

        // Validar grado si fue especificado
        Grado grado = null;
        if (request.getGradoId() != null) {
            grado = gradoRepository.findById(request.getGradoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Grado no encontrado con ID: " + request.getGradoId()));
        }

        // Validar sección si fue especificada
        Seccion seccion = null;
        if (request.getSeccionId() != null) {
            seccion = seccionRepository.findById(request.getSeccionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sección no encontrada con ID: " + request.getSeccionId()));

            // Consistencia grado y sección
            if (grado != null) {
                if (seccion.getGrado() == null || !seccion.getGrado().getId().equals(grado.getId())) {
                    throw new BusinessException("La sección no pertenece al grado seleccionado.");
                }
            } else {
                // Inferir grado si no vino explícito
                grado = seccion.getGrado();
            }
        }

        // Validar solapamientos
        Long gradoIdQuery = grado != null ? grado.getId() : null;
        Long seccionIdQuery = seccion != null ? seccion.getId() : null;

        List<HorarioAsistencia> solapamientos = horarioAsistenciaRepository.findSolapamientos(
                anioEscolar.getId(),
                request.getTurno(),
                gradoIdQuery,
                seccionIdQuery,
                request.getFechaInicio(),
                request.getFechaFin(),
                idActual
        );

        if (!solapamientos.isEmpty()) {
            throw new BusinessException("Ya existe un horario activo que se superpone con este periodo y alcance.");
        }

        return new ContextoAcademicoHorario(anioEscolar, grado, seccion);
    }

    private record ContextoAcademicoHorario(AnioEscolar anioEscolar, Grado grado, Seccion seccion) {}
}
