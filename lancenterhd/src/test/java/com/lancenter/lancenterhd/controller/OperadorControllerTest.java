package com.lancenter.lancenterhd.controller;

import com.lancenter.lancenterhd.enums.EstadoReserva;
import com.lancenter.lancenterhd.model.Reserva;
import com.lancenter.lancenterhd.model.Usuario;
import com.lancenter.lancenterhd.repository.MaquinaRepository;
import com.lancenter.lancenterhd.repository.ReservaRepository;
import com.lancenter.lancenterhd.repository.UsuarioRepository;
import com.lancenter.lancenterhd.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OperadorControllerTest {

    private final UsuarioService usuarioService = mock(UsuarioService.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final ReservaRepository reservaRepository = mock(ReservaRepository.class);
    private final MaquinaRepository maquinaRepository = mock(MaquinaRepository.class);
    private final OperadorController controller =
            new OperadorController(usuarioService, usuarioRepository, reservaRepository, maquinaRepository);

    @Test
    void mostrarConsolaPasaLasReservasYTraduceLosIdentificadores() {
        Model modelo = mock(Model.class);
        List<Reserva> reservas = List.of(new Reserva());
        when(reservaRepository.findAll()).thenReturn(reservas);
        when(usuarioRepository.findAll()).thenReturn(List.of());
        when(maquinaRepository.findAll()).thenReturn(List.of());

        assertEquals("operador/consola", controller.mostrarConsola(12L, modelo));

        verify(modelo).addAttribute("reservas", reservas);
        verify(modelo).addAttribute(eq("nombresClientes"), any());
        verify(modelo).addAttribute(eq("codigosMaquinas"), any());
        verify(modelo).addAttribute("operadorId", 12L);
    }

    @Test
    void validarPagoPasaUnaReservaPendienteAPagado() {
        Reserva reserva = new Reserva();
        reserva.setEstado(EstadoReserva.PENDIENTE_PAGO);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        RedirectAttributes atributos = mock(RedirectAttributes.class);

        assertEquals("redirect:/operador/consola", controller.validarPago(1L, atributos));

        assertEquals(EstadoReserva.PAGADO, reserva.getEstado());
        verify(reservaRepository).save(reserva);
    }

    @Test
    void noShowMarcaComoVencidaUnaReservaPendiente() {
        Reserva reserva = new Reserva();
        reserva.setEstado(EstadoReserva.PENDIENTE_PAGO);
        when(reservaRepository.findById(2L)).thenReturn(Optional.of(reserva));
        RedirectAttributes atributos = mock(RedirectAttributes.class);

        assertEquals("redirect:/operador/consola", controller.marcarNoShow(2L, atributos));

        assertEquals(EstadoReserva.VENCIDA, reserva.getEstado());
        verify(reservaRepository).save(reserva);
    }

    @Test
    void noSePuedeValidarElPagoDeUnaReservaYaPagada() {
        Reserva reserva = new Reserva();
        reserva.setEstado(EstadoReserva.PAGADO);
        when(reservaRepository.findById(3L)).thenReturn(Optional.of(reserva));
        RedirectAttributes atributos = mock(RedirectAttributes.class);

        assertEquals("redirect:/operador/consola", controller.validarPago(3L, atributos));

        assertEquals(EstadoReserva.PAGADO, reserva.getEstado());
        verify(reservaRepository, never()).save(reserva);
    }

    @Test
    void registrarPresencialConstruyeElUsuarioConElServicioYDespuesLoGuarda() {
        Usuario usuario = new Usuario();
        when(usuarioService.registrarPresencial("Ana", "987654321", 12L)).thenReturn(usuario);

        assertEquals("redirect:/operador/consola?operadorId=12",
                controller.registrarPresencial("Ana", "987654321", 12L));

        var orden = inOrder(usuarioService, usuarioRepository);
        orden.verify(usuarioService).registrarPresencial("Ana", "987654321", 12L);
        orden.verify(usuarioRepository).save(usuario);
    }
}
