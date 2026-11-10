package com.jesusnazareno.asistencia.config.security;

import com.jesusnazareno.asistencia.modules.acceso.domain.Permiso;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.RolPermisoRepository;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final RolPermisoRepository rolPermisoRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository,
                                    RolPermisoRepository rolPermisoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolPermisoRepository = rolPermisoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsernameWithDetails(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));

        Set<GrantedAuthority> authorities = new HashSet<>();

        // Normalizar rol: evitar ROLE_ROLE_
        if (usuario.getRol() != null && usuario.getRol().getCodigo() != null) {
            String rolCodigo = usuario.getRol().getCodigo().trim().toUpperCase();
            String rolAuthority = rolCodigo.startsWith("ROLE_") ? rolCodigo : "ROLE_" + rolCodigo;
            authorities.add(new SimpleGrantedAuthority(rolAuthority));

            // Agregar permisos activos del rol
            List<Permiso> permisos = rolPermisoRepository.findPermisosActivosByRolId(usuario.getRol().getId());
            for (Permiso permiso : permisos) {
                if (permiso.getCodigo() != null && !permiso.getCodigo().isBlank()) {
                    authorities.add(new SimpleGrantedAuthority(permiso.getCodigo().trim().toUpperCase()));
                }
            }
        }

        boolean isActivo = Boolean.TRUE.equals(usuario.getActivo());

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .disabled(!isActivo)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .authorities(new ArrayList<>(authorities))
                .build();
    }
}
