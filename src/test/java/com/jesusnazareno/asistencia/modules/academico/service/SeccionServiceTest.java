package com.jesusnazareno.asistencia.modules.academico.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.exception.ValidationException;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.Seccion;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.Turno;
import com.jesusnazareno.asistencia.modules.academico.dto.SeccionRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.SeccionResponse;
import com.jesusnazareno.asistencia.modules.academico.mapper.SeccionMapper;
import com.jesusnazareno.asistencia.modules.academico.repository.GradoRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.SeccionRepository;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeccionServiceTest {

    @Mock
    private SeccionRepository seccionRepository;

    @Mock
    private GradoRepository gradoRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    private SeccionMapper seccionMapper;
    private SeccionServiceImpl seccionService;

    private Grado gradoExistente;
    private SeccionRequest requestValido;
    private Seccion seccionGuardada;

    @BeforeEach
    void setUp() {
        seccionMapper = new SeccionMapper();
        seccionService = new SeccionServiceImpl(seccionRepository, gradoRepository, seccionMapper, matriculaRepository);

        gradoExistente = Grado.builder()
                .id(5L)
                .nivel(NivelEducativo.PRIMARIA)
                .numero(1)
                .nombre("Primer Grado de Primaria")
                .activo(true)
                .build();

        requestValido = SeccionRequest.builder()
                .gradoId(5L)
                .nombre("A")
                .turno(Turno.MANANA)
                .capacidad(30)
                .build();

        seccionGuardada = Seccion.builder()
                .id(10L)
                .grado(gradoExistente)
                .nombre("A")
                .turno(Turno.MANANA)
                .capacidad(30)
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("crearSeccion debe registrar y retornar la sección correctamente")
    void testCrearSeccionExitoso() {
        when(gradoRepository.findById(5L)).thenReturn(Optional.of(gradoExistente));
        when(seccionRepository.findByGradoIdAndNombre(5L, "A")).thenReturn(Optional.empty());
        when(seccionRepository.save(any(Seccion.class))).thenReturn(seccionGuardada);

        SeccionResponse response = seccionService.crearSeccion(requestValido);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("A", response.getNombre());
        assertEquals(Turno.MANANA, response.getTurno());
        assertEquals(30, response.getCapacidad());
        verify(seccionRepository).save(any(Seccion.class));
    }

    @Test
    @DisplayName("crearSeccion con grado inexistente debe lanzar ResourceNotFoundException")
    void testCrearSeccionGradoInexistente() {
        when(gradoRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> seccionService.crearSeccion(requestValido));
    }

    @Test
    @DisplayName("crearSeccion con capacidad menor o igual a 0 debe lanzar ValidationException")
    void testCrearSeccionCapacidadInvalida() {
        when(gradoRepository.findById(5L)).thenReturn(Optional.of(gradoExistente));

        SeccionRequest requestCapacidadCero = SeccionRequest.builder()
                .gradoId(5L)
                .nombre("B")
                .turno(Turno.MANANA)
                .capacidad(0)
                .build();

        assertThrows(ValidationException.class, () -> seccionService.crearSeccion(requestCapacidadCero));
    }

    @Test
    @DisplayName("crearSeccion con nombre duplicado en el mismo grado debe lanzar BusinessException")
    void testCrearSeccionDuplicada() {
        when(gradoRepository.findById(5L)).thenReturn(Optional.of(gradoExistente));
        when(seccionRepository.findByGradoIdAndNombre(5L, "A")).thenReturn(Optional.of(seccionGuardada));

        assertThrows(BusinessException.class, () -> seccionService.crearSeccion(requestValido));
    }

    @Test
    @DisplayName("actualizarSeccion rechazado si capacidad es menor que alumnos matriculados")
    void testActualizarSeccionCapacidadMenorQueMatriculados() {
        when(seccionRepository.findById(10L)).thenReturn(Optional.of(seccionGuardada));
        when(gradoRepository.findById(5L)).thenReturn(Optional.of(gradoExistente));
        when(matriculaRepository.countMatriculadosActivosEnSeccion(10L)).thenReturn(28L);

        SeccionRequest requestCapacidadReducida = SeccionRequest.builder()
                .gradoId(5L)
                .nombre("A")
                .turno(Turno.MANANA)
                .capacidad(25)
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> seccionService.actualizarSeccion(10L, requestCapacidadReducida));
        assertTrue(ex.getMessage().contains("matriculados"));
    }

    @Test
    @DisplayName("activar y desactivar sección modifica el estado activo correctamente")
    void testActivarYDesactivarSeccion() {
        when(seccionRepository.findById(10L)).thenReturn(Optional.of(seccionGuardada));
        when(seccionRepository.save(any(Seccion.class))).thenAnswer(inv -> inv.getArgument(0));

        seccionService.desactivarSeccion(10L);
        assertFalse(seccionGuardada.getActivo());

        seccionService.activarSeccion(10L);
        assertTrue(seccionGuardada.getActivo());
    }

    @Test
    @DisplayName("buscarPaginado calcula vacantes y matriculados correctamente")
    void testBuscarPaginadoCalculaVacantes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Seccion> page = new PageImpl<>(List.of(seccionGuardada), pageable, 1);

        when(seccionRepository.buscarConFiltros(null, null, null, null, pageable)).thenReturn(page);
        when(matriculaRepository.countMatriculadosActivosEnSeccion(10L)).thenReturn(18L);

        Page<SeccionResponse> result = seccionService.buscarPaginado(null, null, null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        SeccionResponse item = result.getContent().get(0);
        assertEquals(Integer.valueOf(18), item.getMatriculados());
        assertEquals(Integer.valueOf(12), item.getVacantes()); // 30 - 18 = 12
    }
}
