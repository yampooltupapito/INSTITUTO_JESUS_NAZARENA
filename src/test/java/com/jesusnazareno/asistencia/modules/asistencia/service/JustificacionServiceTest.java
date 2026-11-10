package com.jesusnazareno.asistencia.modules.asistencia.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Asistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Justificacion;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoJustificacion;
import com.jesusnazareno.asistencia.modules.asistencia.dto.JustificacionRequest;
import com.jesusnazareno.asistencia.modules.asistencia.dto.JustificacionResponse;
import com.jesusnazareno.asistencia.modules.asistencia.dto.RevisionJustificacionRequest;
import com.jesusnazareno.asistencia.modules.asistencia.mapper.JustificacionMapper;
import com.jesusnazareno.asistencia.modules.asistencia.repository.AsistenciaRepository;
import com.jesusnazareno.asistencia.modules.asistencia.repository.JustificacionRepository;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JustificacionServiceTest {

    @Mock
    private JustificacionRepository justificacionRepository;

    @Mock
    private AsistenciaRepository asistenciaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private JustificacionMapper justificacionMapper;
    private JustificacionServiceImpl justificacionService;

    private Asistencia asistenciaAusente;
    private Asistencia asistenciaTardanza;
    private Asistencia asistenciaPresente;
    private Usuario revisor;
    private JustificacionRequest requestValido;

    @BeforeEach
    void setUp() {
        justificacionMapper = new JustificacionMapper();
        justificacionService = new JustificacionServiceImpl(
                justificacionRepository,
                asistenciaRepository,
                justificacionMapper,
                usuarioRepository
        );

        revisor = Usuario.builder()
                .id(2L)
                .username("director_test")
                .activo(true)
                .build();

        Persona estudiante = Persona.builder()
                .id(1L)
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("12345678")
                .nombres("Juan")
                .apellidoPaterno("Pérez")
                .apellidoMaterno("Gómez")
                .fechaNacimiento(LocalDate.of(2012, 3, 10))
                .sexo(Sexo.MASCULINO)
                .activo(true)
                .build();

        Matricula matricula = Matricula.builder()
                .id(10L)
                .estudiante(estudiante)
                .build();

        asistenciaAusente = Asistencia.builder()
                .id(100L)
                .matricula(matricula)
                .fecha(LocalDate.of(2026, 3, 15))
                .estado(EstadoAsistencia.AUSENTE)
                .build();

        asistenciaTardanza = Asistencia.builder()
                .id(101L)
                .matricula(matricula)
                .fecha(LocalDate.of(2026, 3, 16))
                .estado(EstadoAsistencia.TARDANZA)
                .build();

        asistenciaPresente = Asistencia.builder()
                .id(102L)
                .matricula(matricula)
                .fecha(LocalDate.of(2026, 3, 17))
                .estado(EstadoAsistencia.PRESENTE)
                .build();

        requestValido = JustificacionRequest.builder()
                .asistenciaId(100L)
                .motivo("Problema de salud")
                .descripcion("Reposo médico indicado por 24 horas")
                .build();
    }

    @Test
    @DisplayName("crear con asistencia AUSENTE genera justificación en estado PENDIENTE")
    void crear_justificarAusente_creaEnEstadoPendiente() {
        when(asistenciaRepository.findById(100L)).thenReturn(Optional.of(asistenciaAusente));
        when(justificacionRepository.existsByAsistenciaIdAndActivoTrue(100L)).thenReturn(false);

        Justificacion guardada = Justificacion.builder()
                .id(500L)
                .asistencia(asistenciaAusente)
                .motivo("Problema de salud")
                .descripcion("Reposo médico indicado por 24 horas")
                .fechaSolicitud(LocalDate.now())
                .estado(EstadoJustificacion.PENDIENTE)
                .activo(true)
                .build();

        when(justificacionRepository.save(any(Justificacion.class))).thenReturn(guardada);

        JustificacionResponse response = justificacionService.crear(requestValido);

        assertNotNull(response);
        assertEquals(500L, response.getId());
        assertEquals(EstadoJustificacion.PENDIENTE, response.getEstado());
        assertEquals("Problema de salud", response.getMotivo());
        verify(justificacionRepository).save(any(Justificacion.class));
    }

    @Test
    @DisplayName("crear con asistencia TARDANZA es permitido y genera estado PENDIENTE")
    void crear_justificarTardanza_creaEnEstadoPendiente() {
        when(asistenciaRepository.findById(101L)).thenReturn(Optional.of(asistenciaTardanza));
        when(justificacionRepository.existsByAsistenciaIdAndActivoTrue(101L)).thenReturn(false);

        requestValido.setAsistenciaId(101L);

        Justificacion guardada = Justificacion.builder()
                .id(501L)
                .asistencia(asistenciaTardanza)
                .motivo("Problema de salud")
                .estado(EstadoJustificacion.PENDIENTE)
                .activo(true)
                .build();

        when(justificacionRepository.save(any(Justificacion.class))).thenReturn(guardada);

        JustificacionResponse response = justificacionService.crear(requestValido);

        assertNotNull(response);
        assertEquals(501L, response.getId());
        assertEquals(EstadoJustificacion.PENDIENTE, response.getEstado());
    }

    @Test
    @DisplayName("crear sobre asistencia PRESENTE lanza BusinessException")
    void crear_asistenciaPresente_lanzaBusinessException() {
        when(asistenciaRepository.findById(102L)).thenReturn(Optional.of(asistenciaPresente));
        requestValido.setAsistenciaId(102L);

        assertThrows(BusinessException.class, () -> justificacionService.crear(requestValido));
        verify(justificacionRepository, never()).save(any(Justificacion.class));
    }

    @Test
    @DisplayName("crear sobre asistencia ya justificada lanza BusinessException")
    void crear_asistenciaYaJustificada_lanzaBusinessException() {
        Asistencia asistenciaJustificada = Asistencia.builder()
                .id(103L)
                .estado(EstadoAsistencia.JUSTIFICADO)
                .build();
        when(asistenciaRepository.findById(103L)).thenReturn(Optional.of(asistenciaJustificada));
        requestValido.setAsistenciaId(103L);

        assertThrows(BusinessException.class, () -> justificacionService.crear(requestValido));
        verify(justificacionRepository, never()).save(any(Justificacion.class));
    }

    @Test
    @DisplayName("crear duplicando justificación activa para la misma asistencia lanza BusinessException")
    void crear_justificacionDuplicada_lanzaBusinessException() {
        when(asistenciaRepository.findById(100L)).thenReturn(Optional.of(asistenciaAusente));
        when(justificacionRepository.existsByAsistenciaIdAndActivoTrue(100L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> justificacionService.crear(requestValido));
        verify(justificacionRepository, never()).save(any(Justificacion.class));
    }

    @Test
    @DisplayName("crear cuando asistencia no existe lanza ResourceNotFoundException")
    void crear_asistenciaNoExiste_lanzaResourceNotFoundException() {
        when(asistenciaRepository.findById(999L)).thenReturn(Optional.empty());
        requestValido.setAsistenciaId(999L);

        assertThrows(ResourceNotFoundException.class, () -> justificacionService.crear(requestValido));
        verify(justificacionRepository, never()).save(any(Justificacion.class));
    }

    @Test
    @DisplayName("aprobar cambia estado a APROBADA y actualiza la asistencia a JUSTIFICADO")
    void aprobar_cuandoPendiente_cambiaEstadoAprobadaYActualizaAsistenciaAJustificado() {
        Justificacion justificacion = Justificacion.builder()
                .id(500L)
                .asistencia(asistenciaAusente)
                .motivo("Salud")
                .estado(EstadoJustificacion.PENDIENTE)
                .activo(true)
                .build();

        when(justificacionRepository.findById(500L)).thenReturn(Optional.of(justificacion));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(revisor));
        when(justificacionRepository.save(any(Justificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        RevisionJustificacionRequest revision = RevisionJustificacionRequest.builder()
                .revisadoPor(2L)
                .observacionRevision("Aprobado con constancia")
                .build();

        JustificacionResponse response = justificacionService.aprobar(500L, revision);

        assertNotNull(response);
        assertEquals(EstadoJustificacion.APROBADA, response.getEstado());
        assertEquals("Aprobado con constancia", response.getObservacionRevision());
        assertEquals(2L, response.getRevisadoPor());

        // Verificar que la asistencia cambió a JUSTIFICADO
        assertEquals(EstadoAsistencia.JUSTIFICADO, asistenciaAusente.getEstado());
        verify(asistenciaRepository).save(asistenciaAusente);
        verify(justificacionRepository).save(justificacion);
    }

    @Test
    @DisplayName("rechazar cambia estado a RECHAZADA y NO modifica el estado de la asistencia")
    void rechazar_cuandoPendiente_cambiaEstadoRechazadaYConservaEstadoOriginalAsistencia() {
        Justificacion justificacion = Justificacion.builder()
                .id(500L)
                .asistencia(asistenciaTardanza)
                .motivo("Tráfico")
                .estado(EstadoJustificacion.PENDIENTE)
                .activo(true)
                .build();

        when(justificacionRepository.findById(500L)).thenReturn(Optional.of(justificacion));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(revisor));
        when(justificacionRepository.save(any(Justificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        RevisionJustificacionRequest revision = RevisionJustificacionRequest.builder()
                .revisadoPor(2L)
                .observacionRevision("Justificación insuficiente")
                .build();

        JustificacionResponse response = justificacionService.rechazar(500L, revision);

        assertNotNull(response);
        assertEquals(EstadoJustificacion.RECHAZADA, response.getEstado());

        // La asistencia debe conservar TARDANZA y no modificarse
        assertEquals(EstadoAsistencia.TARDANZA, asistenciaTardanza.getEstado());
        verify(asistenciaRepository, never()).save(any(Asistencia.class));
        verify(justificacionRepository).save(justificacion);
    }

    @Test
    @DisplayName("aprobar cuando usuario revisor no existe lanza ResourceNotFoundException")
    void aprobar_cuandoRevisorNoExiste_lanzaResourceNotFoundException() {
        Justificacion justificacion = Justificacion.builder()
                .id(500L)
                .asistencia(asistenciaAusente)
                .estado(EstadoJustificacion.PENDIENTE)
                .activo(true)
                .build();
        when(justificacionRepository.findById(500L)).thenReturn(Optional.of(justificacion));
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

        RevisionJustificacionRequest revision = RevisionJustificacionRequest.builder()
                .revisadoPor(999L)
                .build();

        assertThrows(ResourceNotFoundException.class, () -> justificacionService.aprobar(500L, revision));
        verify(asistenciaRepository, never()).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("aprobar cuando ya no está PENDIENTE lanza BusinessException")
    void aprobar_cuandoNoEstaPendiente_lanzaBusinessException() {
        Justificacion justificacion = Justificacion.builder()
                .id(500L)
                .asistencia(asistenciaAusente)
                .estado(EstadoJustificacion.APROBADA)
                .activo(true)
                .build();
        when(justificacionRepository.findById(500L)).thenReturn(Optional.of(justificacion));

        RevisionJustificacionRequest revision = RevisionJustificacionRequest.builder()
                .revisadoPor(2L)
                .build();

        assertThrows(BusinessException.class, () -> justificacionService.aprobar(500L, revision));
        verify(asistenciaRepository, never()).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("rechazar cuando ya no está PENDIENTE lanza BusinessException")
    void rechazar_cuandoNoEstaPendiente_lanzaBusinessException() {
        Justificacion justificacion = Justificacion.builder()
                .id(500L)
                .asistencia(asistenciaAusente)
                .estado(EstadoJustificacion.RECHAZADA)
                .activo(true)
                .build();
        when(justificacionRepository.findById(500L)).thenReturn(Optional.of(justificacion));

        RevisionJustificacionRequest revision = RevisionJustificacionRequest.builder()
                .revisadoPor(2L)
                .build();

        assertThrows(BusinessException.class, () -> justificacionService.rechazar(500L, revision));
        verify(asistenciaRepository, never()).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("buscarPorId retorna justificación cuando existe")
    void buscarPorId_cuandoExiste_retornaResponse() {
        Justificacion justificacion = Justificacion.builder()
                .id(500L)
                .asistencia(asistenciaAusente)
                .motivo("Cita dental")
                .estado(EstadoJustificacion.PENDIENTE)
                .activo(true)
                .build();
        when(justificacionRepository.findById(500L)).thenReturn(Optional.of(justificacion));

        JustificacionResponse response = justificacionService.buscarPorId(500L);

        assertNotNull(response);
        assertEquals(500L, response.getId());
    }

    @Test
    @DisplayName("listarPorAsistencia retorna lista filtrada")
    void listarPorAsistencia_retornaLista() {
        Justificacion justificacion = Justificacion.builder()
                .id(500L)
                .asistencia(asistenciaAusente)
                .motivo("Cita dental")
                .build();
        when(justificacionRepository.findByAsistenciaId(100L)).thenReturn(List.of(justificacion));

        List<JustificacionResponse> lista = justificacionService.listarPorAsistencia(100L);

        assertNotNull(lista);
        assertEquals(1, lista.size());
    }

    @Test
    @DisplayName("buscarActivaPorAsistencia retorna justificación activa si existe")
    void buscarActivaPorAsistencia_cuandoExiste_retornaOptional() {
        Justificacion justificacion = Justificacion.builder()
                .id(500L)
                .asistencia(asistenciaAusente)
                .motivo("Cita dental")
                .activo(true)
                .build();
        when(justificacionRepository.findByAsistenciaIdAndActivoTrue(100L)).thenReturn(Optional.of(justificacion));

        Optional<JustificacionResponse> opt = justificacionService.buscarActivaPorAsistencia(100L);

        assertNotNull(opt);
        assertEquals(true, opt.isPresent());
        assertEquals(500L, opt.get().getId());
    }

    @Test
    @DisplayName("buscarJustificaciones delega en repository con filtros y paginación")
    void buscarJustificaciones_delegaEnRepository() {
        Justificacion justificacion = Justificacion.builder()
                .id(500L)
                .asistencia(asistenciaAusente)
                .motivo("Cita dental")
                .activo(true)
                .build();
        org.springframework.data.domain.Page<Justificacion> page = new org.springframework.data.domain.PageImpl<>(List.of(justificacion));
        when(justificacionRepository.buscarJustificaciones(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);

        org.springframework.data.domain.Page<JustificacionResponse> res = justificacionService.buscarJustificaciones(
                "QUISPE", EstadoJustificacion.PENDIENTE, null, null, null, null, null,
                org.springframework.data.domain.PageRequest.of(0, 10));

        assertNotNull(res);
        assertEquals(1, res.getTotalElements());
    }
}
