package com.jesusnazareno.asistencia.modules.auditoria.service;

import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.auditoria.domain.AuditoriaEvento;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.dto.AuditoriaEventoDTO;
import com.jesusnazareno.asistencia.modules.auditoria.dto.AuditoriaFiltroDTO;
import com.jesusnazareno.asistencia.modules.auditoria.repository.AuditoriaEventoRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de auditoría funcional y trazabilidad.
 * Gestiona el guardado inmutable de eventos y su consulta paginada y filtrada.
 */
@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaServiceImpl.class);

    private final AuditoriaEventoRepository auditoriaEventoRepository;
    private final UsuarioRepository usuarioRepository;

    public AuditoriaServiceImpl(AuditoriaEventoRepository auditoriaEventoRepository,
                                UsuarioRepository usuarioRepository) {
        this.auditoriaEventoRepository = auditoriaEventoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarEventoConContexto(AccionAuditoria accion,
                                           ModuloAuditoria modulo,
                                           String entidadTipo,
                                           Long entidadId,
                                           ResultadoAuditoria resultado,
                                           String descripcion,
                                           String detalles) {
        String username = resolverUsernameActual();
        String ip = resolverIpActual();
        registrarEvento(username, accion, modulo, entidadTipo, entidadId, resultado, descripcion, ip, detalles);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarEvento(String username,
                                AccionAuditoria accion,
                                ModuloAuditoria modulo,
                                String entidadTipo,
                                Long entidadId,
                                ResultadoAuditoria resultado,
                                String descripcion,
                                String direccionIp,
                                String detalles) {
        try {
            Usuario usuario = null;
            if (username != null && !username.isBlank() && !"anonymousUser".equalsIgnoreCase(username)) {
                Optional<Usuario> optUser = usuarioRepository.findByUsername(username.trim());
                if (optUser.isPresent()) {
                    usuario = optUser.get();
                }
            }

            AuditoriaEvento evento = AuditoriaEvento.builder()
                    .fecha(LocalDateTime.now())
                    .usuario(usuario)
                    .username(username != null ? username.trim() : null)
                    .accion(accion)
                    .modulo(modulo)
                    .entidadTipo(entidadTipo)
                    .entidadId(entidadId)
                    .resultado(resultado != null ? resultado : ResultadoAuditoria.EXITO)
                    .descripcion(descripcion)
                    .direccionIp(direccionIp)
                    .detalles(detalles)
                    .createdAt(LocalDateTime.now())
                    .build();

            auditoriaEventoRepository.save(evento);
            log.debug("Evento de auditoría registrado: {} - {} - {}", modulo, accion, resultado);
        } catch (Exception e) {
            log.error("Error al registrar evento de auditoría [{} - {}]: {}", modulo, accion, e.getMessage(), e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarEvento(AccionAuditoria accion,
                                ModuloAuditoria modulo,
                                ResultadoAuditoria resultado,
                                String descripcion,
                                String entidadTipo,
                                Long entidadId,
                                String detalles) {
        registrarEventoConContexto(accion, modulo, entidadTipo, entidadId, resultado, descripcion, detalles);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarEventoConContexto(String username,
                                           AccionAuditoria accion,
                                           ModuloAuditoria modulo,
                                           ResultadoAuditoria resultado,
                                           String descripcion,
                                           String direccionIp,
                                           String entidadTipo,
                                           Long entidadId,
                                           String detalles) {
        registrarEvento(username, accion, modulo, entidadTipo, entidadId, resultado, descripcion, direccionIp, detalles);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditoriaEventoDTO> buscarEventos(AuditoriaFiltroDTO filtro, Pageable pageable) {
        return listarEventos(filtro, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditoriaEventoDTO> listarEventos(AuditoriaFiltroDTO filtro, Pageable pageable) {
        Specification<AuditoriaEvento> spec = construirSpecification(filtro);

        // Garantizar orden descendente por fecha si no viene especificado
        Pageable ordenado = pageable;
        if (pageable.getSort().isUnsorted()) {
            ordenado = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "fecha"));
        }

        Page<AuditoriaEvento> page = auditoriaEventoRepository.findAll(spec, ordenado);
        return page.map(AuditoriaEventoDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditoriaEventoDTO buscarPorId(Long id) {
        return obtenerPorId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditoriaEventoDTO obtenerPorId(Long id) {
        AuditoriaEvento evento = auditoriaEventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento de auditoría no encontrado con ID: " + id));
        return AuditoriaEventoDTO.fromEntity(evento);
    }

    private Specification<AuditoriaEvento> construirSpecification(AuditoriaFiltroDTO filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filtro != null) {
                if (filtro.getFechaInicio() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("fecha"), filtro.getFechaInicio().atStartOfDay()));
                }
                if (filtro.getFechaFin() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("fecha"), filtro.getFechaFin().atTime(LocalTime.MAX)));
                }
                if (filtro.getUsername() != null && !filtro.getUsername().isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get("username")), "%" + filtro.getUsername().trim().toLowerCase() + "%"));
                }
                if (filtro.getAccion() != null) {
                    predicates.add(cb.equal(root.get("accion"), filtro.getAccion()));
                }
                if (filtro.getModulo() != null) {
                    predicates.add(cb.equal(root.get("modulo"), filtro.getModulo()));
                }
                if (filtro.getResultado() != null) {
                    predicates.add(cb.equal(root.get("resultado"), filtro.getResultado()));
                }
                if (filtro.getEntidadTipo() != null && !filtro.getEntidadTipo().isBlank()) {
                    predicates.add(cb.equal(root.get("entidadTipo"), filtro.getEntidadTipo().trim()));
                }
                if (filtro.getEntidadId() != null) {
                    predicates.add(cb.equal(root.get("entidadId"), filtro.getEntidadId()));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private String resolverUsernameActual() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                return auth.getName();
            }
        } catch (Exception ignored) {
        }
        return "sistema";
    }

    private String resolverIpActual() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest req = attrs.getRequest();
                if (req != null) {
                    String remoteAddr = req.getRemoteAddr();
                    return remoteAddr != null ? remoteAddr : "127.0.0.1";
                }
            }
        } catch (Exception ignored) {
        }
        return "127.0.0.1";
    }
}
