package com.jesusnazareno.asistencia.modules.estudiante.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ResourceNotFoundException;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.PersonaActor;
import com.jesusnazareno.asistencia.modules.persona.domain.TipoActor;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaResponse;
import com.jesusnazareno.asistencia.modules.persona.mapper.PersonaMapper;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaActorRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.TipoActorRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudianteServiceTest {

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private PersonaActorRepository personaActorRepository;

    @Mock
    private TipoActorRepository tipoActorRepository;

    @Mock
    private com.jesusnazareno.asistencia.modules.persona.service.PersonaService personaService;

    private PersonaMapper personaMapper;
    private EstudianteServiceImpl estudianteService;

    private Persona persona;
    private TipoActor tipoActorEstudiante;

    @BeforeEach
    void setUp() {
        personaMapper = new PersonaMapper();
        estudianteService = new EstudianteServiceImpl(
                personaRepository,
                personaActorRepository,
                tipoActorRepository,
                personaMapper,
                personaService
        );

        persona = Persona.builder()
                .id(100L)
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("77889900")
                .nombres("Carlos")
                .apellidoPaterno("Mendoza")
                .apellidoMaterno("Flores")
                .fechaNacimiento(LocalDate.of(2010, 5, 15))
                .sexo(Sexo.MASCULINO)
                .activo(true)
                .build();

        tipoActorEstudiante = TipoActor.builder()
                .id(5L)
                .codigo(EstudianteServiceImpl.CODIGO_ACTOR_ESTUDIANTE)
                .nombre("Estudiante / Alumno")
                .activo(true)
                .build();
    }

    @Test
    @DisplayName("esEstudiante retorna true cuando la persona tiene actor activo")
    void esEstudiante_cuandoTieneActorActivo_retornaTrue() {
        when(personaActorRepository.existsByPersonaIdAndTipoActorCodigoAndActivoTrue(100L, "ESTUDIANTE"))
                .thenReturn(true);

        boolean resultado = estudianteService.esEstudiante(100L);

        assertTrue(resultado);
        verify(personaActorRepository).existsByPersonaIdAndTipoActorCodigoAndActivoTrue(100L, "ESTUDIANTE");
    }

    @Test
    @DisplayName("esEstudiante retorna false cuando la persona no tiene actor activo")
    void esEstudiante_cuandoNoTieneActorActivo_retornaFalse() {
        when(personaActorRepository.existsByPersonaIdAndTipoActorCodigoAndActivoTrue(100L, "ESTUDIANTE"))
                .thenReturn(false);

        boolean resultado = estudianteService.esEstudiante(100L);

        assertFalse(resultado);
    }

    @Test
    @DisplayName("obtenerEstudiantePorId lanza ResourceNotFoundException si la persona no existe")
    void obtenerEstudiantePorId_cuandoNoExiste_lanzaResourceNotFoundException() {
        when(personaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> estudianteService.obtenerEstudiantePorId(999L));
    }

    @Test
    @DisplayName("obtenerEstudiantePorId lanza BusinessException si la persona existe pero no es estudiante")
    void obtenerEstudiantePorId_cuandoExistePeroNoEsEstudiante_lanzaBusinessException() {
        when(personaRepository.findById(100L)).thenReturn(Optional.of(persona));
        when(personaActorRepository.existsByPersonaIdAndTipoActorCodigoAndActivoTrue(100L, "ESTUDIANTE"))
                .thenReturn(false);

        assertThrows(BusinessException.class, () -> estudianteService.obtenerEstudiantePorId(100L));
    }

    @Test
    @DisplayName("obtenerEstudiantePorId retorna DTO cuando existe y es estudiante activo")
    void obtenerEstudiantePorId_cuandoExisteYEsEstudiante_retornaResponse() {
        when(personaRepository.findById(100L)).thenReturn(Optional.of(persona));
        when(personaActorRepository.existsByPersonaIdAndTipoActorCodigoAndActivoTrue(100L, "ESTUDIANTE"))
                .thenReturn(true);

        PersonaResponse response = estudianteService.obtenerEstudiantePorId(100L);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Carlos", response.getNombres());
        assertEquals("77889900", response.getNumeroDocumento());
    }

    @Test
    @DisplayName("listarEstudiantesActivos retorna página de estudiantes")
    void listarEstudiantesActivos_retornaPagina() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Persona> paginaPersonas = new PageImpl<>(List.of(persona), pageable, 1);
        when(personaActorRepository.findPersonasActivasByTipoActorCodigo(eq("ESTUDIANTE"), eq(pageable)))
                .thenReturn(paginaPersonas);

        Page<PersonaResponse> resultado = estudianteService.listarEstudiantesActivos(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Carlos", resultado.getContent().get(0).getNombres());
    }

    @Test
    @DisplayName("asignarActorEstudiante asigna nuevo actor cuando no lo tenía")
    void asignarActorEstudiante_cuandoNoTeniaActor_asignaCorrectamente() {
        when(personaRepository.findById(100L)).thenReturn(Optional.of(persona));
        when(tipoActorRepository.findByCodigo("ESTUDIANTE")).thenReturn(Optional.of(tipoActorEstudiante));
        when(personaActorRepository.findByPersonaIdAndTipoActorId(100L, 5L)).thenReturn(Optional.empty());

        PersonaActor nuevoActor = PersonaActor.builder()
                .id(1L)
                .persona(persona)
                .tipoActor(tipoActorEstudiante)
                .fechaInicio(LocalDate.now())
                .activo(true)
                .build();
        when(personaActorRepository.save(any(PersonaActor.class))).thenReturn(nuevoActor);

        PersonaActor resultado = estudianteService.asignarActorEstudiante(100L);

        assertNotNull(resultado);
        assertTrue(resultado.getActivo());
        verify(personaActorRepository).save(any(PersonaActor.class));
    }

    @Test
    @DisplayName("asignarActorEstudiante lanza BusinessException si ya es estudiante activo")
    void asignarActorEstudiante_cuandoYaEsActivo_lanzaBusinessException() {
        when(personaRepository.findById(100L)).thenReturn(Optional.of(persona));
        when(tipoActorRepository.findByCodigo("ESTUDIANTE")).thenReturn(Optional.of(tipoActorEstudiante));

        PersonaActor actorExistente = PersonaActor.builder()
                .id(1L)
                .persona(persona)
                .tipoActor(tipoActorEstudiante)
                .activo(true)
                .build();
        when(personaActorRepository.findByPersonaIdAndTipoActorId(100L, 5L)).thenReturn(Optional.of(actorExistente));

        assertThrows(BusinessException.class, () -> estudianteService.asignarActorEstudiante(100L));
        verify(personaActorRepository, never()).save(any(PersonaActor.class));
    }

    @Test
    @DisplayName("asignarActorEstudiante reactiva actor si existía inactivo")
    void asignarActorEstudiante_cuandoEstabaInactivo_reactivaActor() {
        when(personaRepository.findById(100L)).thenReturn(Optional.of(persona));
        when(tipoActorRepository.findByCodigo("ESTUDIANTE")).thenReturn(Optional.of(tipoActorEstudiante));

        PersonaActor actorInactivo = PersonaActor.builder()
                .id(1L)
                .persona(persona)
                .tipoActor(tipoActorEstudiante)
                .activo(false)
                .build();
        when(personaActorRepository.findByPersonaIdAndTipoActorId(100L, 5L)).thenReturn(Optional.of(actorInactivo));
        when(personaActorRepository.save(any(PersonaActor.class))).thenAnswer(inv -> inv.getArgument(0));

        PersonaActor resultado = estudianteService.asignarActorEstudiante(100L);

        assertNotNull(resultado);
        assertTrue(resultado.getActivo());
        verify(personaActorRepository).save(actorInactivo);
    }

    @Test
    @DisplayName("registrarEstudiante crea persona y asigna actor ESTUDIANTE")
    void registrarEstudiante_exitoso() {
        com.jesusnazareno.asistencia.modules.persona.dto.PersonaRequest request = new com.jesusnazareno.asistencia.modules.persona.dto.PersonaRequest(
                TipoDocumento.DNI, "77889911", "Ana", "Torres", "Vega",
                LocalDate.of(2011, 3, 20), Sexo.FEMENINO, "999888777", "ana@correo.com", "Calle 1"
        );
        PersonaResponse responseMock = PersonaResponse.builder()
                .id(200L)
                .numeroDocumento("77889911")
                .nombres("Ana")
                .build();

        Persona personaGuardada = Persona.builder().id(200L).build();

        when(personaService.crearPersona(request)).thenReturn(responseMock);
        when(personaRepository.findById(200L)).thenReturn(Optional.of(personaGuardada));
        when(tipoActorRepository.findByCodigo("ESTUDIANTE")).thenReturn(Optional.of(tipoActorEstudiante));
        when(personaActorRepository.findByPersonaIdAndTipoActorId(200L, 5L)).thenReturn(Optional.empty());
        when(personaActorRepository.save(any(PersonaActor.class))).thenAnswer(inv -> inv.getArgument(0));
        when(personaService.buscarPorId(200L)).thenReturn(responseMock);

        PersonaResponse resultado = estudianteService.registrarEstudiante(request);

        assertNotNull(resultado);
        assertEquals(200L, resultado.getId());
        verify(personaService).crearPersona(request);
        verify(personaActorRepository).save(any(PersonaActor.class));
    }

    @Test
    @DisplayName("buscarEstudiantes delega a personaActorRepository con filtros")
    void buscarEstudiantes_delegaCorrectamente() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Persona> paginaPersonas = new PageImpl<>(List.of(persona), pageable, 1);

        when(personaActorRepository.buscarEstudiantes(eq("Carlos"), eq(Boolean.TRUE), eq(pageable)))
                .thenReturn(paginaPersonas);

        Page<PersonaResponse> resultado = estudianteService.buscarEstudiantes("Carlos", "ACTIVOS", pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Carlos", resultado.getContent().get(0).getNombres());
        verify(personaActorRepository).buscarEstudiantes("Carlos", Boolean.TRUE, pageable);
    }

    @Test
    @DisplayName("desactivarEstudiante establece activo=false sin eliminar fisicamente")
    void desactivarEstudiante_exitoso() {
        PersonaActor actor = PersonaActor.builder()
                .id(1L)
                .persona(persona)
                .tipoActor(tipoActorEstudiante)
                .activo(true)
                .build();

        when(personaRepository.findById(100L)).thenReturn(Optional.of(persona));
        when(personaActorRepository.existsByPersonaIdAndTipoActorCodigoAndActivoTrue(100L, "ESTUDIANTE")).thenReturn(true);
        when(personaActorRepository.findByPersonaIdAndTipoActorCodigo(100L, "ESTUDIANTE")).thenReturn(Optional.of(actor));

        estudianteService.desactivarEstudiante(100L);

        assertFalse(persona.getActivo());
        assertFalse(actor.getActivo());
        assertNotNull(actor.getFechaFin());
        verify(personaRepository).save(persona);
        verify(personaActorRepository).save(actor);
        verify(personaRepository, never()).delete(any());
        verify(personaActorRepository, never()).delete(any());
    }

    @Test
    @DisplayName("activarEstudiante establece activo=true")
    void activarEstudiante_exitoso() {
        persona.setActivo(false);
        PersonaActor actor = PersonaActor.builder()
                .id(1L)
                .persona(persona)
                .tipoActor(tipoActorEstudiante)
                .activo(false)
                .fechaFin(LocalDate.now())
                .build();

        when(personaRepository.findById(100L)).thenReturn(Optional.of(persona));
        when(personaActorRepository.existsByPersonaIdAndTipoActorCodigoAndActivoTrue(100L, "ESTUDIANTE")).thenReturn(false);
        when(personaActorRepository.findByPersonaIdAndTipoActorCodigo(100L, "ESTUDIANTE")).thenReturn(Optional.of(actor));

        estudianteService.activarEstudiante(100L);

        assertTrue(persona.getActivo());
        assertTrue(actor.getActivo());
        assertEquals(null, actor.getFechaFin());
        verify(personaRepository).save(persona);
        verify(personaActorRepository).save(actor);
    }
}
