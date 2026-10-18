package com.lancenter.lancenterhd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Pagina de inicio de la aplicacion.
 *
 * Sin esta ruta, AuthController redirige a "/" despues de un login correcto
 * y el usuario recibe un error 404 justo cuando consigue entrar.
 */
@Controller
public class InicioController {

    @GetMapping("/")
    public String inicio() {
        return "inicio";
    }
}
