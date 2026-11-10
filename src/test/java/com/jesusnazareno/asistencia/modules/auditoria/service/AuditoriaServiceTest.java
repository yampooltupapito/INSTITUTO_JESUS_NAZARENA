package com.jesusnazareno.asistencia.modules.auditoria.service;

import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.auditoria.domain.AuditoriaEvento;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.AccionAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ModuloAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.domain.enums.ResultadoAuditoria;
import com.jesusnazareno.asistencia.modules.auditoria.dto.AuditoriaEventoDTO;
import com.jesusnazareno.asistencia.modules.auditoria.dto.AuditoriaFiltroDTO;
import com.jesusnazareno.asistencia.modules.auditoria.repository.AuditoriaEventoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditoriaServiceTest {

    @Mock
    private AuditoriaEventoRepository auditoriaEventoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AuditoriaServiceImpl auditoriaService;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = Usuario.builder()
                .id(10L)
                .username("director1")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("Registrar evento con usuario existente preserva usuario y username histórico")
    void registrarEventoConUsuario() {
        when(usuarioRepository.findByUsername("director1")).thenReturn(Optional.of(usuarioMock));
        when(auditoriaEventoRepository.save(any(AuditoriaEvento.class))).thenAnswer(i -> i.getArgument(0));

        auditoriaService.registrarEvento(
                "director1",
                AccionAuditoria.USUARIO_CREAR,
                ModuloAuditoria.USUARIOS,
                "USUARIO",
                25L,
                ResultadoAuditoria.EXITO,
                "Se creó el usuario: docente1",
                "192.168.1.100",
                "Rol: DOCENTE"
        );

        ArgumentCaptor<AuditoriaEvento> captor = ArgumentCaptor.forClass(AuditoriaEvento.class);
        verify(auditoriaEventoRepository).save(captor.capture());

        AuditoriaEvento guardado = captor.getValue();
        assertThat(guardado.getUsuario()).isEqualTo(usuarioMock);
        assertThat(guardado.getUsername()).isEqualTo("director1");
        assertThat(guardado.getAccion()).isEqualTo(AccionAuditoria.USUARIO_CREAR);
        assertThat(guardado.getModulo()).isEqualTo(ModuloAuditoria.USUARIOS);
        assertThat(guardado.getEntidadTipo()).isEqualTo("USUARIO");
        assertThat(guardado.getEntidadId()).isEqualTo(25L);
        assertThat(guardado.getResultado()).isEqualTo(ResultadoAuditoria.EXITO);
        assertThat(guardado.getDireccionIp()).isEqualTo("192.168.1.100");
    }

    @Test
    @DisplayName("Registrar evento soporta usuario null para eventos de login fallido o usuarios inexistentes")
    void registrarEventoConUsuarioNull() {
        when(usuarioRepository.findByUsername("usuario_desconocido")).thenReturn(Optional.empty());
        when(auditoriaEventoRepository.save(any(AuditoriaEvento.class))).thenAnswer(i -> i.getArgument(0));

        auditoriaService.registrarEvento(
                "usuario_desconocido",
                AccionAuditoria.LOGIN_FALLIDO,
                ModuloAuditoria.SEGURIDAD,
                null,
                null,
                ResultadoAuditoria.FALLO,
                "Credenciales inválidas para username: usuario_desconocido",
                "10.0.0.5",
                null
        );

        ArgumentCaptor<AuditoriaEvento> captor = ArgumentCaptor.forClass(AuditoriaEvento.class);
        verify(auditoriaEventoRepository).save(captor.capture());

        AuditoriaEvento guardado = captor.getValue();
        assertThat(guardado.getUsuario()).isNull();
        assertThat(guardado.getUsername()).isEqualTo("usuario_desconocido");
        assertThat(guardado.getAccion()).isEqualTo(AccionAuditoria.LOGIN_FALLIDO);
        assertThat(guardado.getResultado()).isEqualTo(ResultadoAuditoria.FALLO);
    }

    @Test
    @DisplayName("Búsqueda con filtros ejecuta Specification en PostgreSQL con paginación")
    void buscarEventosConFiltros() {
        AuditoriaFiltroDTO filtro = AuditoriaFiltroDTO.builder()
                .fechaInicio(LocalDate.now().minusDays(7))
                .fechaFin(LocalDate.now())
                .username("admin")
                .modulo(ModuloAuditoria.SEGURIDAD)
                .accion(AccionAuditoria.LOGIN_EXITOSO)
                .resultado(ResultadoAuditoria.EXITO)
                .build();

        AuditoriaEvento eventoMock = AuditoriaEvento.builder()
                .id(1L)
                .fecha(LocalDateTime.now())
                .username("admin")
                .accion(AccionAuditoria.LOGIN_EXITOSO)
                .modulo(ModuloAuditoria.SEGURIDAD)
                .resultado(ResultadoAuditoria.EXITO)
                .descripcion("Inicio de sesión exitoso")
                .direccionIp("127.0.0.1")
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        when(auditoriaEventoRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(eventoMock), pageable, 1));

        Page<AuditoriaEventoDTO> resultado = auditoriaService.buscarEventos(filtro, pageable);

        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().get(0).getUsername()).isEqualTo("admin");
        assertThat(resultado.getContent().get(0).getAccion()).isEqualTo(AccionAuditoria.LOGIN_EXITOSO);
    }

    @Test
    @DisplayName("buscarPorId devuelve DTO correctamente")
    void buscarPorIdExitoso() {
        AuditoriaEvento eventoMock = AuditoriaEvento.builder()
                .id(99L)
                .fecha(LocalDateTime.now())
                .username("admin")
                .accion(AccionAuditoria.ASISTENCIA_REGISTRO_MASIVO)
                .modulo(ModuloAuditoria.ASISTENCIA)
                .resultado(ResultadoAuditoria.EXITO)
                .descripcion("Se registró asistencia masiva: 25 registros.")
                .direccionIp("127.0.0.1")
                .build();

        when(auditoriaEventoRepository.findById(99L)).thenReturn(Optional.of(eventoMock));

        AuditoriaEventoDTO dto = auditoriaService.buscarPorId(99L);
        assertThat(dto.getId()).isEqualTo(99L);
        assertThat(dto.getAccion()).isEqualTo(AccionAuditoria.ASISTENCIA_REGISTRO_MASIVO);
    }
}
