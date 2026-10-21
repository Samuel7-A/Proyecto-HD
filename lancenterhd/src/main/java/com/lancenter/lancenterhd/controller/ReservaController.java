package com.lancenter.lancenterhd.controller;

import com.lancenter.lancenterhd.enums.Canal;
import com.lancenter.lancenterhd.enums.EstadoReserva;
import com.lancenter.lancenterhd.model.Maquina;
import com.lancenter.lancenterhd.model.Reserva;
import com.lancenter.lancenterhd.model.Usuario;
import com.lancenter.lancenterhd.repository.MaquinaRepository;
import com.lancenter.lancenterhd.repository.ReservaRepository;
import com.lancenter.lancenterhd.repository.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;
    private final MaquinaRepository maquinaRepository;

    public ReservaController(ReservaRepository reservaRepository,
                             UsuarioRepository usuarioRepository,
                             MaquinaRepository maquinaRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.maquinaRepository = maquinaRepository;
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
                                   @RequestParam String celular,
                                   @RequestParam String fecha,
                                   @RequestParam String horaInicio,
                                   @RequestParam String horaFin,
                                   RedirectAttributes redireccion) {

        Optional<Usuario> usuario = usuarioRepository.findByCelular(celular);
        if (usuario.isEmpty()) {
            redireccion.addFlashAttribute("error",
                    "Ese celular no esta registrado. Registrate primero para poder reservar.");
            return "redirect:/cliente/disponibilidad";
        }

        Reserva reserva = new Reserva();
        reserva.setUsuarioId(usuario.get().getId());
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
        return "redirect:/cliente/historial?celular=" + celular;
    }

    /** Historial de reservas del cliente (permite filtrar por celular). */
    @GetMapping("/cliente/historial")
    public String verHistorial(@RequestParam(required = false) String celular, Model modelo) {
        List<Reserva> reservas;
        if (celular != null && !celular.isBlank()) {
            Optional<Usuario> usuario = usuarioRepository.findByCelular(celular);
            if (usuario.isPresent()) {
                reservas = reservaRepository.findAll().stream()
                        .filter(r -> r.getUsuarioId() != null && r.getUsuarioId().equals(usuario.get().getId()))
                        .toList();
                modelo.addAttribute("usuario", usuario.get());
            } else {
                reservas = List.of();
                modelo.addAttribute("error", "No se encontró ningún usuario registrado con el celular: " + celular);
            }
        } else {
            reservas = reservaRepository.findAll();
        }

        modelo.addAttribute("reservas", reservas);
        modelo.addAttribute("celularBuscado", celular);

        // Mapa de maquinaId a codigo (M-01, M-02, etc.) para facil visualizacion
        Map<Long, String> maquinasMap = maquinaRepository.findAll().stream()
                .collect(Collectors.toMap(Maquina::getId, Maquina::getCodigo, (a, b) -> a));
        modelo.addAttribute("maquinasMap", maquinasMap);

        return "cliente/historial";
    }
}
