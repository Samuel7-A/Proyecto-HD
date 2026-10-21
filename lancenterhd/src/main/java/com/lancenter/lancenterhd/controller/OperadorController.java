package com.lancenter.lancenterhd.controller;

import com.lancenter.lancenterhd.enums.EstadoReserva;
import com.lancenter.lancenterhd.model.Maquina;
import com.lancenter.lancenterhd.model.Reserva;
import com.lancenter.lancenterhd.model.Usuario;
import com.lancenter.lancenterhd.repository.MaquinaRepository;
import com.lancenter.lancenterhd.repository.ReservaRepository;
import com.lancenter.lancenterhd.repository.UsuarioRepository;
import com.lancenter.lancenterhd.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/operador")
public class OperadorController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final MaquinaRepository maquinaRepository;

    public OperadorController(UsuarioService usuarioService,
                              UsuarioRepository usuarioRepository,
                              ReservaRepository reservaRepository,
                              MaquinaRepository maquinaRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
        this.maquinaRepository = maquinaRepository;
    }

    @GetMapping("/consola")
    public String mostrarConsola(@RequestParam(required = false) Long operadorId, Model modelo) {
        modelo.addAttribute("reservas", reservaRepository.findAll());
        modelo.addAttribute("nombresClientes", nombresDeClientes());
        modelo.addAttribute("codigosMaquinas", codigosDeMaquinas());
        modelo.addAttribute("operadorId", operadorId);
        return "operador/consola";
    }

    /** id de usuario -> nombre, para no mostrar identificadores en la consola. */
    private Map<Long, String> nombresDeClientes() {
        return usuarioRepository.findAll().stream()
                .collect(Collectors.toMap(Usuario::getId, Usuario::getNombre));
    }

    /** id de maquina -> codigo, para que el operador reconozca el equipo. */
    private Map<Long, String> codigosDeMaquinas() {
        return maquinaRepository.findAll().stream()
                .collect(Collectors.toMap(Maquina::getId, Maquina::getCodigo));
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

    /**
     * El operador confirma que el cliente pago en el mostrador.
     * Solo aplica a una reserva que este pendiente de pago.
     */
    @PostMapping("/consola/validar-pago")
    public String validarPago(@RequestParam Long reservaId, RedirectAttributes redirectAttributes) {
        cambiarEstado(reservaId,
                EstadoReserva.PENDIENTE_PAGO,
                EstadoReserva.PAGADO,
                "Pago validado: la reserva quedo como PAGADO.",
                "Solo se puede validar el pago de una reserva pendiente de pago.",
                redirectAttributes);
        return "redirect:/operador/consola";
    }

    /**
     * El cliente no llego dentro del tiempo de gracia. La reserva vence y la
     * maquina vuelve a quedar libre.
     */
    @PostMapping("/consola/no-show")
    public String marcarNoShow(@RequestParam Long reservaId, RedirectAttributes redirectAttributes) {
        cambiarEstado(reservaId,
                EstadoReserva.PENDIENTE_PAGO,
                EstadoReserva.VENCIDA,
                "Reserva marcada como VENCIDA por inasistencia.",
                "Solo se puede marcar inasistencia de una reserva pendiente de pago.",
                redirectAttributes);
        return "redirect:/operador/consola";
    }

    /**
     * Mueve una reserva de un estado a otro, siempre que su estado actual sea
     * el esperado. Deja el resultado como mensaje para la consola.
     */
    private void cambiarEstado(Long reservaId,
                               EstadoReserva estadoEsperado,
                               EstadoReserva estadoNuevo,
                               String mensajeExito,
                               String mensajeError,
                               RedirectAttributes redirectAttributes) {

        Optional<Reserva> encontrada = reservaRepository.findById(reservaId);
        if (encontrada.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "No existe la reserva indicada.");
            return;
        }

        Reserva reserva = encontrada.get();
        if (reserva.getEstado() != estadoEsperado) {
            redirectAttributes.addFlashAttribute("error", mensajeError);
            return;
        }

        reserva.setEstado(estadoNuevo);
        reservaRepository.save(reserva);
        redirectAttributes.addFlashAttribute("mensaje", mensajeExito);
    }
}
