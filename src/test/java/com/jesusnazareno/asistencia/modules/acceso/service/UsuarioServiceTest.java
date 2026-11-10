package com.jesusnazareno.asistencia.modules.acceso.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.acceso.dto.CambioPasswordRequest;
import com.jesusnazareno.asistencia.modules.acceso.dto.UsuarioCreateRequest;
import com.jesusnazareno.asistencia.modules.acceso.dto.UsuarioResponse;
import com.jesusnazareno.asistencia.modules.acceso.dto.UsuarioUpdateRequest;
import com.jesusnazareno.asistencia.modules.acceso.domain.Rol;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.mapper.UsuarioMapper;
import com.jesusnazareno.asistencia.modules.acceso.repository.RolRepository;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioMapper usuarioMapper;
    private UsuarioServiceImpl usuarioService;

    private Persona persona;
    private Rol rolAdmin;
    private Rol rolDirector;

    @BeforeEach
    void setUp() {
        usuarioMapper = new UsuarioMapper();
        usuarioService = new UsuarioServiceImpl(
                usuarioRepository,
                personaRepository,
                rolRepository,
                usuarioMapper,
                passwordEncoder
        );

        persona = Persona.builder()
                .id(1L)
                .nombres("Carlos")
                .apellidoPaterno("Flores")
                .apellidoMaterno("Mendoza")
                .numeroDocumento("44556677")
                .activo(true)
                .build();

        rolAdmin = Rol.builder()
                .id(1L)
                .codigo("ADMIN")
                .nombre("Administrador")
                .activo(true)
                .build();

        rolDirector = Rol.builder()
                .id(2L)
                .codigo("DIRECTOR")
                .nombre("Director")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("crear usuario exitoso con contraseña hasheada y activo=true")
    void crear_datosValidos_guardaConBCryptYRetornaResponse() {
        UsuarioCreateRequest request = new UsuarioCreateRequest(1L, "cflores", "Secreta123*", 1L);

        when(usuarioRepository.existsByUsername("cflores")).thenReturn(false);
        when(personaRepository.findById(1L)).thenReturn(Optional.of(persona));
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolAdmin));
        when(passwordEncoder.encode("Secreta123*")).thenReturn("$2a$10$hashedPasswordHere");

        Usuario usuarioGuardado = Usuario.builder()
                .id(10L)
                .persona(persona)
                .username("cflores")
                .password("$2a$10$hashedPasswordHere")
                .rol(rolAdmin)
                .activo(true)
                .build();

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        UsuarioResponse response = usuarioService.crear(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("cflores", response.getUsername());
        assertEquals("ADMIN", response.getRolCodigo());
        assertTrue(response.getActivo());
        verify(passwordEncoder).encode("Secreta123*");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("crear usuario con username duplicado lanza BusinessException")
    void crear_usernameDuplicado_lanzaBusinessException() {
        UsuarioCreateRequest request = new UsuarioCreateRequest(1L, "cflores", "Secreta123*", 1L);

        when(usuarioRepository.existsByUsername("cflores")).thenReturn(true);

        assertThrows(BusinessException.class, () -> usuarioService.crear(request));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("crear usuario con persona inexistente lanza ResourceNotFoundException")
    void crear_personaNoExiste_lanzaResourceNotFoundException() {
        UsuarioCreateRequest request = new UsuarioCreateRequest(999L, "cflores", "Secreta123*", 1L);

        when(usuarioRepository.existsByUsername("cflores")).thenReturn(false);
        when(personaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.crear(request));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("crear usuario con rol inexistente lanza ResourceNotFoundException")
    void crear_rolNoExiste_lanzaResourceNotFoundException() {
        UsuarioCreateRequest request = new UsuarioCreateRequest(1L, "cflores", "Secreta123*", 999L);

        when(usuarioRepository.existsByUsername("cflores")).thenReturn(false);
        when(personaRepository.findById(1L)).thenReturn(Optional.of(persona));
        when(rolRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.crear(request));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("cambiar password con password actual correcta la hashea y guarda")
    void cambiarPassword_passwordActualCorrecta_actualizaPasswordHasheada() {
        Usuario usuario = Usuario.builder()
                .id(10L)
                .username("cflores")
                .password("$2a$10$oldHashedPassword")
                .activo(true)
                .build();

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("OldPassword123", "$2a$10$oldHashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("NewPassword456")).thenReturn("$2a$10$newHashedPassword");

        CambioPasswordRequest request = new CambioPasswordRequest("OldPassword123", "NewPassword456");

        usuarioService.cambiarPassword(10L, request);

        assertEquals("$2a$10$newHashedPassword", usuario.getPassword());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("cambiar password con password actual incorrecta lanza BusinessException")
    void cambiarPassword_passwordActualIncorrecta_lanzaBusinessException() {
        Usuario usuario = Usuario.builder()
                .id(10L)
                .username("cflores")
                .password("$2a$10$oldHashedPassword")
                .activo(true)
                .build();

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("WrongPassword", "$2a$10$oldHashedPassword")).thenReturn(false);

        CambioPasswordRequest request = new CambioPasswordRequest("WrongPassword", "NewPassword456");

        assertThrows(BusinessException.class, () -> usuarioService.cambiarPassword(10L, request));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("desactivar usuario cambia activo a false")
    void desactivar_usuarioExistente_cambiaActivoAFalse() {
        Usuario usuario = Usuario.builder()
                .id(10L)
                .username("cflores")
                .activo(true)
                .build();

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        UsuarioResponse response = usuarioService.desactivar(10L);

        assertNotNull(response);
        assertFalse(usuario.getActivo());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("activar usuario cambia activo a true")
    void activar_usuarioInactivo_cambiaActivoATrue() {
        Usuario usuario = Usuario.builder()
                .id(10L)
                .username("cflores")
                .activo(false)
                .build();

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        UsuarioResponse response = usuarioService.activar(10L);

        assertNotNull(response);
        assertTrue(usuario.getActivo());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("actualizar usuario modifica rol sin tocar la contraseña")
    void actualizar_cambiaRol_noModificaPassword() {
        Usuario usuario = Usuario.builder()
                .id(10L)
                .username("cflores")
                .password("$2a$10$immutableHash")
                .rol(rolAdmin)
                .activo(true)
                .build();

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findById(2L)).thenReturn(Optional.of(rolDirector));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        UsuarioUpdateRequest request = new UsuarioUpdateRequest(2L, null);

        UsuarioResponse response = usuarioService.actualizar(10L, request);

        assertNotNull(response);
        assertEquals(rolDirector, usuario.getRol());
        assertEquals("$2a$10$immutableHash", usuario.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
    }
}
