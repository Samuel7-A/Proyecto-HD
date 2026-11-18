
package com.lancenter.lancenterhd.controller;

import com.lancenter.lancenterhd.model.Reserva;
import com.lancenter.lancenterhd.model.Usuario;
import com.lancenter.lancenterhd.security.SesionUsuario;
import com.lancenter.lancenterhd.service.OperadorService;
import com.lancenter.lancenterhd.service.OperadorService.ResultadoCambioEstado;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OperadorControllerTest {

    private final OperadorService operadorService = mock(OperadorService.class);
    private final SesionUsuario sesionUsuario = mock(SesionUsuario.class);

    private final OperadorController controller =
            new OperadorController(operadorService, sesionUsuario);

    @Test
    void mostrarConsolaPasaLasReservasYTraduceLosIdentificadores() {
        Model modelo = mock(Model.class);

        List<Reserva> reservas = List.of(new Reserva());

        when(operadorService.obtenerReservas()).thenReturn(reservas);
        when(operadorService.obtenerNombresClientes()).thenReturn(Map.of());
        when(operadorService.obtenerCodigosMaquinas()).thenReturn(Map.of());

        assertEquals("operador/consola", controller.mostrarConsola(modelo));

        verify(modelo).addAttribute("reservas", reservas);
        verify(modelo).addAttribute(eq("nombresClientes"), eq(Map.of()));
        verify(modelo).addAttribute(eq("codigosMaquinas"), eq(Map.of()));
    }

    @Test
    void validarPagoPasaUnaReservaPendienteAPagado() {
        RedirectAttributes atributos = mock(RedirectAttributes.class);

        when(operadorService.validarPago(1L))
                .thenReturn(ResultadoCambioEstado.ACTUALIZADO);

        assertEquals(
                "redirect:/operador/consola",
                controller.validarPago(1L, atributos)
        );

        verify(operadorService).validarPago(1L);
        verify(atributos).addFlashAttribute(
                "mensaje",
                "Pago validado: la reserva quedo como PAGADO."
        );
    }

    @Test
    void noShowMarcaComoVencidaUnaReservaPendiente() {
        RedirectAttributes atributos = mock(RedirectAttributes.class);

        when(operadorService.marcarNoShow(2L))
                .thenReturn(ResultadoCambioEstado.ACTUALIZADO);

        assertEquals(
                "redirect:/operador/consola",
                controller.marcarNoShow(2L, atributos)
        );

        verify(operadorService).marcarNoShow(2L);
        verify(atributos).addFlashAttribute(
                "mensaje",
                "Reserva marcada como VENCIDA por inasistencia."
        );
    }

    @Test
    void noSePuedeValidarElPagoDeUnaReservaYaPagada() {
        RedirectAttributes atributos = mock(RedirectAttributes.class);

        when(operadorService.validarPago(3L))
                .thenReturn(ResultadoCambioEstado.ESTADO_NO_PERMITIDO);

        assertEquals(
                "redirect:/operador/consola",
                controller.validarPago(3L, atributos)
        );

        verify(operadorService).validarPago(3L);
        verify(atributos).addFlashAttribute(
                "error",
                "Solo se puede validar el pago de una reserva pendiente de pago."
        );
    }

    @Test
    void registrarPresencialUsaElOperadorDeLaSesion() {
        Usuario operador = new Usuario();
        operador.setId(12L);

        when(sesionUsuario.usuarioActual()).thenReturn(Optional.of(operador));

        assertEquals(
                "redirect:/operador/consola",
                controller.registrarPresencial("Ana", "987654321")
        );

        verify(operadorService).registrarPresencial(
                "Ana", "987654321", 12L
        );
    }
}
