package com.lancenter.lancenterhd.controller;

import com.lancenter.lancenterhd.enums.Canal;
import com.lancenter.lancenterhd.enums.EstadoReserva;
import com.lancenter.lancenterhd.model.Maquina;
import com.lancenter.lancenterhd.model.Reserva;
import com.lancenter.lancenterhd.model.Usuario;
import com.lancenter.lancenterhd.repository.MaquinaRepository;
import com.lancenter.lancenterhd.repository.ReservaRepository;
import com.lancenter.lancenterhd.security.SesionUsuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Flujo de reserva del cliente.
 *
 * El cliente llega desde la vista de disponibilidad con la maquina elegida,
 * completa fecha y horario, y se crea la reserva en estado PENDIENTE_PAGO.
 * El operador la confirma despues en el local, igual que en el canal
 * presencial.
 */
@Controller
public class ReservaController {

    private static final BigDecimal COSTO_POR_HORA = new BigDecimal("5.00");

    private final ReservaRepository reservaRepository;
    private final MaquinaRepository maquinaRepository;
    private final SesionUsuario sesionUsuario;

    public ReservaController(ReservaRepository reservaRepository,
                             MaquinaRepository maquinaRepository,
                             SesionUsuario sesionUsuario) {
        this.reservaRepository = reservaRepository;
        this.maquinaRepository = maquinaRepository;
        this.sesionUsuario = sesionUsuario;
    }

    /** Muestra el formulario de reserva para la maquina elegida. */
    @GetMapping("/cliente/reservar")
    public String mostrarFormulario(@RequestParam Long maquinaId,
                                    Model modelo,
                                    RedirectAttributes redireccion) {
        Optional<Maquina> maquina = maquinaRepository.findById(maquinaId);
        if (maquina.isEmpty()) {
            redireccion.addFlashAttribute("error", "La maquina indicada no existe.");
            return "redirect:/cliente/disponibilidad";
        }
        modelo.addAttribute("maquina", maquina.get());
        return "cliente/reservar";
    }

    /** Crea la reserva y vuelve a la disponibilidad con mensaje. */
    @PostMapping("/cliente/reservar")
    public String registrarReserva(@RequestParam Long maquinaId,
                                   @RequestParam String fecha,
                                   @RequestParam String horaInicio,
                                   @RequestParam String horaFin,
                                   RedirectAttributes redireccion) {

        Usuario usuario = sesionUsuario.usuarioActual().orElse(null);
        if (usuario == null) {
            redireccion.addFlashAttribute("error",
                    "Debes iniciar sesion como cliente para poder reservar.");
            return "redirect:/cliente/disponibilidad";
        }

        Reserva reserva = new Reserva();
        reserva.setUsuarioId(usuario.getId());
        reserva.setMaquinaId(maquinaId);
        reserva.setFecha(LocalDate.parse(fecha));
        reserva.setHoraInicio(LocalTime.parse(horaInicio));
        reserva.setHoraFin(LocalTime.parse(horaFin));
        reserva.setEstado(EstadoReserva.PENDIENTE_PAGO);
        reserva.setCanal(Canal.WEB);
        reserva.setCosto(COSTO_POR_HORA);

        reservaRepository.save(reserva);

        redireccion.addFlashAttribute("mensaje",
                "Reserva registrada exitosamente. Pasa al local para validar el pago.");
        return "redirect:/cliente/historial";
    }

    /** Historial de reservas del cliente (permite filtrar por celular). */
    @GetMapping("/cliente/historial")
    public String verHistorial(Model modelo) {
        Usuario usuario = sesionUsuario.usuarioActual().orElse(null);

        List<Reserva> reservas;
        if (usuario != null) {
            reservas = reservaRepository.findAll().stream()
                    .filter(r -> r.getUsuarioId() != null && r.getUsuarioId().equals(usuario.getId()))
                    .toList();
            modelo.addAttribute("usuario", usuario);
        } else {
            reservas = List.of();
        }

        modelo.addAttribute("reservas", reservas);

        // Mapa de maquinaId a codigo (M-01, M-02, etc.) para facil visualizacion
        Map<Long, String> maquinasMap = maquinaRepository.findAll().stream()
                .collect(Collectors.toMap(Maquina::getId, Maquina::getCodigo, (a, b) -> a));
        modelo.addAttribute("maquinasMap", maquinasMap);

        return "cliente/historial";
    }
}
