package com.jesusnazareno.asistencia.modules.acceso.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.acceso.dto.CambioPasswordRequest;
import com.jesusnazareno.asistencia.modules.acceso.dto.UsuarioCreateRequest;
import com.jesusnazareno.asistencia.modules.acceso.dto.UsuarioResponse;
import com.jesusnazareno.asistencia.modules.acceso.dto.UsuarioUpdateRequest;
import com.jesusnazareno.asistencia.modules.acceso.domain.Permiso;
import com.jesusnazareno.asistencia.modules.acceso.domain.Rol;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.mapper.UsuarioMapper;
import com.jesusnazareno.asistencia.modules.acceso.repository.RolPermisoRepository;
import com.jesusnazareno.asistencia.modules.acceso.repository.RolRepository;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final RolRepository rolRepository;
    private final RolPermisoRepository rolPermisoRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final com.jesusnazareno.asistencia.modules.auditoria.service.AuditoriaService auditoriaService;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              PersonaRepository personaRepository,
                              RolRepository rolRepository,
                              UsuarioMapper usuarioMapper,
                              PasswordEncoder passwordEncoder) {
        this(usuarioRepository, personaRepository, rolRepository, null, usuarioMapper, passwordEncoder, null);
    }

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              PersonaRepository personaRepository,
                              RolRepository rolRepository,
                              RolPermisoRepository rolPermisoRepository,
                              UsuarioMapper usuarioMapper,
                              PasswordEncoder passwordEncoder) {
        this(usuarioRepository, personaRepository, rolRepository, rolPermisoRepository, usuarioMapper, passwordEncoder, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              PersonaRepository personaRepository,
                              RolRepository rolRepository,
                              RolPermisoRepository rolPermisoRepository,
                              UsuarioMapper usuarioMapper,
                              PasswordEncoder passwordEncoder,
                              com.jesusnazareno.asistencia.modules.auditoria.service.AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
        this.rolRepository = rolRepository;
        this.rolPermisoRepository = rolPermisoRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    @Override
    @Transactional
    public UsuarioResponse crear(UsuarioCreateRequest request) {
        String username = request.getUsername().trim();

        if (usuarioRepository.existsByUsername(username)) {
            throw new BusinessException("El nombre de usuario '" + username + "' ya se encuentra registrado");
        }

        if (usuarioRepository.existsByPersonaId(request.getPersonaId())) {
            throw new BusinessException("La persona seleccionada ya tiene un usuario asignado");
        }

        if (request.getPassword() == null || request.getPassword().trim().length() < 8) {
            throw new BusinessException("La contraseña debe tener al menos 8 caracteres");
        }

        Persona persona = personaRepository.findById(request.getPersonaId())
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + request.getPersonaId()));

        Rol rol = rolRepository.findById(request.getRolId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + request.getRolId()));

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Usuario usuario = Usuario.builder()
                .persona(persona)
                .username(username)
                .password(encodedPassword)
                .rol(rol)
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);

        if (auditoriaService != null) {
            auditoriaService.registrarEventoConContexto(
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.USUARIO_CREAR,
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.USUARIOS,
                    "USUARIO",
                    guardado.getId(),
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                    "Se creó el usuario: " + guardado.getUsername(),
                    "Rol: " + (guardado.getRol() != null ? guardado.getRol().getNombre() : "N/A")
            );
        }

        return usuarioMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return usuarioMapper.toResponse(obtenerEntidadPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorIdConPermisos(Long id) {
        Usuario usuario = obtenerEntidadPorId(id);
        UsuarioResponse response = usuarioMapper.toResponse(usuario);

        if (usuario.getRol() != null && rolPermisoRepository != null) {
            List<Permiso> permisos = rolPermisoRepository.findPermisosActivosByRolId(usuario.getRol().getId());
            List<String> codigosPermisos = permisos.stream()
                    .map(Permiso::getCodigo)
                    .sorted()
                    .collect(Collectors.toList());
            response.setPermisos(codigosPermisos);
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorUsername(String username) {
        return usuarioMapper.toResponse(obtenerEntidadPorUsername(username));
    }

    @Override
    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = obtenerEntidadPorId(id);

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            String nuevoUsername = request.getUsername().trim();
            if (!usuario.getUsername().equalsIgnoreCase(nuevoUsername) && usuarioRepository.existsByUsername(nuevoUsername)) {
                throw new BusinessException("El nombre de usuario '" + nuevoUsername + "' ya se encuentra registrado");
            }
            usuario.setUsername(nuevoUsername);
        }

        if (request.getRolId() != null) {
            Rol nuevoRol = rolRepository.findById(request.getRolId())
                    .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + request.getRolId()));
            usuario.setRol(nuevoRol);
        }

        if (request.getActivo() != null) {
            usuario.setActivo(request.getActivo());
        }

        Usuario actualizado = usuarioRepository.save(usuario);

        if (auditoriaService != null) {
            auditoriaService.registrarEventoConContexto(
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.USUARIO_EDITAR,
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.USUARIOS,
                    "USUARIO",
                    actualizado.getId(),
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                    "Se actualizó el usuario: " + actualizado.getUsername(),
                    "Rol: " + (actualizado.getRol() != null ? actualizado.getRol().getNombre() : "N/A")
            );
        }

        return usuarioMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public UsuarioResponse activar(Long id) {
        Usuario usuario = obtenerEntidadPorId(id);
        usuario.setActivo(true);
        Usuario actualizado = usuarioRepository.save(usuario);

        if (auditoriaService != null) {
            auditoriaService.registrarEventoConContexto(
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.USUARIO_ACTIVAR,
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.USUARIOS,
                    "USUARIO",
                    actualizado.getId(),
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                    "Se activó el usuario: " + actualizado.getUsername(),
                    null
            );
        }

        return usuarioMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public UsuarioResponse desactivar(Long id) {
        Usuario usuario = obtenerEntidadPorId(id);

        // Prevenir auto-desactivación del usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null && auth.getName().equalsIgnoreCase(usuario.getUsername())) {
            throw new BusinessException("No puede desactivar su propio usuario");
        }

        // Prevenir desactivación del último Administrador activo
        if (usuario.getRol() != null && "ADMIN".equalsIgnoreCase(usuario.getRol().getCodigo())) {
            long adminActivos = usuarioRepository.countByRolCodigoAndActivoTrue("ADMIN");
            if (adminActivos <= 1) {
                throw new BusinessException("No se puede desactivar al único usuario Administrador activo del sistema");
            }
        }

        usuario.setActivo(false);
        Usuario actualizado = usuarioRepository.save(usuario);

        if (auditoriaService != null) {
            auditoriaService.registrarEventoConContexto(
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.USUARIO_DESACTIVAR,
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.USUARIOS,
                    "USUARIO",
                    actualizado.getId(),
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                    "Se desactivó el usuario: " + actualizado.getUsername(),
                    null
            );
        }

        return usuarioMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public void cambiarPassword(Long id, CambioPasswordRequest request) {
        Usuario usuario = obtenerEntidadPorId(id);

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
            throw new BusinessException("La contraseña actual es incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepository.save(usuario);

        if (auditoriaService != null) {
            auditoriaService.registrarEventoConContexto(
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.USUARIO_CAMBIAR_PASSWORD,
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.USUARIOS,
                    "USUARIO",
                    usuario.getId(),
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                    "Contraseña del usuario actualizada.",
                    null
            );
        }
    }

    @Override
    @Transactional
    public void resetearPassword(Long id, String nuevaPassword, String confirmarPassword) {
        if (nuevaPassword == null || nuevaPassword.trim().length() < 8) {
            throw new BusinessException("La nueva contraseña debe tener al menos 8 caracteres");
        }

        if (!nuevaPassword.equals(confirmarPassword)) {
            throw new BusinessException("Las contraseñas no coinciden");
        }

        Usuario usuario = obtenerEntidadPorId(id);
        usuario.setPassword(passwordEncoder.encode(nuevaPassword.trim()));
        usuarioRepository.save(usuario);

        if (auditoriaService != null) {
            auditoriaService.registrarEventoConContexto(
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria.USUARIO_CAMBIAR_PASSWORD,
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria.USUARIOS,
                    "USUARIO",
                    usuario.getId(),
                    com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria.EXITO,
                    "Contraseña del usuario actualizada.",
                    null
            );
        }
    }

    @Override
    @Transactional
    public void registrarUltimoAcceso(String username) {
        usuarioRepository.findByUsername(username).ifPresent(usuario -> {
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuarioRepository.save(usuario);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarActivos() {
        return usuarioRepository.findByActivoTrue().stream()
                .map(usuarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listarPaginado(String q, Long rolId, Boolean activo, Pageable pageable) {
        String queryLimpio = (q != null && !q.trim().isEmpty()) ? q.trim() : null;
        Page<Usuario> pagina = usuarioRepository.buscarUsuarios(queryLimpio, rolId, activo, pageable);
        return pagina.map(usuarioMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerEntidadPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerEntidadPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con username: " + username));
    }
}
