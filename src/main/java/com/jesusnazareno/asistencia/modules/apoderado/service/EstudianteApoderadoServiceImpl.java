package com.jesusnazareno.asistencia.modules.apoderado.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.exception.ValidationException;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.apoderado.domain.EstudianteApoderado;
import com.jesusnazareno.asistencia.modules.apoderado.dto.ApoderadoCreateRequest;
import com.jesusnazareno.asistencia.modules.apoderado.dto.ApoderadoResponse;
import com.jesusnazareno.asistencia.modules.apoderado.dto.ApoderadoUpdateRequest;
import com.jesusnazareno.asistencia.modules.apoderado.dto.EstudianteApoderadoRequest;
import com.jesusnazareno.asistencia.modules.apoderado.dto.EstudianteApoderadoResponse;
import com.jesusnazareno.asistencia.modules.apoderado.dto.EstudianteResumenDTO;
import com.jesusnazareno.asistencia.modules.apoderado.repository.EstudianteApoderadoRepository;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.PersonaActor;
import com.jesusnazareno.asistencia.modules.persona.domain.TipoActor;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaActorRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.TipoActorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class EstudianteApoderadoServiceImpl implements EstudianteApoderadoService {

    private final PersonaRepository personaRepository;
    private final PersonaActorRepository personaActorRepository;
    private final TipoActorRepository tipoActorRepository;
    private final EstudianteApoderadoRepository estudianteApoderadoRepository;
    private final UsuarioRepository usuarioRepository;

    public EstudianteApoderadoServiceImpl(
            PersonaRepository personaRepository,
            PersonaActorRepository personaActorRepository,
            TipoActorRepository tipoActorRepository,
            EstudianteApoderadoRepository estudianteApoderadoRepository,
            UsuarioRepository usuarioRepository) {
        this.personaRepository = personaRepository;
        this.personaActorRepository = personaActorRepository;
        this.tipoActorRepository = tipoActorRepository;
        this.estudianteApoderadoRepository = estudianteApoderadoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApoderadoResponse> buscarApoderados(String q, Boolean activo, Pageable pageable) {
        Page<Persona> personas = personaActorRepository.buscarApoderados(q, activo, pageable);
        return personas.map(this::mapToApoderadoResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ApoderadoResponse obtenerApoderadoPorId(Long id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Apoderado no encontrado con ID: " + id));

        boolean esApoderado = personaActorRepository.findByPersonaId(id).stream()
                .anyMatch(pa -> "PADRE_APODERADO".equals(pa.getTipoActor().getCodigo()));

        if (!esApoderado) {
            throw new ResourceNotFoundException("La persona con ID " + id + " no está registrada como apoderado.");
        }

        return mapToApoderadoResponse(persona);
    }

    @Override
    public ApoderadoResponse registrarApoderado(ApoderadoCreateRequest request) {
        Optional<Persona> personaOpt = personaRepository.findByNumeroDocumento(request.getNumeroDocumento());
        Persona persona;

        if (personaOpt.isPresent()) {
            persona = personaOpt.get();

            Optional<PersonaActor> actorOpt = personaActorRepository
                    .findByPersonaIdAndTipoActorCodigo(persona.getId(), "PADRE_APODERADO");

            if (actorOpt.isPresent()) {
                PersonaActor actor = actorOpt.get();
                if (Boolean.TRUE.equals(actor.getActivo()) && request.getEstudianteId() == null) {
                    throw new BusinessException("La persona ya se encuentra registrada como apoderado institucional activo.");
                }
                if (!Boolean.TRUE.equals(actor.getActivo())) {
                    actor.setActivo(true);
                    actor.setFechaInicio(LocalDate.now());
                    actor.setFechaFin(null);
                    personaActorRepository.save(actor);
                }
            } else {
                TipoActor tipoActor = tipoActorRepository.findByCodigo("PADRE_APODERADO")
                        .orElseThrow(() -> new BusinessException("El tipo de actor PADRE_APODERADO no está configurado en el sistema."));

                PersonaActor nuevoActor = PersonaActor.builder()
                        .persona(persona)
                        .tipoActor(tipoActor)
                        .fechaInicio(LocalDate.now())
                        .activo(true)
                        .build();
                personaActorRepository.save(nuevoActor);
            }

            // Actualizar datos de contacto si se proporcionaron
            if (request.getTelefono() != null && !request.getTelefono().isBlank()) {
                persona.setTelefono(request.getTelefono());
            }
            if (request.getCorreo() != null && !request.getCorreo().isBlank()) {
                persona.setCorreo(request.getCorreo());
            }
            if (request.getDireccion() != null && !request.getDireccion().isBlank()) {
                persona.setDireccion(request.getDireccion());
            }
            personaRepository.save(persona);

        } else {
            // Crear nueva Persona
            persona = new Persona();
            persona.setTipoDocumento(request.getTipoDocumento());
            persona.setNumeroDocumento(request.getNumeroDocumento());
            persona.setNombres(request.getNombres());
            persona.setApellidoPaterno(request.getApellidoPaterno());
            persona.setApellidoMaterno(request.getApellidoMaterno());
            persona.setFechaNacimiento(request.getFechaNacimiento());
            persona.setSexo(request.getSexo());
            persona.setTelefono(request.getTelefono());
            persona.setCorreo(request.getCorreo());
            persona.setDireccion(request.getDireccion());
            persona.setActivo(true);

            persona = personaRepository.save(persona);

            TipoActor tipoActor = tipoActorRepository.findByCodigo("PADRE_APODERADO")
                    .orElseThrow(() -> new BusinessException("El tipo de actor PADRE_APODERADO no está configurado en el sistema."));

            PersonaActor personaActor = PersonaActor.builder()
                    .persona(persona)
                    .tipoActor(tipoActor)
                    .fechaInicio(LocalDate.now())
                    .activo(true)
                    .build();
            personaActorRepository.save(personaActor);
        }

        // Si se especificó un estudiante para asignación inmediata
        if (request.getEstudianteId() != null && request.getParentesco() != null) {
            EstudianteApoderadoRequest relacionReq = new EstudianteApoderadoRequest(
                    request.getEstudianteId(),
                    persona.getId(),
                    request.getParentesco(),
                    request.getEsPrincipal(),
                    LocalDate.now()
            );
            asignarApoderadoAEstudiante(relacionReq);
        }

        return mapToApoderadoResponse(persona);
    }

    @Override
    public void actualizarApoderado(Long id, ApoderadoUpdateRequest request) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Apoderado no encontrado con ID: " + id));

        if (!persona.getNumeroDocumento().equals(request.getNumeroDocumento())
                && personaRepository.existsByNumeroDocumentoAndIdNot(request.getNumeroDocumento(), id)) {
            throw new ValidationException("El número de documento ya está registrado para otra persona.");
        }

        persona.setTipoDocumento(request.getTipoDocumento());
        persona.setNumeroDocumento(request.getNumeroDocumento());
        persona.setNombres(request.getNombres());
        persona.setApellidoPaterno(request.getApellidoPaterno());
        persona.setApellidoMaterno(request.getApellidoMaterno());
        persona.setFechaNacimiento(request.getFechaNacimiento());
        persona.setSexo(request.getSexo());
        persona.setTelefono(request.getTelefono());
        persona.setCorreo(request.getCorreo());
        persona.setDireccion(request.getDireccion());

        personaRepository.save(persona);
    }

    @Override
    public void activarApoderado(Long id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Apoderado no encontrado con ID: " + id));

        PersonaActor actor = personaActorRepository
                .findByPersonaIdAndTipoActorCodigo(id, "PADRE_APODERADO")
                .orElseThrow(() -> new BusinessException("La persona no posee la función de apoderado."));

        actor.setActivo(true);
        actor.setFechaFin(null);
        personaActorRepository.save(actor);

        if (Boolean.FALSE.equals(persona.getActivo())) {
            persona.setActivo(true);
            personaRepository.save(persona);
        }
    }

    @Override
    public void desactivarApoderado(Long id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Apoderado no encontrado con ID: " + id));

        PersonaActor actor = personaActorRepository
                .findByPersonaIdAndTipoActorCodigo(id, "PADRE_APODERADO")
                .orElseThrow(() -> new BusinessException("La persona no posee la función de apoderado."));

        actor.setActivo(false);
        actor.setFechaFin(LocalDate.now());
        personaActorRepository.save(actor);

        // Desactivar relaciones activas con estudiantes para este apoderado
        List<EstudianteApoderado> relaciones = estudianteApoderadoRepository.findByApoderadoIdAndActivoTrue(id);
        for (EstudianteApoderado rel : relaciones) {
            rel.setActivo(false);
            rel.setFechaFin(LocalDate.now());
            rel.setEsPrincipal(false);
            estudianteApoderadoRepository.save(rel);
        }

        // Si la persona ya no posee ningún otro actor activo, se desactiva la disponibilidad general
        if (personaActorRepository.countByPersonaIdAndActivoTrue(id) == 0) {
            persona.setActivo(false);
            personaRepository.save(persona);
        }
    }

    @Override
    public EstudianteApoderadoResponse asignarApoderadoAEstudiante(EstudianteApoderadoRequest request) {
        if (Objects.equals(request.getEstudianteId(), request.getApoderadoId())) {
            throw new BusinessException("El estudiante no puede ser su propio apoderado.");
        }

        Persona estudiante = personaRepository.findById(request.getEstudianteId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + request.getEstudianteId()));

        if (!personaActorRepository.existsByPersonaIdAndTipoActorCodigoAndActivoTrue(estudiante.getId(), "ESTUDIANTE")) {
            throw new BusinessException("La persona seleccionada no posee el perfil de ESTUDIANTE activo.");
        }

        Persona apoderado = personaRepository.findById(request.getApoderadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Apoderado no encontrado con ID: " + request.getApoderadoId()));

        // Asegurar que el apoderado tenga actor PADRE_APODERADO activo
        Optional<PersonaActor> apoderadoActorOpt = personaActorRepository
                .findByPersonaIdAndTipoActorCodigo(apoderado.getId(), "PADRE_APODERADO");

        if (apoderadoActorOpt.isEmpty()) {
            TipoActor tipoActor = tipoActorRepository.findByCodigo("PADRE_APODERADO")
                    .orElseThrow(() -> new BusinessException("El tipo de actor PADRE_APODERADO no está configurado."));
            PersonaActor nuevoActor = PersonaActor.builder()
                    .persona(apoderado)
                    .tipoActor(tipoActor)
                    .fechaInicio(LocalDate.now())
                    .activo(true)
                    .build();
            personaActorRepository.save(nuevoActor);
        } else if (!Boolean.TRUE.equals(apoderadoActorOpt.get().getActivo())) {
            PersonaActor actor = apoderadoActorOpt.get();
            actor.setActivo(true);
            actor.setFechaFin(null);
            personaActorRepository.save(actor);
        }

        // Validar si ya existe una relación activa entre ambos
        if (estudianteApoderadoRepository.existsByEstudianteIdAndApoderadoIdAndActivoTrue(estudiante.getId(), apoderado.getId())) {
            throw new BusinessException("El apoderado ya está relacionado activamente con el estudiante.");
        }

        // Si se marca como principal, desmarcar cualquier apoderado principal activo previo para este estudiante
        boolean esPrincipal = Boolean.TRUE.equals(request.getEsPrincipal());
        if (esPrincipal) {
            Optional<EstudianteApoderado> principalPrevio = estudianteApoderadoRepository
                    .findByEstudianteIdAndEsPrincipalTrueAndActivoTrue(estudiante.getId());
            principalPrevio.ifPresent(p -> {
                p.setEsPrincipal(false);
                estudianteApoderadoRepository.save(p);
            });
        }

        // Buscar si existe un historial inactivo previo entre ambos para reactivarlo
        List<EstudianteApoderado> previas = estudianteApoderadoRepository.findRelacionesByEstudianteId(estudiante.getId());
        Optional<EstudianteApoderado> inactivaPrevia = previas.stream()
                .filter(r -> r.getApoderado().getId().equals(apoderado.getId()) && !Boolean.TRUE.equals(r.getActivo()))
                .findFirst();

        EstudianteApoderado guardada;
        if (inactivaPrevia.isPresent()) {
            EstudianteApoderado rel = inactivaPrevia.get();
            rel.setParentesco(request.getParentesco());
            rel.setEsPrincipal(esPrincipal);
            rel.setFechaInicio(request.getFechaInicio() != null ? request.getFechaInicio() : LocalDate.now());
            rel.setFechaFin(null);
            rel.setActivo(true);
            guardada = estudianteApoderadoRepository.save(rel);
        } else {
            EstudianteApoderado nueva = EstudianteApoderado.builder()
                    .estudiante(estudiante)
                    .apoderado(apoderado)
                    .parentesco(request.getParentesco())
                    .esPrincipal(esPrincipal)
                    .fechaInicio(request.getFechaInicio() != null ? request.getFechaInicio() : LocalDate.now())
                    .activo(true)
                    .build();
            guardada = estudianteApoderadoRepository.save(nueva);
        }

        return mapToRelacionResponse(guardada);
    }

    @Override
    public void marcarComoPrincipal(Long estudianteId, Long relacionId) {
        EstudianteApoderado relacion = estudianteApoderadoRepository.findById(relacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Relación de apoderado no encontrada con ID: " + relacionId));

        if (!Objects.equals(relacion.getEstudiante().getId(), estudianteId)) {
            throw new BusinessException("La relación no corresponde al estudiante especificado.");
        }

        if (!Boolean.TRUE.equals(relacion.getActivo())) {
            throw new BusinessException("No se puede marcar como principal una relación inactiva.");
        }

        // Desmarcar al principal activo previo si existe y es diferente
        Optional<EstudianteApoderado> principalPrevio = estudianteApoderadoRepository
                .findByEstudianteIdAndEsPrincipalTrueAndActivoTrue(estudianteId);
        principalPrevio.ifPresent(p -> {
            if (!p.getId().equals(relacionId)) {
                p.setEsPrincipal(false);
                estudianteApoderadoRepository.save(p);
            }
        });

        relacion.setEsPrincipal(true);
        estudianteApoderadoRepository.save(relacion);
    }

    @Override
    public void desactivarRelacion(Long estudianteId, Long relacionId) {
        EstudianteApoderado relacion = estudianteApoderadoRepository.findById(relacionId)
                .orElseThrow(() -> new ResourceNotFoundException("Relación de apoderado no encontrada con ID: " + relacionId));

        if (!Objects.equals(relacion.getEstudiante().getId(), estudianteId)) {
            throw new BusinessException("La relación no corresponde al estudiante especificado.");
        }

        relacion.setActivo(false);
        relacion.setFechaFin(LocalDate.now());
        relacion.setEsPrincipal(false);
        estudianteApoderadoRepository.save(relacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteApoderadoResponse> obtenerApoderadosPorEstudiante(Long estudianteId) {
        return estudianteApoderadoRepository.findRelacionesByEstudianteId(estudianteId).stream()
                .map(this::mapToRelacionResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstudianteApoderadoResponse> obtenerRelacionesPorApoderado(Long apoderadoId) {
        return estudianteApoderadoRepository.findRelacionesByApoderadoId(apoderadoId).stream()
                .map(this::mapToRelacionResponse)
                .toList();
    }

    private ApoderadoResponse mapToApoderadoResponse(Persona persona) {
        List<EstudianteApoderado> relaciones = estudianteApoderadoRepository.findRelacionesByApoderadoId(persona.getId());

        List<EstudianteResumenDTO> estudiantesAsociados = relaciones.stream()
                .map(rel -> new EstudianteResumenDTO(
                        rel.getId(),
                        rel.getEstudiante().getId(),
                        rel.getEstudiante().getNumeroDocumento(),
                        rel.getEstudiante().getNombres() + " " + rel.getEstudiante().getApellidoPaterno() + " " + rel.getEstudiante().getApellidoMaterno(),
                        rel.getParentesco(),
                        rel.getParentesco().getDescripcion(),
                        rel.getEsPrincipal(),
                        rel.getActivo(),
                        rel.getFechaInicio(),
                        rel.getFechaFin()
                ))
                .toList();

        boolean tieneUsuario = usuarioRepository.existsByPersonaId(persona.getId());

        return new ApoderadoResponse(
                persona.getId(),
                persona.getTipoDocumento() != null ? persona.getTipoDocumento().name() : "DNI",
                persona.getNumeroDocumento(),
                persona.getNombres(),
                persona.getApellidoPaterno(),
                persona.getApellidoMaterno(),
                persona.getFechaNacimiento(),
                persona.getSexo() != null ? persona.getSexo().name() : "",
                persona.getTelefono(),
                persona.getCorreo(),
                persona.getDireccion(),
                persona.getActivo(),
                tieneUsuario,
                estudiantesAsociados,
                persona.getCreatedAt(),
                persona.getUpdatedAt()
        );
    }

    private EstudianteApoderadoResponse mapToRelacionResponse(EstudianteApoderado rel) {
        Persona est = rel.getEstudiante();
        Persona apod = rel.getApoderado();

        return new EstudianteApoderadoResponse(
                rel.getId(),
                est.getId(),
                est.getNumeroDocumento(),
                est.getNombres() + " " + est.getApellidoPaterno() + " " + est.getApellidoMaterno(),
                apod.getId(),
                apod.getNumeroDocumento(),
                apod.getNombres() + " " + apod.getApellidoPaterno() + " " + apod.getApellidoMaterno(),
                apod.getTelefono(),
                apod.getCorreo(),
                apod.getDireccion(),
                rel.getParentesco(),
                rel.getParentesco() != null ? rel.getParentesco().getDescripcion() : "",
                rel.getEsPrincipal(),
                rel.getFechaInicio(),
                rel.getFechaFin(),
                rel.getActivo(),
                rel.getCreatedAt(),
                rel.getUpdatedAt()
        );
    }
}
