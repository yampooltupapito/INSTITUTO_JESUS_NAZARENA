package com.jesusnazareno.asistencia.modules.academico.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.academico.domain.Grado;
import com.jesusnazareno.asistencia.modules.academico.domain.enums.NivelEducativo;
import com.jesusnazareno.asistencia.modules.academico.dto.GradoRequest;
import com.jesusnazareno.asistencia.modules.academico.dto.GradoResponse;
import com.jesusnazareno.asistencia.modules.academico.mapper.GradoMapper;
import com.jesusnazareno.asistencia.modules.academico.mapper.SeccionMapper;
import com.jesusnazareno.asistencia.modules.academico.repository.GradoRepository;
import com.jesusnazareno.asistencia.modules.academico.repository.SeccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GradoServiceTest {

    @Mock
    private GradoRepository gradoRepository;

    @Mock
    private SeccionRepository seccionRepository;

    private GradoMapper gradoMapper;
    private SeccionMapper seccionMapper;
    private GradoServiceImpl gradoService;

    private GradoRequest requestValido;
    private Grado gradoGuardado;

    @BeforeEach
    void setUp() {
        gradoMapper = new GradoMapper();
        seccionMapper = new SeccionMapper();
        gradoService = new GradoServiceImpl(gradoRepository, gradoMapper, seccionRepository, seccionMapper);

        requestValido = GradoRequest.builder()
                .nivel(NivelEducativo.PRIMARIA)
                .numero(1)
                .nombre("Primer Grado de Primaria")
                .descripcion("Primer grado")
                .build();

        gradoGuardado = Grado.builder()
                .id(5L)
                .nivel(NivelEducativo.PRIMARIA)
                .numero(1)
                .nombre("Primer Grado de Primaria")
                .descripcion("Primer grado")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("crearGrado debe registrar y retornar el grado correctamente")
    void testCrearGradoExitoso() {
        when(gradoRepository.findByNivelAndNumero(NivelEducativo.PRIMARIA, 1)).thenReturn(Optional.empty());
        when(gradoRepository.save(any(Grado.class))).thenReturn(gradoGuardado);

        GradoResponse response = gradoService.crearGrado(requestValido);

        assertNotNull(response);
        assertEquals(5L, response.getId());
        assertEquals(NivelEducativo.PRIMARIA, response.getNivel());
        assertEquals(1, response.getNumero());
        verify(gradoRepository).save(any(Grado.class));
    }

    @Test
    @DisplayName("crearGrado con nivel y número duplicados debe lanzar BusinessException")
    void testCrearGradoDuplicado() {
        when(gradoRepository.findByNivelAndNumero(NivelEducativo.PRIMARIA, 1)).thenReturn(Optional.of(gradoGuardado));

        assertThrows(BusinessException.class, () -> gradoService.crearGrado(requestValido));
    }

    @Test
    @DisplayName("buscarPorId inexistente debe lanzar ResourceNotFoundException")
    void testBuscarPorIdInexistente() {
        when(gradoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gradoService.buscarPorId(99L));
    }
}
