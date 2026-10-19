package com.lancenter.lancenterhd.controller;

import com.lancenter.lancenterhd.service.MaquinaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DisponibilidadController {

    private final MaquinaService maquinaService;

    public DisponibilidadController(MaquinaService maquinaService) {
        this.maquinaService = maquinaService;
    }

    @GetMapping("/cliente/disponibilidad")
    public String disponibilidad(
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) String horaInicio,
            Model model) {
        model.addAttribute("maquinas", maquinaService.listarTodas());
        model.addAttribute("fechaSeleccionada", fecha);
        model.addAttribute("horaInicioSeleccionada", horaInicio);
        return "cliente/disponibilidad";
    }
}