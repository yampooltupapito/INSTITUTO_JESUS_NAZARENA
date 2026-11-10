package com.jesusnazareno.asistencia.modules.personal.service;

import com.jesusnazareno.asistencia.exception.BusinessException;
import com.jesusnazareno.asistencia.modules.acceso.repository.UsuarioRepository;
import com.jesusnazareno.asistencia.modules.persona.domain.Persona;
import com.jesusnazareno.asistencia.modules.persona.domain.PersonaActor;
import com.jesusnazareno.asistencia.modules.persona.domain.TipoActor;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.Sexo;
import com.jesusnazareno.asistencia.modules.persona.domain.enums.TipoDocumento;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaRequest;
import com.jesusnazareno.asistencia.modules.persona.dto.PersonaResponse;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaActorRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.PersonaRepository;
import com.jesusnazareno.asistencia.modules.persona.repository.TipoActorRepository;
import com.jesusnazareno.asistencia.modules.persona.service.PersonaService;
import com.jesusnazareno.asistencia.modules.personal.dto.PersonalCreateRequest;
import com.jesusnazareno.asistencia.modules.personal.dto.PersonalResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de Lógica de Negocio - PersonalService")
class PersonalServiceTest {

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private PersonaActorRepository personaActorRepository;

    @Mock
    private TipoActorRepository tipoActorRepository;

    @Mock
    private PersonaService personaService;

    @Mock
    private UsuarioRepository usuarioRepository;

    private PersonalServiceImpl personalService;

    private Persona persona;
    private TipoActor tipoDocente;
    private TipoActor tipoDirector;
    private TipoActor tipoAuxiliar;

    @BeforeEach
    void setUp() {
        personalService = new PersonalServiceImpl(
                personaRepository,
                personaActorRepository,
                tipoActorRepository,
                personaService,
                usuarioRepository
        );

        persona = Persona.builder()
                .id(1L)
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("10203040")
                .nombres("Juan Alberto")
                .apellidoPaterno("Pérez")
                .apellidoMaterno("Quispe")
                .fechaNacimiento(LocalDate.of(1985, 5, 20))
                .sexo(Sexo.MASCULINO)
                .telefono("987654321")
                .correo("jperez@colegio.edu.pe")
                .activo(true)
                .build();

        tipoDocente = TipoActor.builder().id(2L).codigo("DOCENTE").nombre("Docente").activo(true).build();
        tipoDirector = TipoActor.builder().id(1L).codigo("DIRECTOR").nombre("Director").activo(true).build();
        tipoAuxiliar = TipoActor.builder().id(3L).codigo("AUXILIAR").nombre("Auxiliar").activo(true).build();
    }

    @Test
    @DisplayName("5. Crear Persona nueva + actor funciona atómicamente")
    void registrarPersonal_personaNueva_creaPersonaYActor() {
        PersonalCreateRequest req = new PersonalCreateRequest();
        req.setTipoDocumento(TipoDocumento.DNI);
        req.setNumeroDocumento("77889900");
        req.setNombres("Carlos");
        req.setApellidoPaterno("Mendoza");
        req.setApellidoMaterno("Flores");
        req.setFechaNacimiento(LocalDate.of(1988, 10, 10));
        req.setSexo(Sexo.MASCULINO);
        req.setTipoActor("DOCENTE");

        when(personaRepository.findByNumeroDocumento("77889900")).thenReturn(Optional.empty());

        PersonaResponse personaCreada = PersonaResponse.builder()
                .id(2L)
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("77889900")
                .nombres("Carlos")
                .apellidoPaterno("Mendoza")
                .apellidoMaterno("Flores")
                .activo(true)
                .build();
        when(personaService.crearPersona(any(PersonaRequest.class))).thenReturn(personaCreada);

        Persona personaPersistida = Persona.builder().id(2L).numeroDocumento("77889900").nombres("Carlos").apellidoPaterno("Mendoza").apellidoMaterno("Flores").activo(true).build();
        when(personaRepository.findById(2L)).thenReturn(Optional.of(personaPersistida));
        when(tipoActorRepository.findByCodigo("DOCENTE")).thenReturn(Optional.of(tipoDocente));

        PersonalResponse resp = personalService.registrarPersonal(req);

        assertNotNull(resp);
        assertEquals("77889900", resp.getNumeroDocumento());
        verify(personaService).crearPersona(any(PersonaRequest.class));
        verify(personaActorRepository).save(any(PersonaActor.class));
    }

    @Test
    @DisplayName("6. Persona existente reutiliza registro sin crear nueva Persona")
    void registrarPersonal_personaExistente_reutilizaPersonaYAsignaActor() {
        PersonalCreateRequest req = new PersonalCreateRequest();
        req.setTipoDocumento(TipoDocumento.DNI);
        req.setNumeroDocumento("10203040");
        req.setNombres("Juan");
        req.setApellidoPaterno("Pérez");
        req.setApellidoMaterno("Quispe");
        req.setFechaNacimiento(LocalDate.of(1985, 5, 20));
        req.setSexo(Sexo.MASCULINO);
        req.setTipoActor("DIRECTOR");

        when(personaRepository.findByNumeroDocumento("10203040")).thenReturn(Optional.of(persona));
        when(personaActorRepository.findByPersonaIdAndTipoActorCodigo(1L, "DIRECTOR")).thenReturn(Optional.empty());
        when(tipoActorRepository.findByCodigo("DIRECTOR")).thenReturn(Optional.of(tipoDirector));
        when(personaRepository.findById(1L)).thenReturn(Optional.of(persona));

        PersonalResponse resp = personalService.registrarPersonal(req);

        assertNotNull(resp);
        assertEquals(1L, resp.getId());
        verify(personaService, never()).crearPersona(any());
        verify(personaActorRepository).save(any(PersonaActor.class));
    }

    @Test
    @DisplayName("8. No se duplica PersonaActor si la persona ya tiene esa función activa")
    void registrarPersonal_actorYaActivo_lanzaBusinessException() {
        PersonalCreateRequest req = new PersonalCreateRequest();
        req.setNumeroDocumento("10203040");
        req.setTipoActor("DOCENTE");

        PersonaActor actorExistente = PersonaActor.builder().id(10L).persona(persona).tipoActor(tipoDocente).activo(true).build();
        when(personaRepository.findByNumeroDocumento("10203040")).thenReturn(Optional.of(persona));
        when(personaActorRepository.findByPersonaIdAndTipoActorCodigo(1L, "DOCENTE")).thenReturn(Optional.of(actorExistente));

        assertThrows(BusinessException.class, () -> personalService.registrarPersonal(req));
        verify(personaActorRepository, never()).save(any());
    }

    @Test
    @DisplayName("9, 10, 11. DIRECTOR, DOCENTE y AUXILIAR son tipos de personal válidos")
    void tiposPersonalPermitidos_incluyenDirectorDocenteYAuxiliar() {
        List<String> permitidos = personalService.obtenerTiposPersonalPermitidos();
        assertTrue(permitidos.contains("DIRECTOR"));
        assertTrue(permitidos.contains("DOCENTE"));
        assertTrue(permitidos.contains("AUXILIAR"));
        assertFalse(permitidos.contains("ESTUDIANTE"));
        assertFalse(permitidos.contains("PADRE_APODERADO"));
    }

    @Test
    @DisplayName("12. Desactivar DOCENTE no elimina el registro físico de Persona ni de PersonaActor")
    void desactivarPersonal_noEliminaFisicamente() {
        PersonaActor actorDocente = PersonaActor.builder().id(10L).persona(persona).tipoActor(tipoDocente).activo(true).build();
        when(personaRepository.findById(1L)).thenReturn(Optional.of(persona));
        when(personaActorRepository.findByPersonaIdAndTipoActorCodigo(1L, "DOCENTE")).thenReturn(Optional.of(actorDocente));
        when(personaActorRepository.countByPersonaIdAndActivoTrue(1L)).thenReturn(0L);

        personalService.desactivarPersonal(1L, "DOCENTE");

        assertFalse(actorDocente.getActivo());
        assertNotNull(actorDocente.getFechaFin());
        verify(personaActorRepository).save(actorDocente);
        verify(personaRepository, never()).delete(any());
        verify(personaRepository, never()).deleteById(any());
        verify(personaActorRepository, never()).delete(any());
        verify(personaActorRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("13. Desactivar DOCENTE preserva otro actor activo (ej. PADRE_APODERADO)")
    void desactivarPersonal_conOtroActorActivo_mantienePersonaActiva() {
        PersonaActor actorDocente = PersonaActor.builder().id(10L).persona(persona).tipoActor(tipoDocente).activo(true).build();
        when(personaRepository.findById(1L)).thenReturn(Optional.of(persona));
        when(personaActorRepository.findByPersonaIdAndTipoActorCodigo(1L, "DOCENTE")).thenReturn(Optional.of(actorDocente));
        // Aún le queda 1 actor activo (por ejemplo, PADRE_APODERADO)
        when(personaActorRepository.countByPersonaIdAndActivoTrue(1L)).thenReturn(1L);

        personalService.desactivarPersonal(1L, "DOCENTE");

        assertFalse(actorDocente.getActivo());
        // Persona no se desactiva porque aún tiene otra función activa
        assertTrue(persona.getActivo());
    }

    @Test
    @DisplayName("14. Activación funciona reactivando relación existente sin duplicar registro")
    void activarPersonal_reactivaRelacionSinDuplicar() {
        PersonaActor actorDocente = PersonaActor.builder().id(10L).persona(persona).tipoActor(tipoDocente).activo(false).fechaFin(LocalDate.now()).build();
        when(personaRepository.findById(1L)).thenReturn(Optional.of(persona));
        when(personaActorRepository.findByPersonaIdAndTipoActorCodigo(1L, "DOCENTE")).thenReturn(Optional.of(actorDocente));

        personalService.activarPersonal(1L, "DOCENTE");

        assertTrue(actorDocente.getActivo());
        assertEquals(null, actorDocente.getFechaFin());
        assertTrue(persona.getActivo());
        verify(personaActorRepository).save(actorDocente);
    }
}
