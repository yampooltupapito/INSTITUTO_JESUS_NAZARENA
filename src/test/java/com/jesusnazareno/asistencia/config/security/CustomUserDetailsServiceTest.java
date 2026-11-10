package com.jesusnazareno.asistencia.config.security;

import com.jesusnazareno.asistencia.modules.acceso.domain.Permiso;
import com.jesusnazareno.asistencia.modules.acceso.domain.Rol;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.RolPermisoRepository;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolPermisoRepository rolPermisoRepository;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new CustomUserDetailsService(usuarioRepository, rolPermisoRepository);
    }

    @Test
    @DisplayName("loadUserByUsername carga usuario activo con rol y permisos normalizados")
    void loadUserByUsername_usuarioActivo_retornaUserDetailsHabilitadoConAuthorities() {
        Rol rolAdmin = Rol.builder()
                .id(1L)
                .codigo("ADMIN")
                .nombre("Administrador")
                .activo(true)
                .build();

        Usuario usuario = Usuario.builder()
                .id(10L)
                .username("admin")
                .password("$2a$10$encodedPassword")
                .rol(rolAdmin)
                .activo(true)
                .build();

        Permiso p1 = Permiso.builder().id(101L).codigo("ASISTENCIA_REGISTRAR").activo(true).build();
        Permiso p2 = Permiso.builder().id(102L).codigo("DASHBOARD_VER").activo(true).build();

        when(usuarioRepository.findByUsernameWithDetails("admin")).thenReturn(Optional.of(usuario));
        when(rolPermisoRepository.findPermisosActivosByRolId(1L)).thenReturn(List.of(p1, p2));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("$2a$10$encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());

        Set<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertTrue(authorities.contains("ROLE_ADMIN"));
        assertTrue(authorities.contains("ASISTENCIA_REGISTRAR"));
        assertTrue(authorities.contains("DASHBOARD_VER"));
        assertFalse(authorities.contains("ROLE_ROLE_ADMIN"));
    }

    @Test
    @DisplayName("loadUserByUsername con rol que ya tiene prefijo ROLE_ no duplica prefijo")
    void loadUserByUsername_rolConPrefijoRole_noDuplicaPrefijo() {
        Rol rolDirector = Rol.builder()
                .id(2L)
                .codigo("ROLE_DIRECTOR")
                .nombre("Director")
                .activo(true)
                .build();

        Usuario usuario = Usuario.builder()
                .id(11L)
                .username("director")
                .password("$2a$10$encodedPassword")
                .rol(rolDirector)
                .activo(true)
                .build();

        when(usuarioRepository.findByUsernameWithDetails("director")).thenReturn(Optional.of(usuario));
        when(rolPermisoRepository.findPermisosActivosByRolId(2L)).thenReturn(List.of());

        UserDetails userDetails = userDetailsService.loadUserByUsername("director");

        Set<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertTrue(authorities.contains("ROLE_DIRECTOR"));
        assertFalse(authorities.contains("ROLE_ROLE_DIRECTOR"));
    }

    @Test
    @DisplayName("loadUserByUsername con usuario inactivo retorna UserDetails deshabilitado")
    void loadUserByUsername_usuarioInactivo_retornaUserDetailsDeshabilitado() {
        Rol rolAuxiliar = Rol.builder()
                .id(3L)
                .codigo("AUXILIAR")
                .activo(true)
                .build();

        Usuario usuarioInactivo = Usuario.builder()
                .id(12L)
                .username("auxiliar_baja")
                .password("$2a$10$encodedPassword")
                .rol(rolAuxiliar)
                .activo(false)
                .build();

        when(usuarioRepository.findByUsernameWithDetails("auxiliar_baja")).thenReturn(Optional.of(usuarioInactivo));
        when(rolPermisoRepository.findPermisosActivosByRolId(3L)).thenReturn(List.of());

        UserDetails userDetails = userDetailsService.loadUserByUsername("auxiliar_baja");

        assertNotNull(userDetails);
        assertFalse(userDetails.isEnabled());
    }

    @Test
    @DisplayName("loadUserByUsername con username inexistente lanza UsernameNotFoundException")
    void loadUserByUsername_usernameNoExiste_lanzaUsernameNotFoundException() {
        when(usuarioRepository.findByUsernameWithDetails("inexistente")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("inexistente"));
    }
}
