package com.jesusnazareno.asistencia.modules.auditoria.integration;

import com.jesusnazareno.asistencia.modules.acceso.domain.Rol;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.dto.CambioPasswordRequest;
import com.jesusnazareno.asistencia.modules.acceso.dto.UsuarioCreateRequest;
import com.jesusnazareno.asistencia.modules.acceso.mapper.UsuarioMapper;
import com.jesusnazareno.asistencia.modules.acceso.repository.RolRepository;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.acceso.service.UsuarioServiceImpl;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.service.AuditoriaService;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditoriaBusinessEventsTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditoriaService auditoriaService;

    @Test
    @DisplayName("Crear usuario genera auditoría USUARIO_CREAR")
    void crearUsuarioGeneraAuditoria() {
        UsuarioMapper usuarioMapper = new UsuarioMapper();
        UsuarioServiceImpl usuarioService = new UsuarioServiceImpl(
                usuarioRepository, personaRepository, rolRepository, null, usuarioMapper, passwordEncoder, auditoriaService
        );

        UsuarioCreateRequest request = UsuarioCreateRequest.builder()
                .personaId(1L)
                .rolId(2L)
                .username("docente1")
                .password("PasswordSeguro123!")
                .build();

        Persona persona = Persona.builder().id(1L).nombres("Juan").apellidoPaterno("Perez").build();
        Rol rol = Rol.builder().id(2L).nombre("DOCENTE").codigo("DOCENTE").build();

        when(usuarioRepository.existsByUsername("docente1")).thenReturn(false);
        when(usuarioRepository.existsByPersonaId(1L)).thenReturn(false);
        when(personaRepository.findById(1L)).thenReturn(Optional.of(persona));
        when(rolRepository.findById(2L)).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode(any())).thenReturn("bcrypt_hash_placeholder");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> {
            Usuario u = i.getArgument(0);
            return Usuario.builder().id(100L).persona(u.getPersona()).username(u.getUsername()).rol(u.getRol()).build();
        });

        usuarioService.crear(request);

        verify(auditoriaService).registrarEventoConContexto(
                eq(AccionAuditoria.USUARIO_CREAR),
                eq(ModuloAuditoria.USUARIOS),
                eq("USUARIO"),
                eq(100L),
                eq(ResultadoAuditoria.EXITO),
                contains("docente1"),
                contains("DOCENTE")
        );
    }

    @Test
    @DisplayName("Cambio de contraseña genera auditoría pero NUNCA registra contraseña ni hash")
    void cambiarPasswordNoRegistraDatosSensibles() {
        UsuarioMapper usuarioMapper = new UsuarioMapper();
        UsuarioServiceImpl usuarioService = new UsuarioServiceImpl(
                usuarioRepository, personaRepository, rolRepository, null, usuarioMapper, passwordEncoder, auditoriaService
        );

        Usuario usuario = Usuario.builder()
                .id(50L)
                .username("director1")
                .password("$2a$12$hashedPasswordOld")
                .build();

        when(usuarioRepository.findById(50L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("PasswordViejo123!", "$2a$12$hashedPasswordOld")).thenReturn(true);
        when(passwordEncoder.encode("PasswordNuevo123!")).thenReturn("$2a$12$hashedPasswordNew");

        CambioPasswordRequest request = new CambioPasswordRequest("PasswordViejo123!", "PasswordNuevo123!");

        usuarioService.cambiarPassword(50L, request);

        ArgumentCaptor<String> descCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> detallesCaptor = ArgumentCaptor.forClass(String.class);

        verify(auditoriaService).registrarEventoConContexto(
                eq(AccionAuditoria.USUARIO_CAMBIAR_PASSWORD),
                eq(ModuloAuditoria.USUARIOS),
                eq("USUARIO"),
                eq(50L),
                eq(ResultadoAuditoria.EXITO),
                descCaptor.capture(),
                detallesCaptor.capture()
        );

        String descripcion = descCaptor.getValue();
        String detalles = detallesCaptor.getValue();

        assertThat(descripcion).isEqualTo("Contraseña del usuario actualizada.");
        assertThat(descripcion).doesNotContain("PasswordViejo123!");
        assertThat(descripcion).doesNotContain("PasswordNuevo123!");
        assertThat(descripcion).doesNotContain("$2a$12$");

        assertThat(detalles).isNull();
    }
}
