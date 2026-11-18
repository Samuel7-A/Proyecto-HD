
package com.lancenter.lancenterhd.controller;

import com.lancenter.lancenterhd.model.Usuario;
import com.lancenter.lancenterhd.security.SesionUsuario;
import com.lancenter.lancenterhd.service.OperadorService;
import com.lancenter.lancenterhd.service.OperadorService.ResultadoCambioEstado;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/operador")
public class OperadorController {

    private final OperadorService operadorService;
    private final SesionUsuario sesionUsuario;

    public OperadorController(OperadorService operadorService,
                              SesionUsuario sesionUsuario) {
        this.operadorService = operadorService;
        this.sesionUsuario = sesionUsuario;
    }

    @GetMapping("/consola")
    public String mostrarConsola(Model modelo) {
        modelo.addAttribute("reservas", operadorService.obtenerReservas());
        modelo.addAttribute("nombresClientes", operadorService.obtenerNombresClientes());
        modelo.addAttribute("codigosMaquinas", operadorService.obtenerCodigosMaquinas());
        return "operador/consola";
    }

    @PostMapping("/consola/registrar")
    public String registrarPresencial(@RequestParam String nombre,
                                      @RequestParam String celular) {
        Long operadorId = sesionUsuario.usuarioActual()
                .map(Usuario::getId)
                .orElse(null);

        operadorService.registrarPresencial(nombre, celular, operadorId);
        return "redirect:/operador/consola";
    }

    @PostMapping("/consola/validar-pago")
    public String validarPago(@RequestParam Long reservaId,
                              RedirectAttributes redirectAttributes) {
        ResultadoCambioEstado resultado = operadorService.validarPago(reservaId);

        if (resultado == ResultadoCambioEstado.ACTUALIZADO) {
            redirectAttributes.addFlashAttribute(
                    "mensaje", "Pago validado: la reserva quedo como PAGADO."
            );
        } else if (resultado == ResultadoCambioEstado.NO_ENCONTRADA) {
            redirectAttributes.addFlashAttribute(
                    "error", "No existe la reserva indicada."
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "error", "Solo se puede validar el pago de una reserva pendiente de pago."
            );
        }

        return "redirect:/operador/consola";
    }

    @PostMapping("/consola/no-show")
    public String marcarNoShow(@RequestParam Long reservaId,
                               RedirectAttributes redirectAttributes) {
        ResultadoCambioEstado resultado = operadorService.marcarNoShow(reservaId);

        if (resultado == ResultadoCambioEstado.ACTUALIZADO) {
            redirectAttributes.addFlashAttribute(
                    "mensaje", "Reserva marcada como VENCIDA por inasistencia."
            );
        } else if (resultado == ResultadoCambioEstado.NO_ENCONTRADA) {
            redirectAttributes.addFlashAttribute(
                    "error", "No existe la reserva indicada."
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "error", "Solo se puede marcar inasistencia de una reserva pendiente de pago."
            );
        }

        return "redirect:/operador/consola";
    }
}
