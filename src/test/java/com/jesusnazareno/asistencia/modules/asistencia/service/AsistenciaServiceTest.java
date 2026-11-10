package com.jesusnazareno.asistencia.modules.asistencia.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.acceso.domain.Usuario;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import com.jesusnazareno.asistencia.modules.asistencia.domain.Asistencia;
import com.jesusnazareno.asistencia.modules.asistencia.domain.enums.EstadoAsistencia;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaRequest;
import com.jesusnazareno.asistencia.modules.asistencia.dto.AsistenciaResponse;
import com.jesusnazareno.asistencia.modules.asistencia.mapper.AsistenciaMapper;
import com.jesusnazareno.asistencia.modules.asistencia.repository.AsistenciaRepository;
import com.jesusnazareno.asistencia.modules.matricula.domain.Matricula;
import com.jesusnazareno.asistencia.modules.matricula.domain.enums.EstadoMatricula;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
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
import java.time.LocalTime;
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
class AsistenciaServiceTest {

    @Mock
    private AsistenciaRepository asistenciaRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private AsistenciaMapper asistenciaMapper;
    private AsistenciaServiceImpl asistenciaService;

    private Matricula matricula;
    private Persona estudiante;
    private AnioEscolar anioEscolar;
    private Grado grado;
    private Seccion seccion;
    private Usuario usuario;
    private AsistenciaRequest requestValido;

    @BeforeEach
    void setUp() {
        asistenciaMapper = new AsistenciaMapper();
        asistenciaService = new AsistenciaServiceImpl(asistenciaRepository, matriculaRepository, asistenciaMapper, usuarioRepository);

        usuario = Usuario.builder()
                .id(1L)
                .username("auxiliar_test")
                .activo(true)
                .build();

        estudiante = Persona.builder()
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

        anioEscolar = AnioEscolar.builder()
                .id(2L)
                .anio(2026)
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 12, 20))
                .nombre("Año Escolar 2026")
                .activo(true)
                .build();

        grado = Grado.builder()
                .id(3L)
                .nivel(NivelEducativo.SECUNDARIA)
                .numero(1)
                .nombre("Primer Grado de Secundaria")
                .activo(true)
                .build();

        seccion = Seccion.builder()
                .id(4L)
                .grado(grado)
                .nombre("A")
                .turno(Turno.MANANA)
                .capacidad(30)
                .activo(true)
                .build();

        matricula = Matricula.builder()
                .id(10L)
                .estudiante(estudiante)
                .anioEscolar(anioEscolar)
                .grado(grado)
                .seccion(seccion)
                .codigoMatricula("MAT-2026-001")
                .fechaMatricula(LocalDate.of(2026, 2, 15))
                .estado(EstadoMatricula.MATRICULADO)
                .build();

        requestValido = AsistenciaRequest.builder()
                .matriculaId(10L)
                .fecha(LocalDate.of(2026, 3, 15))
                .estado(EstadoAsistencia.PRESENTE)
                .horaIngreso(LocalTime.of(7, 50))
                .horaSalida(LocalTime.of(13, 30))
                .observacion("Asistencia puntual")
                .registradoPor(1L)
                .build();
    }

    @Test
    @DisplayName("registrar con datos válidos guarda y retorna respuesta")
    void registrar_asistenciaValida_guardaYRetornaResponse() {
        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matricula));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(asistenciaRepository.existsByMatriculaIdAndFecha(10L, LocalDate.of(2026, 3, 15))).thenReturn(false);

        Asistencia guardada = Asistencia.builder()
                .id(100L)
                .matricula(matricula)
                .fecha(LocalDate.of(2026, 3, 15))
                .estado(EstadoAsistencia.PRESENTE)
                .horaIngreso(LocalTime.of(7, 50))
                .horaSalida(LocalTime.of(13, 30))
                .observacion("Asistencia puntual")
                .registradoPor(usuario)
                .build();
        when(asistenciaRepository.save(any(Asistencia.class))).thenReturn(guardada);

        AsistenciaResponse response = asistenciaService.registrar(requestValido);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(EstadoAsistencia.PRESENTE, response.getEstado());
        verify(asistenciaRepository).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("registrar cuando matrícula no existe lanza ResourceNotFoundException")
    void registrar_matriculaNoExiste_lanzaResourceNotFoundException() {
        when(matriculaRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> asistenciaService.registrar(requestValido));
        verify(asistenciaRepository, never()).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("registrar cuando usuario no existe lanza ResourceNotFoundException")
    void registrar_usuarioNoExiste_lanzaResourceNotFoundException() {
        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matricula));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> asistenciaService.registrar(requestValido));
        verify(asistenciaRepository, never()).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("registrar cuando matrícula no está en estado MATRICULADO lanza BusinessException")
    void registrar_matriculaInvalidaEstadoNoMatriculado_lanzaBusinessException() {
        matricula.setEstado(EstadoMatricula.RETIRADO);
        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matricula));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertThrows(BusinessException.class, () -> asistenciaService.registrar(requestValido));
        verify(asistenciaRepository, never()).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("registrar cuando el año escolar asociado está inactivo lanza BusinessException")
    void registrar_anioEscolarInactivo_lanzaBusinessException() {
        anioEscolar.setActivo(false);
        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matricula));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertThrows(BusinessException.class, () -> asistenciaService.registrar(requestValido));
        verify(asistenciaRepository, never()).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("registrar con matrícula y fecha duplicada lanza BusinessException")
    void registrar_matriculaFechaDuplicada_lanzaBusinessException() {
        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matricula));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(asistenciaRepository.existsByMatriculaIdAndFecha(10L, LocalDate.of(2026, 3, 15))).thenReturn(true);

        assertThrows(BusinessException.class, () -> asistenciaService.registrar(requestValido));
        verify(asistenciaRepository, never()).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("registrar con hora de salida anterior a hora de ingreso lanza BusinessException")
    void registrar_horaSalidaAnteriorAHoraIngreso_lanzaBusinessException() {
        requestValido.setHoraIngreso(LocalTime.of(12, 0));
        requestValido.setHoraSalida(LocalTime.of(8, 0));

        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matricula));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(asistenciaRepository.existsByMatriculaIdAndFecha(10L, LocalDate.of(2026, 3, 15))).thenReturn(false);

        assertThrows(BusinessException.class, () -> asistenciaService.registrar(requestValido));
        verify(asistenciaRepository, never()).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("registrar estudiante AUSENTE con hora de ingreso lanza BusinessException")
    void registrar_ausenteConHoraIngreso_lanzaBusinessException() {
        requestValido.setEstado(EstadoAsistencia.AUSENTE);
        requestValido.setHoraIngreso(LocalTime.of(8, 0));
        requestValido.setHoraSalida(null);

        when(matriculaRepository.findById(10L)).thenReturn(Optional.of(matricula));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(asistenciaRepository.existsByMatriculaIdAndFecha(10L, LocalDate.of(2026, 3, 15))).thenReturn(false);

        assertThrows(BusinessException.class, () -> asistenciaService.registrar(requestValido));
        verify(asistenciaRepository, never()).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("actualizar modifica campos permitidos correctamente")
    void actualizar_cuandoExiste_actualizaCamposControlados() {
        Asistencia existente = Asistencia.builder()
                .id(100L)
                .matricula(matricula)
                .fecha(LocalDate.of(2026, 3, 15))
                .estado(EstadoAsistencia.TARDANZA)
                .horaIngreso(LocalTime.of(8, 15))
                .registradoPor(usuario)
                .build();
        when(asistenciaRepository.findById(100L)).thenReturn(Optional.of(existente));
        when(asistenciaRepository.save(any(Asistencia.class))).thenAnswer(inv -> inv.getArgument(0));

        AsistenciaRequest updateRequest = AsistenciaRequest.builder()
                .matriculaId(10L)
                .fecha(LocalDate.of(2026, 3, 15))
                .estado(EstadoAsistencia.JUSTIFICADO)
                .horaIngreso(null)
                .horaSalida(null)
                .observacion("Justificación médica aprobada")
                .registradoPor(1L)
                .build();

        AsistenciaResponse response = asistenciaService.actualizar(100L, updateRequest);

        assertNotNull(response);
        assertEquals(EstadoAsistencia.JUSTIFICADO, response.getEstado());
        assertEquals("Justificación médica aprobada", response.getObservacion());
        verify(asistenciaRepository).save(existente);
    }

    @Test
    @DisplayName("actualizar impide modificar la matrícula del registro histórico")
    void actualizar_intentoCambiarMatricula_lanzaBusinessException() {
        Asistencia existente = Asistencia.builder()
                .id(100L)
                .matricula(matricula)
                .fecha(LocalDate.of(2026, 3, 15))
                .estado(EstadoAsistencia.PRESENTE)
                .registradoPor(usuario)
                .build();
        when(asistenciaRepository.findById(100L)).thenReturn(Optional.of(existente));

        AsistenciaRequest updateRequest = AsistenciaRequest.builder()
                .matriculaId(999L) // Diferente matrícula
                .fecha(LocalDate.of(2026, 3, 15))
                .estado(EstadoAsistencia.PRESENTE)
                .registradoPor(1L)
                .build();

        assertThrows(BusinessException.class, () -> asistenciaService.actualizar(100L, updateRequest));
        verify(asistenciaRepository, never()).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("actualizar impide modificar la fecha del registro histórico")
    void actualizar_intentoCambiarFecha_lanzaBusinessException() {
        Asistencia existente = Asistencia.builder()
                .id(100L)
                .matricula(matricula)
                .fecha(LocalDate.of(2026, 3, 15))
                .estado(EstadoAsistencia.PRESENTE)
                .registradoPor(usuario)
                .build();
        when(asistenciaRepository.findById(100L)).thenReturn(Optional.of(existente));

        AsistenciaRequest updateRequest = AsistenciaRequest.builder()
                .matriculaId(10L)
                .fecha(LocalDate.of(2026, 3, 20)) // Diferente fecha
                .estado(EstadoAsistencia.PRESENTE)
                .registradoPor(1L)
                .build();

        assertThrows(BusinessException.class, () -> asistenciaService.actualizar(100L, updateRequest));
        verify(asistenciaRepository, never()).save(any(Asistencia.class));
    }

    @Test
    @DisplayName("buscarPorId retorna registro cuando existe")
    void buscarPorId_cuandoExiste_retornaResponse() {
        Asistencia existente = Asistencia.builder()
                .id(100L)
                .matricula(matricula)
                .fecha(LocalDate.of(2026, 3, 15))
                .estado(EstadoAsistencia.PRESENTE)
                .registradoPor(usuario)
                .build();
        when(asistenciaRepository.findById(100L)).thenReturn(Optional.of(existente));

        AsistenciaResponse response = asistenciaService.buscarPorId(100L);

        assertNotNull(response);
        assertEquals(100L, response.getId());
    }

    @Test
    @DisplayName("buscarPorId lanza ResourceNotFoundException cuando no existe")
    void buscarPorId_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(asistenciaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> asistenciaService.buscarPorId(999L));
    }

    @Test
    @DisplayName("listarPorFecha retorna lista de asistencias")
    void listarPorFecha_retornaLista() {
        Asistencia existente = Asistencia.builder()
                .id(100L)
                .matricula(matricula)
                .fecha(LocalDate.of(2026, 3, 15))
                .estado(EstadoAsistencia.PRESENTE)
                .registradoPor(usuario)
                .build();
        when(asistenciaRepository.findByFecha(LocalDate.of(2026, 3, 15))).thenReturn(List.of(existente));

        List<AsistenciaResponse> lista = asistenciaService.listarPorFecha(LocalDate.of(2026, 3, 15));

        assertNotNull(lista);
        assertEquals(1, lista.size());
    }

    @Test
    @DisplayName("listarPorRangoDeFechas lanza BusinessException si fecha fin es anterior a fecha inicio")
    void listarPorRangoDeFechas_cuandoFechaFinAnteriorAInicio_lanzaBusinessException() {
        LocalDate inicio = LocalDate.of(2026, 3, 20);
        LocalDate fin = LocalDate.of(2026, 3, 10);

        assertThrows(BusinessException.class, () -> asistenciaService.listarPorRangoDeFechas(inicio, fin, null));
    }
}
