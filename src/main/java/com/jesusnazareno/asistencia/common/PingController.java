package com.jesusnazareno.asistencia.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador temporal de prueba para verificar el funcionamiento inicial del servidor.
 */
@RestController
public class PingController {

    @GetMapping("/ping")
    public String ping() {
        return "SGE OK";
    }
}
