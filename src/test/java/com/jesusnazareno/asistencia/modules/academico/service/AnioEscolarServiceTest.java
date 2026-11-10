package com.jesusnazareno.asistencia.modules.academico.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.academico.domain.AnioEscolar;
import com.jesusnazareno.asistencia.modules.academico.dto.AnioEscolarRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.AnioEscolarResponse;
import com.jesusnazareno.asistencia.modules.academico.mapper.AnioEscolarMapper;
import com.jesusnazareno.asistencia.modules.academico.repository.AnioEscolarRepository;
import com.jesusnazareno.asistencia.modules.matricula.repository.MatriculaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnioEscolarServiceTest {

    @Mock
    private AnioEscolarRepository anioEscolarRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    private AnioEscolarMapper anioEscolarMapper;
    private AnioEscolarServiceImpl anioEscolarService;

    private AnioEscolarRequest requestValido;
    private AnioEscolar anioEscolarGuardado;

    @BeforeEach
    void setUp() {
        anioEscolarMapper = new AnioEscolarMapper();
        anioEscolarService = new AnioEscolarServiceImpl(anioEscolarRepository, anioEscolarMapper, matriculaRepository);

        requestValido = AnioEscolarRequest.builder()
                .anio(2026)
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 12, 20))
                .nombre("Año Escolar 2026")
                .build();

        anioEscolarGuardado = AnioEscolar.builder()
                .id(1L)
                .anio(2026)
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 12, 20))
                .nombre("Año Escolar 2026")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("crearAnioEscolar debe guardar y retornar la respuesta correctamente")
    void testCrearAnioEscolarExitoso() {
        when(anioEscolarRepository.findByAnio(2026)).thenReturn(Optional.empty());
        when(anioEscolarRepository.save(any(AnioEscolar.class))).thenReturn(anioEscolarGuardado);

        AnioEscolarResponse response = anioEscolarService.crearAnioEscolar(requestValido);

        assertNotNull(response);
        assertEquals(2026, response.getAnio());
        assertEquals("Año Escolar 2026", response.getNombre());
        verify(anioEscolarRepository).save(any(AnioEscolar.class));
    }

    @Test
    @DisplayName("crearAnioEscolar con fecha fin anterior a fecha inicio debe lanzar BusinessException")
    void testCrearAnioEscolarFechasInvalidas() {
        AnioEscolarRequest requestInvalido = AnioEscolarRequest.builder()
                .anio(2026)
                .fechaInicio(LocalDate.of(2026, 12, 1))
                .fechaFin(LocalDate.of(2026, 3, 1)) // Anterior
                .nombre("Año Escolar 2026")
                .build();

        assertThrows(BusinessException.class, () -> anioEscolarService.crearAnioEscolar(requestInvalido));
    }

    @Test
    @DisplayName("crearAnioEscolar con año duplicado debe lanzar BusinessException")
    void testCrearAnioEscolarDuplicado() {
        when(anioEscolarRepository.findByAnio(2026)).thenReturn(Optional.of(anioEscolarGuardado));

        assertThrows(BusinessException.class, () -> anioEscolarService.crearAnioEscolar(requestValido));
    }

    @Test
    @DisplayName("buscarPorId inexistente debe lanzar ResourceNotFoundException")
    void testBuscarPorIdInexistente() {
        when(anioEscolarRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> anioEscolarService.buscarPorId(99L));
    }

    @Test
    @DisplayName("actualizarAnioEscolar cambiando año cuando ya tiene matrículas debe lanzar BusinessException")
    void testActualizarAnioConMatriculasExistentesRechazado() {
        AnioEscolar existente = AnioEscolar.builder()
                .id(1L)
                .anio(2026)
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 12, 20))
                .nombre("Año Escolar 2026")
                .activo(true)
                .build();

        AnioEscolarRequest cambio = AnioEscolarRequest.builder()
                .anio(2027) // Intento de cambiar año
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 12, 20))
                .nombre("Año 2027")
                .build();

        when(anioEscolarRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(anioEscolarRepository.findByAnio(2027)).thenReturn(Optional.empty());
        when(matriculaRepository.countByAnioEscolarId(1L)).thenReturn(5L);

        assertThrows(BusinessException.class, () -> anioEscolarService.actualizarAnioEscolar(1L, cambio));
    }

    @Test
    @DisplayName("desactivarAnioEscolar debe cambiar estado a inactivo preservando el registro")
    void testDesactivarAnioEscolarPreserva() {
        AnioEscolar existente = AnioEscolar.builder()
                .id(1L)
                .anio(2026)
                .fechaInicio(LocalDate.of(2026, 3, 1))
                .fechaFin(LocalDate.of(2026, 12, 20))
                .nombre("Año Escolar 2026")
                .activo(true)
                .build();

        when(anioEscolarRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(anioEscolarRepository.save(any(AnioEscolar.class))).thenAnswer(invocation -> invocation.getArgument(0));

        anioEscolarService.desactivarAnioEscolar(1L);

        assertEquals(false, existente.getActivo());
        verify(anioEscolarRepository).save(existente);
    }
}
