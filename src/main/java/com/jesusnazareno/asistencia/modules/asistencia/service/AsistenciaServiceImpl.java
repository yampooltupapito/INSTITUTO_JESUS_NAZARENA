package com.jesusnazareno.asistencia.modules.asistencia.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Asistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaRequest;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaResponse;
import com.jesusnazareno.asistencia.modules.asistencia.mapper.AsistenciaMapper;
import com.jesusnazareno.asistencia.modules.asistencia.repository.AsistenciaRepository;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de la lógica de negocio para la gestión de asistencia diaria.
 */
@Service
public class AsistenciaServiceImpl implements AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final MatriculaRepository matriculaRepository;
    private final AsistenciaMapper asistenciaMapper;
    private final com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository usuarioRepository;
    private final com.jesusnazareno.asistencia.modules.academico.service.HorarioAsistenciaService horarioAsistenciaService;
    private final com.jesusnazareno.asistencia.modules.academico.service.CalendarioEscolarService calendarioEscolarService;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;
    private final com.jesusnazareno.asistencia.modules.auditoria.service.AuditoriaService auditoriaService;

    public AsistenciaServiceImpl(AsistenciaRepository asistenciaRepository,
                                  MatriculaRepository matriculaRepository,
                                  AsistenciaMapper asistenciaMapper,
                                  com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository usuarioRepository) {
        this(asistenciaRepository, matriculaRepository, asistenciaMapper, usuarioRepository, null, null, null, null);
    }

    public AsistenciaServiceImpl(AsistenciaRepository asistenciaRepository,
                                  MatriculaRepository matriculaRepository,
                                  AsistenciaMapper asistenciaMapper,
                                  com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository usuarioRepository,
                                  com.jesusnazareno.asistencia.modules.academico.service.HorarioAsistenciaService horarioAsistenciaService) {
        this(asistenciaRepository, matriculaRepository, asistenciaMapper, usuarioRepository, horarioAsistenciaService, null, null, null);
    }

    public AsistenciaServiceImpl(AsistenciaRepository asistenciaRepository,
                                  MatriculaRepository matriculaRepository,
                                  AsistenciaMapper asistenciaMapper,
                                  com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository usuarioRepository,
                                  com.jesusnazareno.asistencia.modules.academico.service.HorarioAsistenciaService horarioAsistenciaService,
                                  com.jesusnazareno.asistencia.modules.academico.service.CalendarioEscolarService calendarioEscolarService) {
        this(asistenciaRepository, matriculaRepository, asistenciaMapper, usuarioRepository, horarioAsistenciaService, calendarioEscolarService, null, null);
    }

    public AsistenciaServiceImpl(AsistenciaRepository asistenciaRepository,
                                  MatriculaRepository matriculaRepository,
                                  AsistenciaMapper asistenciaMapper,
                                  com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository usuarioRepository,
                                  com.jesusnazareno.asistencia.modules.academico.service.HorarioAsistenciaService horarioAsistenciaService,
                                  com.jesusnazareno.asistencia.modules.academico.service.CalendarioEscolarService calendarioEscolarService,
                                  org.springframework.context.ApplicationEventPublisher eventPublisher) {
        this(asistenciaRepository, matriculaRepository, asistenciaMapper, usuarioRepository, horarioAsistenciaService, calendarioEscolarService, eventPublisher, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public AsistenciaServiceImpl(AsistenciaRepository asistenciaRepository,
                                  MatriculaRepository matriculaRepository,
                                  AsistenciaMapper asistenciaMapper,
                                  com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository usuarioRepository,
                                  com.jesusnazareno.asistencia.modules.academico.service.HorarioAsistenciaService horarioAsistenciaService,
                                  com.jesusnazareno.asistencia.modules.academico.service.CalendarioEscolarService calendarioEscolarService,
                                  org.springframework.context.ApplicationEventPublisher eventPublisher,
                                  com.jesusnazareno.asistencia.modules.auditoria.service.AuditoriaService auditoriaService) {
        this.asistenciaRepository = asistenciaRepository;
        this.matriculaRepository = matriculaRepository;
        this.asistenciaMapper = asistenciaMapper;
        this.usuarioRepository = usuarioRepository;
        this.horarioAsistenciaService = horarioAsistenciaService;
        this.calendarioEscolarService = calendarioEscolarService;
        this.eventPublisher = eventPublisher;
        this.auditoriaService = auditoriaService;
    }

    @Override
    @Transactional
    public AsistenciaResponse registrar(AsistenciaRequest request) {
        return registrarInterno(request, true);
    }

    private AsistenciaResponse registrarInterno(AsistenciaRequest request, boolean auditar) {
        if (request == null) {
            throw new BusinessException("La solicitud de asistencia no puede ser nula.");
        }
        if (request.getEstado() == EstadoAsistencia.JUSTIFICADO) {
            throw new BusinessException("No se puede registrar manualmente una asistencia en estado JUSTIFICADO.");
        }

        // 1. Validar existencia de Matrícula
        Matricula matricula = matriculaRepository.findById(request.getMatriculaId())
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula no encontrada con ID: " + request.getMatriculaId()));

        // Validar existencia de Usuario que registra
        com.jesusnazareno.asistencia.modules.acceso.domain.Usuario usuario = usuarioRepository.findById(request.getRegistradoPor())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.getRegistradoPor()));

        // 2. Validar que la matrícula esté en estado activo/válido
        if (matricula.getEstado() != EstadoMatricula.MATRICULADO) {
            throw new BusinessException("La matrícula no permite registrar asistencia.");
        }

        // Validar referencias académicas activas
        if (matricula.getAnioEscolar() == null || !Boolean.TRUE.equals(matricula.getAnioEscolar().getActivo())) {
            throw new BusinessException("El año escolar asociado a la matrícula no se encuentra activo.");
        }
        if (matricula.getSeccion() == null || !Boolean.TRUE.equals(matricula.getSeccion().getActivo())) {
            throw new BusinessException("La sección asociada a la matrícula no se encuentra activa.");
        }

        // 3. Validar si la fecha es día lectivo en el calendario escolar
        if (calendarioEscolarService != null) {
            Long anioEscolarId = matricula.getAnioEscolar().getId();
            if (!calendarioEscolarService.esDiaLectivo(anioEscolarId, request.getFecha())) {
                throw new BusinessException("No corresponde registrar asistencia en una fecha no lectiva.");
            }
        }

        // 4. Restricción fundamental: una sola asistencia por matrícula y fecha
        if (asistenciaRepository.existsByMatriculaIdAndFecha(matricula.getId(), request.getFecha())) {
            throw new BusinessException("La asistencia del estudiante ya fue registrada para esta fecha.");
        }

        // 4. Determinar estado automáticamente si hay horario y hora de ingreso para PRESENTE o TARDANZA
        if (request.getEstado() != EstadoAsistencia.AUSENTE && request.getEstado() != EstadoAsistencia.JUSTIFICADO
                && request.getHoraIngreso() != null && horarioAsistenciaService != null) {
            Long anioEscolarId = matricula.getAnioEscolar() != null ? matricula.getAnioEscolar().getId() : null;
            Long gradoId = matricula.getGrado() != null ? matricula.getGrado().getId() :
                    (matricula.getSeccion() != null && matricula.getSeccion().getGrado() != null ? matricula.getSeccion().getGrado().getId() : null);
            Long seccionId = matricula.getSeccion() != null ? matricula.getSeccion().getId() : null;
            com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno turno =
                    matricula.getSeccion() != null ? matricula.getSeccion().getTurno() : null;

            var horarioOpt = horarioAsistenciaService.resolverHorario(
                    request.getFecha(), anioEscolarId, gradoId, seccionId, turno
            );
            if (horarioOpt.isPresent()) {
                EstadoAsistencia estadoCalculado = horarioAsistenciaService.determinarEstado(horarioOpt.get(), request.getHoraIngreso());
                request.setEstado(estadoCalculado);
            }
        }

        // 5. Validar reglas de horarios según estado
        validarHorarios(request.getEstado(), request.getHoraIngreso(), request.getHoraSalida());

        Asistencia asistencia = asistenciaMapper.toEntity(request, matricula, usuario);
        try {
            Asistencia guardada = asistenciaRepository.save(asistencia);
            publicarEvaluacionAlerta(matricula.getId());
            if (auditar && auditoriaService != null) {
                auditoriaService.registrarEventoConContexto(
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.ASISTENCIA_REGISTRAR,
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.ASISTENCIA,
                        "ASISTENCIA",
                        guardada.getId(),
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                        "Se registró asistencia para matrícula ID: " + matricula.getId() + " - Estado: " + guardada.getEstado(),
                        "Fecha: " + guardada.getFecha()
                );
            }
            return asistenciaMapper.toResponse(guardada);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Error de concurrencia o duplicidad al registrar la asistencia.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AsistenciaResponse buscarPorId(Long id) {
        if (id == null) {
            throw new ResourceNotFoundException("Identificador de asistencia no puede ser nulo.");
        }
        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de asistencia no encontrado con ID: " + id));
        return asistenciaMapper.toResponse(asistencia);
    }

    @Override
    @Transactional
    public AsistenciaResponse actualizar(Long id, AsistenciaRequest request) {
        if (id == null) {
            throw new ResourceNotFoundException("Identificador de asistencia no puede ser nulo.");
        }
        if (request == null) {
            throw new BusinessException("La solicitud de actualización no puede ser nula.");
        }

        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de asistencia no encontrado con ID: " + id));

        // Protección de JUSTIFICADO: no puede sobreescribirse
        if (asistencia.getEstado() == EstadoAsistencia.JUSTIFICADO && request.getEstado() != EstadoAsistencia.JUSTIFICADO) {
            throw new BusinessException("Una asistencia en estado JUSTIFICADO no puede modificarse manualmente.");
        }

        // Actualización controlada: Prohibir alterar matrícula o fecha del registro histórico
        if (request.getMatriculaId() != null && !request.getMatriculaId().equals(asistencia.getMatricula().getId())) {
            throw new BusinessException("No se permite modificar la matrícula asociada a un registro histórico de asistencia.");
        }
        if (request.getFecha() != null && !request.getFecha().equals(asistencia.getFecha())) {
            throw new BusinessException("No se permite modificar la fecha de un registro histórico de asistencia.");
        }

        if (request.getEstado() != EstadoAsistencia.AUSENTE && request.getEstado() != EstadoAsistencia.JUSTIFICADO
                && request.getHoraIngreso() != null && horarioAsistenciaService != null
                && asistencia.getEstado() != EstadoAsistencia.JUSTIFICADO) {
            Matricula m = asistencia.getMatricula();
            Long anioEscolarId = m.getAnioEscolar() != null ? m.getAnioEscolar().getId() : null;
            Long gradoId = m.getGrado() != null ? m.getGrado().getId() :
                    (m.getSeccion() != null && m.getSeccion().getGrado() != null ? m.getSeccion().getGrado().getId() : null);
            Long seccionId = m.getSeccion() != null ? m.getSeccion().getId() : null;
            com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno turno =
                    m.getSeccion() != null ? m.getSeccion().getTurno() : null;

            var horarioOpt = horarioAsistenciaService.resolverHorario(
                    asistencia.getFecha(), anioEscolarId, gradoId, seccionId, turno
            );
            if (horarioOpt.isPresent()) {
                EstadoAsistencia estadoCalculado = horarioAsistenciaService.determinarEstado(horarioOpt.get(), request.getHoraIngreso());
                request.setEstado(estadoCalculado);
            }
        }

        // Validar horarios
        validarHorarios(request.getEstado(), request.getHoraIngreso(), request.getHoraSalida());

        asistenciaMapper.updateEntity(request, asistencia);
        try {
            Asistencia guardada = asistenciaRepository.save(asistencia);
            publicarEvaluacionAlerta(asistencia.getMatricula().getId());
            if (auditoriaService != null) {
                auditoriaService.registrarEventoConContexto(
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.ASISTENCIA_EDITAR,
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.ASISTENCIA,
                        "ASISTENCIA",
                        guardada.getId(),
                        com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                        "Se actualizó asistencia ID: " + guardada.getId() + " - Estado: " + guardada.getEstado(),
                        "Fecha: " + guardada.getFecha()
                );
            }
            return asistenciaMapper.toResponse(guardada);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Error de concurrencia al actualizar la asistencia.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AsistenciaResponse> listar(Pageable pageable) {
        return asistenciaRepository.findAll(pageable)
                .map(asistenciaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AsistenciaResponse> listarPorFecha(LocalDate fecha, Pageable pageable) {
        if (fecha == null) {
            throw new BusinessException("La fecha de consulta no puede ser nula.");
        }
        return asistenciaRepository.findByFecha(fecha, pageable)
                .map(asistenciaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsistenciaResponse> listarPorFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new BusinessException("La fecha de consulta no puede ser nula.");
        }
        return asistenciaRepository.findByFecha(fecha).stream()
                .map(asistenciaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AsistenciaResponse> listarPorMatricula(Long matriculaId, Pageable pageable) {
        if (matriculaId == null) {
            throw new ResourceNotFoundException("Identificador de matrícula no puede ser nulo.");
        }
        return asistenciaRepository.findByMatriculaId(matriculaId, pageable)
                .map(asistenciaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsistenciaResponse> listarPorMatricula(Long matriculaId) {
        if (matriculaId == null) {
            throw new ResourceNotFoundException("Identificador de matrícula no puede ser nulo.");
        }
        return asistenciaRepository.findByMatriculaId(matriculaId).stream()
                .map(asistenciaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AsistenciaResponse> listarPorRangoDeFechas(LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable) {
        if (fechaInicio == null || fechaFin == null) {
            throw new BusinessException("Las fechas de inicio y fin no pueden ser nulas.");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new BusinessException("La fecha final no puede ser anterior a la fecha inicial.");
        }
        return asistenciaRepository.findByFechaBetween(fechaInicio, fechaFin, pageable)
                .map(asistenciaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsistenciaResponse> listarPorMatriculaYRangoDeFechas(Long matriculaId, LocalDate fechaInicio, LocalDate fechaFin) {
        if (matriculaId == null) {
            throw new ResourceNotFoundException("Identificador de matrícula no puede ser nulo.");
        }
        if (fechaInicio == null || fechaFin == null) {
            throw new BusinessException("Las fechas de inicio y fin no pueden ser nulas.");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new BusinessException("La fecha final no puede ser anterior a la fecha inicial.");
        }
        return asistenciaRepository.findByMatriculaIdAndFechaBetween(matriculaId, fechaInicio, fechaFin).stream()
                .map(asistenciaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<AsistenciaResponse> registrarLote(List<AsistenciaRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }
        if (calendarioEscolarService != null) {
            for (AsistenciaRequest req : requests) {
                if (req != null && req.getMatriculaId() != null && req.getFecha() != null) {
                    Matricula m = matriculaRepository.findById(req.getMatriculaId()).orElse(null);
                    if (m != null && m.getAnioEscolar() != null) {
                        if (!calendarioEscolarService.esDiaLectivo(m.getAnioEscolar().getId(), req.getFecha())) {
                            throw new BusinessException("No corresponde registrar asistencia en una fecha no lectiva.");
                        }
                    }
                }
            }
        }
        List<AsistenciaResponse> respuestas = requests.stream()
                .map(req -> registrarInterno(req, false))
                .toList();

        if (auditoriaService != null && !respuestas.isEmpty()) {
            auditoriaService.registrarEventoConContexto(
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.ASISTENCIA_REGISTRO_MASIVO,
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.ASISTENCIA,
                    "ASISTENCIA",
                    null,
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                    "Se registró asistencia masiva: " + respuestas.size() + " registros.",
                    null
            );
        }

        return respuestas;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AsistenciaResponse> buscarAsistencias(
            String q, Long anioEscolarId, Long gradoId, Long seccionId,
            EstadoAsistencia estado, LocalDate fechaInicio, LocalDate fechaFin, Pageable pageable) {
        return asistenciaRepository.buscarAsistencias(
                (q != null && q.isBlank()) ? null : q,
                anioEscolarId, gradoId, seccionId, estado, fechaInicio, fechaFin, pageable
        ).map(asistenciaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Matricula> listarMatriculasValidasParaRegistro(Long seccionId, Long anioEscolarId) {
        if (seccionId == null || anioEscolarId == null) {
            throw new BusinessException("Sección y año escolar son requeridos para cargar estudiantes.");
        }
        return asistenciaRepository.findMatriculasValidasParaAsistencia(seccionId, anioEscolarId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AsistenciaResponse> buscarAsistenciaExistente(Long matriculaId, LocalDate fecha) {
        if (matriculaId == null || fecha == null) {
            return Optional.empty();
        }
        return asistenciaRepository.findByMatriculaIdAndFecha(matriculaId, fecha)
                .map(asistenciaMapper::toResponse);
    }

    private void validarHorarios(EstadoAsistencia estado, LocalTime horaIngreso, LocalTime horaSalida) {
        if (estado == EstadoAsistencia.AUSENTE) {
            if (horaIngreso != null || horaSalida != null) {
                throw new BusinessException("Un estudiante en estado AUSENTE no debe tener hora de ingreso ni de salida.");
            }
        }

        if (horaIngreso != null && horaSalida != null) {
            if (horaSalida.isBefore(horaIngreso)) {
                throw new BusinessException("La hora de salida no puede ser anterior a la hora de ingreso.");
            }
        }
    }

    private void publicarEvaluacionAlerta(Long matriculaId) {
        if (eventPublisher != null && matriculaId != null) {
            eventPublisher.publishEvent(new com.jesusnazareno.asistencia.modules.alerta.event.AlertaEvaluacionEvent(matriculaId));
        }
    }
}
