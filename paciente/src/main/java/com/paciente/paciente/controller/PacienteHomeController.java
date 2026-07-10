package com.paciente.paciente.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PacienteHomeController {

    @GetMapping({ "/", "/home", "/pacientes/home", "/pacientes/portal" })
    public String home() {
        return "forward:/index.html";
    }

    @GetMapping("/pacientes/styles.css")
    public String styles() {
        return "forward:/styles.css";
    }

    @GetMapping("/pacientes/app.js")
    public String appScript() {
        return "forward:/app.js";
    }
}
