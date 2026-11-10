package com.jesusnazareno.asistencia.modules.apoderado.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.exception.ValidationException;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.apoderado.domain.EstudianteApoderado;
import com.jesusnazareno.asistencia.modules.apoderado.domain.enums.Parentesco;
import com.jesusnazareno.asistencia.modules.apoderado.dto.ApoderadoCreateRequest;
import com.jesusnazareno.asistencia.modules.apoderado.dto.ApoderadoResponse;
import com.jesusnazareno.asistencia.modules.apoderado.dto.ApoderadoUpdateRequest;
import com.jesusnazareno.asistencia.modules.apoderado.dto.EstudianteApoderadoRequest;
import com.jesusnazareno.asistencia.modules.apoderado.dto.EstudianteApoderadoResponse;
import com.jesusnazareno.asistencia.modules.apoderado.repository.EstudianteApoderadoRepository;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.PersonaActor;
import com.jesusnazareno.asistencia.modules.persona.domain.TipoActor;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaActorRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.TipoActorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de Negocio - Prompt 20: EstudianteApoderadoService")
class EstudianteApoderadoServiceTest {

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private PersonaActorRepository personaActorRepository;

    @Mock
    private TipoActorRepository tipoActorRepository;

    @Mock
    private EstudianteApoderadoRepository estudianteApoderadoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private EstudianteApoderadoServiceImpl estudianteApoderadoService;

    private TipoActor tipoActorPadre;
    private TipoActor tipoActorEstudiante;
    private Persona estudiante;
    private Persona apoderado;

    @BeforeEach
    void setUp() {
        tipoActorPadre = new TipoActor(1L, null, null, null, null, "PADRE_APODERADO", "Padre / Apoderado", "Tutor", true);
        tipoActorEstudiante = new TipoActor(2L, null, null, null, null, "ESTUDIANTE", "Estudiante", "Alumno", true);

        estudiante = new Persona();
        estudiante.setId(10L);
        estudiante.setNumeroDocumento("77889900");
        estudiante.setNombres("Carlos");
        estudiante.setApellidoPaterno("Mendoza");
        estudiante.setApellidoMaterno("Flores");
        estudiante.setActivo(true);

        apoderado = new Persona();
        apoderado.setId(20L);
        apoderado.setNumeroDocumento("12345678");
        apoderado.setNombres("María Elena");
        apoderado.setApellidoPaterno("Flores");
        apoderado.setApellidoMaterno("Ramos");
        apoderado.setActivo(true);
    }

    @Test
    @DisplayName("01. Registrar apoderado nuevo creando Persona + PersonaActor PADRE_APODERADO")
    void registrarApoderadoNuevo_creaPersonaYActor() {
        ApoderadoCreateRequest req = new ApoderadoCreateRequest();
        req.setTipoDocumento(TipoDocumento.DNI);
        req.setNumeroDocumento("44556677");
        req.setNombres("Juan");
        req.setApellidoPaterno("Pérez");
        req.setApellidoMaterno("Gómez");
        req.setFechaNacimiento(LocalDate.of(1980, 5, 10));
        req.setSexo(Sexo.MASCULINO);
        req.setTelefono("912345678");

        when(personaRepository.findByNumeroDocumento("44556677")).thenReturn(Optional.empty());
        when(personaRepository.save(any(Persona.class))).thenAnswer(invocation -> {
            Persona p = invocation.getArgument(0);
            p.setId(30L);
            return p;
        });
        when(tipoActorRepository.findByCodigo("PADRE_APODERADO")).thenReturn(Optional.of(tipoActorPadre));
        when(estudianteApoderadoRepository.findRelacionesByApoderadoId(30L)).thenReturn(Collections.emptyList());
        when(usuarioRepository.existsByPersonaId(30L)).thenReturn(false);

        ApoderadoResponse response = estudianteApoderadoService.registrarApoderado(req);

        assertNotNull(response);
        assertEquals("Juan", response.getNombres());
        assertEquals("44556677", response.getNumeroDocumento());
        verify(personaRepository).save(any(Persona.class));
        verify(personaActorRepository).save(any(PersonaActor.class));
    }

    @Test
    @DisplayName("02. Registrar apoderado reutilizando Persona existente sin duplicar registro")
    void registrarApoderado_reutilizaPersonaExistente() {
        ApoderadoCreateRequest req = new ApoderadoCreateRequest();
        req.setTipoDocumento(TipoDocumento.DNI);
        req.setNumeroDocumento("12345678");
        req.setNombres("María Elena");
        req.setApellidoPaterno("Flores");
        req.setApellidoMaterno("Ramos");
        req.setTelefono("987654321");

        when(personaRepository.findByNumeroDocumento("12345678")).thenReturn(Optional.of(apoderado));
        when(personaActorRepository.findByPersonaIdAndTipoActorCodigo(20L, "PADRE_APODERADO"))
                .thenReturn(Optional.empty());
        when(tipoActorRepository.findByCodigo("PADRE_APODERADO")).thenReturn(Optional.of(tipoActorPadre));
        when(estudianteApoderadoRepository.findRelacionesByApoderadoId(20L)).thenReturn(Collections.emptyList());
        when(usuarioRepository.existsByPersonaId(20L)).thenReturn(false);

        ApoderadoResponse response = estudianteApoderadoService.registrarApoderado(req);

        assertNotNull(response);
        assertEquals(20L, response.getId());
        verify(personaActorRepository).save(any(PersonaActor.class));
    }

    @Test
    @DisplayName("03. Asignar apoderado a estudiante requiere que el estudiante tenga perfil ESTUDIANTE")
    void asignarApoderado_fallaSiPersonaNoEsEstudiante() {
        EstudianteApoderadoRequest req = new EstudianteApoderadoRequest(10L, 20L, Parentesco.MADRE, true, LocalDate.now());

        when(personaRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(personaActorRepository.existsByPersonaIdAndTipoActorCodigoAndActivoTrue(10L, "ESTUDIANTE"))
                .thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                estudianteApoderadoService.asignarApoderadoAEstudiante(req)
        );
        assertTrue(ex.getMessage().contains("perfil de ESTUDIANTE"));
    }

    @Test
    @DisplayName("04. Rechazar si el estudiante intenta ser su propio apoderado")
    void asignarApoderado_fallaSiEstudianteEsMismaPersonaQueApoderado() {
        EstudianteApoderadoRequest req = new EstudianteApoderadoRequest(10L, 10L, Parentesco.PADRE, true, LocalDate.now());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                estudianteApoderadoService.asignarApoderadoAEstudiante(req)
        );
        assertTrue(ex.getMessage().contains("propio apoderado"));
    }

    @Test
    @DisplayName("05. Rechazar relación duplicada activa entre el mismo estudiante y apoderado")
    void asignarApoderado_fallaSiRelacionYaExisteActiva() {
        EstudianteApoderadoRequest req = new EstudianteApoderadoRequest(10L, 20L, Parentesco.MADRE, false, LocalDate.now());

        when(personaRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(personaActorRepository.existsByPersonaIdAndTipoActorCodigoAndActivoTrue(10L, "ESTUDIANTE"))
                .thenReturn(true);
        when(personaRepository.findById(20L)).thenReturn(Optional.of(apoderado));
        when(personaActorRepository.findByPersonaIdAndTipoActorCodigo(20L, "PADRE_APODERADO"))
                .thenReturn(Optional.of(new PersonaActor(1L, null, null, null, null, apoderado, tipoActorPadre, LocalDate.now(), null, true)));
        when(estudianteApoderadoRepository.existsByEstudianteIdAndApoderadoIdAndActivoTrue(10L, 20L))
                .thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () ->
                estudianteApoderadoService.asignarApoderadoAEstudiante(req)
        );
        assertTrue(ex.getMessage().contains("ya está relacionado activamente"));
    }

    @Test
    @DisplayName("06. Designar apoderado principal desmarca automáticamente al anterior principal activo")
    void asignarApoderado_desmarcaPrincipalPrevio() {
        EstudianteApoderadoRequest req = new EstudianteApoderadoRequest(10L, 20L, Parentesco.MADRE, true, LocalDate.now());

        Persona otroApoderado = new Persona();
        otroApoderado.setId(21L);
        otroApoderado.setNombres("Padre Previo");

        EstudianteApoderado previoPrincipal = EstudianteApoderado.builder()
                .id(100L)
                .estudiante(estudiante)
                .apoderado(otroApoderado)
                .parentesco(Parentesco.PADRE)
                .esPrincipal(true)
                .activo(true)
                .fechaInicio(LocalDate.now().minusMonths(6))
                .build();

        when(personaRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(personaActorRepository.existsByPersonaIdAndTipoActorCodigoAndActivoTrue(10L, "ESTUDIANTE"))
                .thenReturn(true);
        when(personaRepository.findById(20L)).thenReturn(Optional.of(apoderado));
        when(personaActorRepository.findByPersonaIdAndTipoActorCodigo(20L, "PADRE_APODERADO"))
                .thenReturn(Optional.of(new PersonaActor(1L, null, null, null, null, apoderado, tipoActorPadre, LocalDate.now(), null, true)));
        when(estudianteApoderadoRepository.existsByEstudianteIdAndApoderadoIdAndActivoTrue(10L, 20L))
                .thenReturn(false);
        when(estudianteApoderadoRepository.findByEstudianteIdAndEsPrincipalTrueAndActivoTrue(10L))
                .thenReturn(Optional.of(previoPrincipal));
        when(estudianteApoderadoRepository.findRelacionesByEstudianteId(10L))
                .thenReturn(List.of(previoPrincipal));
        when(estudianteApoderadoRepository.save(any(EstudianteApoderado.class))).thenAnswer(i -> i.getArgument(0));

        EstudianteApoderadoResponse resp = estudianteApoderadoService.asignarApoderadoAEstudiante(req);

        assertNotNull(resp);
        assertTrue(resp.getEsPrincipal());
        assertFalse(previoPrincipal.getEsPrincipal()); // desmarcado!
    }

    @Test
    @DisplayName("07. Desactivar relación marca activo=false y fechaFin sin eliminar la Persona")
    void desactivarRelacion_noEliminaPersona() {
        EstudianteApoderado relacion = EstudianteApoderado.builder()
                .id(50L)
                .estudiante(estudiante)
                .apoderado(apoderado)
                .parentesco(Parentesco.MADRE)
                .esPrincipal(true)
                .activo(true)
                .fechaInicio(LocalDate.now().minusMonths(3))
                .build();

        when(estudianteApoderadoRepository.findById(50L)).thenReturn(Optional.of(relacion));

        estudianteApoderadoService.desactivarRelacion(10L, 50L);

        assertFalse(relacion.getActivo());
        assertNotNull(relacion.getFechaFin());
        assertFalse(relacion.getEsPrincipal());
        verify(estudianteApoderadoRepository).save(relacion);
        verify(personaRepository, never()).delete(any(Persona.class));
    }
}
