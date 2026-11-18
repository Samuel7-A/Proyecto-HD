package com.lancenter.lancenterhd.controller;

import com.lancenter.lancenterhd.enums.EstadoMaquina;
import com.lancenter.lancenterhd.enums.EstadoReserva;
import com.lancenter.lancenterhd.enums.Rol;
import com.lancenter.lancenterhd.model.Maquina;
import com.lancenter.lancenterhd.model.Reserva;
import com.lancenter.lancenterhd.model.Usuario;
import com.lancenter.lancenterhd.repository.MaquinaRepository;
import com.lancenter.lancenterhd.repository.ReservaRepository;
import com.lancenter.lancenterhd.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Panel de administracion.
 *
 * Da al administrador la vision global del local para tomar decisiones: cuantas
 * maquinas hay y como estan, cuantas reservas hay por estado, cuantos clientes
 * estan registrados y cuanto se ha cobrado.
 *
 * Es de solo lectura a proposito: las operaciones del dia (validar un pago,
 * marcar un no-show) las hace el operador desde su consola, no el administrador.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MaquinaRepository maquinaRepository;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;

    public AdminController(MaquinaRepository maquinaRepository,
                           ReservaRepository reservaRepository,
                           UsuarioRepository usuarioRepository) {
        this.maquinaRepository = maquinaRepository;
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/panel")
    public String mostrarPanel(Model modelo) {
        List<Maquina> maquinas = maquinaRepository.findAll();
        List<Reserva> reservas = reservaRepository.findAll();
        List<Usuario> usuarios = usuarioRepository.findAll();

        // Ocupacion del local.
        modelo.addAttribute("totalMaquinas", maquinas.size());
        modelo.addAttribute("maquinasDisponibles", contarMaquinas(maquinas, EstadoMaquina.DISPONIBLE));
        modelo.addAttribute("maquinasOcupadas", contarMaquinas(maquinas, EstadoMaquina.OCUPADA));
        modelo.addAttribute("maquinasMantenimiento", contarMaquinas(maquinas, EstadoMaquina.MANTENIMIENTO));

        // Estado de las reservas.
        modelo.addAttribute("totalReservas", reservas.size());
        modelo.addAttribute("reservasPendientes", contarReservas(reservas, EstadoReserva.PENDIENTE_PAGO));
        modelo.addAttribute("reservasPagadas", contarReservas(reservas, EstadoReserva.PAGADO));
        modelo.addAttribute("reservasCompletadas", contarReservas(reservas, EstadoReserva.COMPLETADA));
        modelo.addAttribute("reservasVencidas", contarReservas(reservas, EstadoReserva.VENCIDA));
        modelo.addAttribute("reservasCanceladas", contarReservas(reservas, EstadoReserva.CANCELADA));
        modelo.addAttribute("reservasHoy", reservas.stream()
                .filter(reserva -> LocalDate.now().equals(reserva.getFecha()))
                .count());

        // Usuarios registrados.
        modelo.addAttribute("totalUsuarios", usuarios.size());
        modelo.addAttribute("totalClientes", usuarios.stream()
                .filter(usuario -> Rol.CLIENTE.equals(usuario.getRol()))
                .count());

        // Dinero cobrado.
        modelo.addAttribute("ingresos", ingresos(reservas));

        // Traduccion de identificadores para la tabla de detalle.
        modelo.addAttribute("reservas", reservas);
        modelo.addAttribute("nombresClientes", porId(usuarios, Usuario::getId, Usuario::getNombre));
        modelo.addAttribute("codigosMaquinas", porId(maquinas, Maquina::getId, Maquina::getCodigo));
        modelo.addAttribute("coloresEstado", coloresDeEstado());

        return "admin/panel";
    }

    /** Color de Bootstrap para cada estado, para no decidirlo en la vista. */
    private Map<EstadoReserva, String> coloresDeEstado() {
        return Map.of(
                EstadoReserva.PENDIENTE_PAGO, "warning",
                EstadoReserva.PAGADO, "success",
                EstadoReserva.COMPLETADA, "secondary",
                EstadoReserva.VENCIDA, "danger",
                EstadoReserva.CANCELADA, "dark");
    }

    private long contarMaquinas(List<Maquina> maquinas, EstadoMaquina estado) {
        return maquinas.stream()
                .filter(maquina -> estado.equals(maquina.getEstado()))
                .count();
    }

    private long contarReservas(List<Reserva> reservas, EstadoReserva estado) {
        return reservas.stream()
                .filter(reserva -> estado.equals(reserva.getEstado()))
                .count();
    }

    /** Suma el costo de las reservas ya cobradas (pagadas o completadas). */
    private BigDecimal ingresos(List<Reserva> reservas) {
        return reservas.stream()
                .filter(reserva -> EstadoReserva.PAGADO.equals(reserva.getEstado())
                        || EstadoReserva.COMPLETADA.equals(reserva.getEstado()))
                .map(Reserva::getCosto)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private <T> Map<Long, String> porId(List<T> elementos,
                                        Function<T, Long> id,
                                        Function<T, String> etiqueta) {
        return elementos.stream().collect(Collectors.toMap(id, etiqueta));
    }
}
