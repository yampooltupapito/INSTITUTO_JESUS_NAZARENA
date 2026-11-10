package com.jesusnazareno.asistencia.modules.personal.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.PersonaActor;
import com.jesusnazareno.asistencia.modules.persona.domain.TipoActor;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaRequest;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaResponse;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaActorRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.TipoActorRepository;
import com.jesusnazareno.asistencia.modules.persona.service.PersonaService;
import com.jesusnazareno.asistencia.modules.persona.service.PersonaServiceImpl;
import com.jesusnazareno.asistencia.modules.persona.mapper.PersonaMapper;
import com.jesusnazareno.asistencia.modules.personal.dto.PersonalActorDTO;
import com.jesusnazareno.asistencia.modules.personal.dto.PersonalCreateRequest;
import com.jesusnazareno.asistencia.modules.personal.dto.PersonalResponse;
import com.jesusnazareno.asistencia.modules.personal.dto.PersonalUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de la capa de servicio para la gestión integral del personal institucional.
 */
@Service
public class PersonalServiceImpl implements PersonalService {

    public static final List<String> TIPOS_PERSONAL_PERMITIDOS = List.of("DIRECTOR", "DOCENTE", "AUXILIAR");

    private final PersonaRepository personaRepository;
    private final PersonaActorRepository personaActorRepository;
    private final TipoActorRepository tipoActorRepository;
    private final PersonaService personaService;
    private final UsuarioRepository usuarioRepository;

    public PersonalServiceImpl(PersonaRepository personaRepository,
                               PersonaActorRepository personaActorRepository,
                               TipoActorRepository tipoActorRepository) {
        this(personaRepository, personaActorRepository, tipoActorRepository,
                new PersonaServiceImpl(personaRepository, new PersonaMapper()), null);
    }

    @Autowired
    public PersonalServiceImpl(PersonaRepository personaRepository,
                               PersonaActorRepository personaActorRepository,
                               TipoActorRepository tipoActorRepository,
                               PersonaService personaService,
                               UsuarioRepository usuarioRepository) {
        this.personaRepository = personaRepository;
        this.personaActorRepository = personaActorRepository;
        this.tipoActorRepository = tipoActorRepository;
        this.personaService = personaService != null ? personaService : new PersonaServiceImpl(personaRepository, new PersonaMapper());
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonalResponse> listarPersonal(String q, String tipoActor, String estado, Pageable pageable) {
        String query = (q != null && !q.trim().isEmpty()) ? q.trim() : null;
        String tipoFiltro = (tipoActor != null && !tipoActor.isBlank() && !"TODOS".equalsIgnoreCase(tipoActor.trim()))
                ? tipoActor.trim().toUpperCase() : null;

        Boolean activo = null;
        if ("ACTIVOS".equalsIgnoreCase(estado) || "true".equalsIgnoreCase(estado)) {
            activo = Boolean.TRUE;
        } else if ("INACTIVOS".equalsIgnoreCase(estado) || "false".equalsIgnoreCase(estado)) {
            activo = Boolean.FALSE;
        }

        Page<Persona> personasPage = personaActorRepository.buscarPersonal(query, tipoFiltro, activo, pageable);
        return personasPage.map(this::mapearPersonal);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonalResponse buscarPorId(Long personaId) {
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new ResourceNotFoundException("Personal no encontrado con ID: " + personaId));
        return mapearPersonal(persona);
    }

    @Override
    @Transactional
    public PersonalResponse registrarPersonal(PersonalCreateRequest request) {
        if (request == null) {
            throw new BusinessException("Los datos de personal son requeridos.");
        }

        validarTipoPersonal(request.getTipoActor());
        String codigoActor = request.getTipoActor().trim().toUpperCase();

        Optional<Persona> existenteOpt = personaRepository.findByNumeroDocumento(request.getNumeroDocumento().trim());

        if (existenteOpt.isPresent()) {
            Persona personaExistente = existenteOpt.get();
            Optional<PersonaActor> actorOpt = personaActorRepository.findByPersonaIdAndTipoActorCodigo(personaExistente.getId(), codigoActor);

            if (actorOpt.isPresent()) {
                PersonaActor actor = actorOpt.get();
                if (Boolean.TRUE.equals(actor.getActivo())) {
                    throw new BusinessException("La persona ya tiene asignada esta función institucional.");
                }
                actor.setActivo(true);
                actor.setFechaInicio(request.getFechaInicio() != null ? request.getFechaInicio() : LocalDate.now());
                actor.setFechaFin(null);
                personaActorRepository.save(actor);
            } else {
                TipoActor tipoActor = tipoActorRepository.findByCodigo(codigoActor)
                        .orElseThrow(() -> new ResourceNotFoundException("Tipo de actor no encontrado: " + codigoActor));
                PersonaActor nuevoActor = PersonaActor.builder()
                        .persona(personaExistente)
                        .tipoActor(tipoActor)
                        .fechaInicio(request.getFechaInicio() != null ? request.getFechaInicio() : LocalDate.now())
                        .activo(true)
                        .build();
                personaActorRepository.save(nuevoActor);
            }

            personaExistente.setActivo(true);
            personaRepository.save(personaExistente);
            return buscarPorId(personaExistente.getId());
        }

        // Persona nueva
        PersonaRequest personaRequest = new PersonaRequest(
                request.getTipoDocumento(),
                request.getNumeroDocumento().trim(),
                request.getNombres().trim(),
                request.getApellidoPaterno().trim(),
                request.getApellidoMaterno().trim(),
                request.getFechaNacimiento(),
                request.getSexo(),
                request.getTelefono(),
                request.getCorreo(),
                request.getDireccion()
        );

        PersonaResponse personaCreada = personaService.crearPersona(personaRequest);
        Persona persona = personaRepository.findById(personaCreada.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada tras creación"));

        TipoActor tipoActor = tipoActorRepository.findByCodigo(codigoActor)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de actor no encontrado: " + codigoActor));

        PersonaActor nuevoActor = PersonaActor.builder()
                .persona(persona)
                .tipoActor(tipoActor)
                .fechaInicio(request.getFechaInicio() != null ? request.getFechaInicio() : LocalDate.now())
                .activo(true)
                .build();
        personaActorRepository.save(nuevoActor);

        return buscarPorId(persona.getId());
    }

    @Override
    @Transactional
    public PersonalResponse actualizarPersonal(Long personaId, PersonalUpdateRequest request) {
        if (personaId == null) {
            throw new ResourceNotFoundException("ID de personal no puede ser nulo.");
        }

        PersonaRequest personaRequest = new PersonaRequest(
                request.getTipoDocumento(),
                request.getNumeroDocumento().trim(),
                request.getNombres().trim(),
                request.getApellidoPaterno().trim(),
                request.getApellidoMaterno().trim(),
                request.getFechaNacimiento(),
                request.getSexo(),
                request.getTelefono(),
                request.getCorreo(),
                request.getDireccion()
        );

        personaService.actualizarPersona(personaId, personaRequest);
        return buscarPorId(personaId);
    }

    @Override
    @Transactional
    public void activarPersonal(Long personaId, String tipoActorCodigo) {
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new ResourceNotFoundException("Personal no encontrado con ID: " + personaId));

        persona.setActivo(true);
        personaRepository.save(persona);

        if (tipoActorCodigo != null && !tipoActorCodigo.isBlank()) {
            String codigo = tipoActorCodigo.trim().toUpperCase();
            PersonaActor actor = personaActorRepository.findByPersonaIdAndTipoActorCodigo(personaId, codigo)
                    .orElseThrow(() -> new ResourceNotFoundException("Función institucional " + codigo + " no encontrada para esta persona"));
            actor.setActivo(true);
            actor.setFechaFin(null);
            personaActorRepository.save(actor);
        } else {
            List<PersonaActor> actoresPersonal = personaActorRepository.findByPersonaIdAndTipoActorCodigoIn(personaId, TIPOS_PERSONAL_PERMITIDOS);
            for (PersonaActor pa : actoresPersonal) {
                pa.setActivo(true);
                pa.setFechaFin(null);
                personaActorRepository.save(pa);
            }
        }
    }

    @Override
    @Transactional
    public void desactivarPersonal(Long personaId, String tipoActorCodigo) {
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new ResourceNotFoundException("Personal no encontrado con ID: " + personaId));

        if (tipoActorCodigo != null && !tipoActorCodigo.isBlank()) {
            String codigo = tipoActorCodigo.trim().toUpperCase();
            PersonaActor actor = personaActorRepository.findByPersonaIdAndTipoActorCodigo(personaId, codigo)
                    .orElseThrow(() -> new ResourceNotFoundException("Función institucional " + codigo + " no encontrada para esta persona"));
            actor.setActivo(false);
            actor.setFechaFin(LocalDate.now());
            personaActorRepository.save(actor);
        } else {
            List<PersonaActor> actoresPersonal = personaActorRepository.findByPersonaIdAndTipoActorCodigoIn(personaId, TIPOS_PERSONAL_PERMITIDOS);
            for (PersonaActor pa : actoresPersonal) {
                pa.setActivo(false);
                pa.setFechaFin(LocalDate.now());
                personaActorRepository.save(pa);
            }
        }

        // Si ya no le quedan actores activos de ningún tipo institucional, desactivar disponibilidad general de Persona
        long actoresActivosRestantes = personaActorRepository.countByPersonaIdAndActivoTrue(personaId);
        if (actoresActivosRestantes == 0) {
            persona.setActivo(false);
            personaRepository.save(persona);
        }
    }

    @Override
    @Transactional
    public void asignarNuevoActor(Long personaId, String tipoActorCodigo) {
        validarTipoPersonal(tipoActorCodigo);
        String codigo = tipoActorCodigo.trim().toUpperCase();

        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new ResourceNotFoundException("Personal no encontrado con ID: " + personaId));

        Optional<PersonaActor> existenteOpt = personaActorRepository.findByPersonaIdAndTipoActorCodigo(personaId, codigo);
        if (existenteOpt.isPresent()) {
            PersonaActor existente = existenteOpt.get();
            if (Boolean.TRUE.equals(existente.getActivo())) {
                throw new BusinessException("La persona ya tiene asignada esta función institucional.");
            }
            existente.setActivo(true);
            existente.setFechaInicio(LocalDate.now());
            existente.setFechaFin(null);
            personaActorRepository.save(existente);
        } else {
            TipoActor tipoActor = tipoActorRepository.findByCodigo(codigo)
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo de actor no encontrado: " + codigo));
            PersonaActor nuevo = PersonaActor.builder()
                    .persona(persona)
                    .tipoActor(tipoActor)
                    .fechaInicio(LocalDate.now())
                    .activo(true)
                    .build();
            personaActorRepository.save(nuevo);
        }

        persona.setActivo(true);
        personaRepository.save(persona);
    }

    @Override
    public List<String> obtenerTiposPersonalPermitidos() {
        return TIPOS_PERSONAL_PERMITIDOS;
    }

    private void validarTipoPersonal(String tipoActor) {
        if (tipoActor == null || !TIPOS_PERSONAL_PERMITIDOS.contains(tipoActor.trim().toUpperCase())) {
            throw new BusinessException("El tipo de personal seleccionado no es válido. Debe ser DIRECTOR, DOCENTE o AUXILIAR.");
        }
    }

    private PersonalResponse mapearPersonal(Persona persona) {
        PersonalResponse resp = new PersonalResponse();
        resp.setId(persona.getId());
        resp.setTipoDocumento(persona.getTipoDocumento());
        resp.setNumeroDocumento(persona.getNumeroDocumento());
        resp.setNombres(persona.getNombres());
        resp.setApellidoPaterno(persona.getApellidoPaterno());
        resp.setApellidoMaterno(persona.getApellidoMaterno());
        resp.setFechaNacimiento(persona.getFechaNacimiento());
        resp.setSexo(persona.getSexo());
        resp.setTelefono(persona.getTelefono());
        resp.setCorreo(persona.getCorreo());
        resp.setDireccion(persona.getDireccion());
        resp.setActivo(persona.getActivo());
        resp.setCreatedAt(persona.getCreatedAt());
        resp.setUpdatedAt(persona.getUpdatedAt());
        resp.setCreatedBy(persona.getCreatedBy());
        resp.setUpdatedBy(persona.getUpdatedBy());

        List<PersonaActor> actores = personaActorRepository.findByPersonaIdOrderByFechaInicioDesc(persona.getId());
        List<PersonalActorDTO> dtos = new ArrayList<>();
        List<String> codigosActivos = new ArrayList<>();

        for (PersonaActor pa : actores) {
            PersonalActorDTO dto = new PersonalActorDTO(
                    pa.getId(),
                    pa.getTipoActor().getId(),
                    pa.getTipoActor().getCodigo(),
                    pa.getTipoActor().getNombre(),
                    pa.getFechaInicio(),
                    pa.getFechaFin(),
                    pa.getActivo()
            );
            dtos.add(dto);
            if (Boolean.TRUE.equals(pa.getActivo()) && TIPOS_PERSONAL_PERMITIDOS.contains(pa.getTipoActor().getCodigo())) {
                codigosActivos.add(pa.getTipoActor().getCodigo());
            }
        }
        resp.setActores(dtos);
        resp.setActoresResumen(codigosActivos.isEmpty() ? "Inactivo" : String.join(", ", codigosActivos));

        if (usuarioRepository != null) {
            usuarioRepository.findByPersonaId(persona.getId()).ifPresent(u -> {
                resp.setTieneUsuario(true);
                resp.setUsername(u.getUsername());
                resp.setUsuarioActivo(u.getActivo());
            });
        }
        if (resp.getTieneUsuario() == null) {
            resp.setTieneUsuario(false);
        }

        return resp;
    }
}
