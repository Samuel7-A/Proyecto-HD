package com.lancenter.lancenterhd.controller;

import com.lancenter.lancenterhd.model.Usuario;
import com.lancenter.lancenterhd.repository.UsuarioRepository;
import com.lancenter.lancenterhd.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Registro e inicio de sesion de clientes.
 * El operador no usa estas rutas: el crea al cliente presencial desde su consola.
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro() {
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(@RequestParam String nombre,
                            @RequestParam String email,
                            @RequestParam String celular,
                            @RequestParam String clave,
                            Model modelo) {
        if (usuarioRepository.findByEmail(email).isPresent()) {
            modelo.addAttribute("error", "Ya existe una cuenta registrada con ese correo.");
            return "auth/registro";
        }
        if (usuarioRepository.findByCelular(celular).isPresent()) {
            modelo.addAttribute("error", "Ese celular ya tiene una cuenta. Pide tu contrasena en el local.");
            return "auth/registro";
        }

        Usuario usuario = usuarioService.registrar(nombre, email, celular, clave);
        usuarioRepository.save(usuario);
        return "redirect:/auth/login?registrado=true";
    }

    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String ingresar(@RequestParam String credencial,
                        @RequestParam String clave,
                        Model modelo) {

        Usuario usuario = usuarioRepository.findByEmail(credencial)
                .orElseGet(() -> usuarioRepository.findByCelular(credencial).orElse(null));

        if (usuario != null && usuarioService.claveCorrecta(usuario, clave)) {
            return "redirect:/";
        }

        modelo.addAttribute("error", "La credencial o la contrasena no son correctas.");
        return "auth/login";
    }

}
