package com.jesusnazareno.asistencia.modules.acceso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/inicio")
    public String inicio() {
        return "redirect:/dashboard";
    }
}
