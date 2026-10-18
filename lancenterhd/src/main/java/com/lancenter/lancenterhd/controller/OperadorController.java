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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/operador")
public class OperadorController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public OperadorController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/consola")
    public String mostrarConsola(@RequestParam(required = false) Long operadorId, Model modelo) {
        modelo.addAttribute("reservas", List.of());
        modelo.addAttribute("operadorId", operadorId);
        return "operador/consola";
    }

    @PostMapping("/consola/registrar")
    public String registrarPresencial(@RequestParam String nombre,
                                      @RequestParam String celular,
                                      @RequestParam(required = false) Long operadorId) {
        Usuario usuario = usuarioService.registrarPresencial(nombre, celular, operadorId);
        usuarioRepository.save(usuario);
        return operadorId == null
                ? "redirect:/operador/consola"
                : "redirect:/operador/consola?operadorId=" + operadorId;
    }

    @PostMapping("/consola/validar-pago")
    public String validarPago(@RequestParam Long reservaId, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(
                "error", "No se pudo validar el pago: todavía no hay un repositorio de reservas."
        );
        return "redirect:/operador/consola";
    }

    @PostMapping("/consola/no-show")
    public String marcarNoShow(@RequestParam Long reservaId, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(
                "error", "No se pudo marcar la reserva como no-show: todavía no hay un repositorio de reservas."
        );
        return "redirect:/operador/consola";
    }
}
