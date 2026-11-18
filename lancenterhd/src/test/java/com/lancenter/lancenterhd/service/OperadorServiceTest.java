
package com.lancenter.lancenterhd.service;

import com.lancenter.lancenterhd.enums.EstadoReserva;
import com.lancenter.lancenterhd.model.Maquina;
import com.lancenter.lancenterhd.model.Reserva;
import com.lancenter.lancenterhd.model.Usuario;
import com.lancenter.lancenterhd.repository.MaquinaRepository;
import com.lancenter.lancenterhd.repository.ReservaRepository;
import com.lancenter.lancenterhd.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OperadorServiceTest {

    private UsuarioService usuarioService;
    private UsuarioRepository usuarioRepository;
    private ReservaRepository reservaRepository;
    private MaquinaRepository maquinaRepository;
    private OperadorService operadorService;

    @BeforeEach
    void setUp() {
        usuarioService = mock(UsuarioService.class);
        usuarioRepository = mock(UsuarioRepository.class);
        reservaRepository = mock(ReservaRepository.class);
        maquinaRepository = mock(MaquinaRepository.class);

        operadorService = new OperadorService(
                usuarioService,
                usuarioRepository,
                reservaRepository,
                maquinaRepository
        );
    }

    @Test
    void obtenerNombresClientesDevuelveMapaDeIdentificadores() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Ana");

        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        assertEquals(
                "Ana",
                operadorService.obtenerNombresClientes().get(1L)
        );
    }

    @Test
    void obtenerCodigosMaquinasDevuelveMapaDeIdentificadores() {
        Maquina maquina = new Maquina();
        maquina.setId(2L);
        maquina.setCodigo("PC-02");

        when(maquinaRepository.findAll()).thenReturn(List.of(maquina));

        assertEquals(
                "PC-02",
                operadorService.obtenerCodigosMaquinas().get(2L)
        );
    }

    @Test
    void obtenerReservasConsultaElRepositorio() {
        List<Reserva> reservas = List.of(new Reserva());
        when(reservaRepository.findAll()).thenReturn(reservas);

        assertEquals(reservas, operadorService.obtenerReservas());
    }

    @Test
    void registrarPresencialCreaYGuardaElUsuario() {
        Usuario usuario = new Usuario();

        when(usuarioService.registrarPresencial("Ana", "987654321", 12L))
                .thenReturn(usuario);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        assertSame(
                usuario,
                operadorService.registrarPresencial("Ana", "987654321", 12L)
        );

        verify(usuarioRepository).save(usuario);
    }

    @Test
    void validarPagoCambiaReservaPendienteAPagado() {
        Reserva reserva = new Reserva();
        reserva.setEstado(EstadoReserva.PENDIENTE_PAGO);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        assertEquals(
                OperadorService.ResultadoCambioEstado.ACTUALIZADO,
                operadorService.validarPago(1L)
        );

        assertEquals(EstadoReserva.PAGADO, reserva.getEstado());
        verify(reservaRepository).save(reserva);
    }

    @Test
    void marcarNoShowCambiaReservaPendienteAVencida() {
        Reserva reserva = new Reserva();
        reserva.setEstado(EstadoReserva.PENDIENTE_PAGO);

        when(reservaRepository.findById(2L)).thenReturn(Optional.of(reserva));

        assertEquals(
                OperadorService.ResultadoCambioEstado.ACTUALIZADO,
                operadorService.marcarNoShow(2L)
        );

        assertEquals(EstadoReserva.VENCIDA, reserva.getEstado());
        verify(reservaRepository).save(reserva);
    }

    @Test
    void rechazaTransicionCuandoElEstadoNoEsElEsperado() {
        Reserva reserva = new Reserva();
        reserva.setEstado(EstadoReserva.PAGADO);

        when(reservaRepository.findById(3L)).thenReturn(Optional.of(reserva));

        assertEquals(
                OperadorService.ResultadoCambioEstado.ESTADO_NO_PERMITIDO,
                operadorService.validarPago(3L)
        );

        assertEquals(EstadoReserva.PAGADO, reserva.getEstado());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void devuelveNoEncontradaCuandoLaReservaNoExiste() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertEquals(
                OperadorService.ResultadoCambioEstado.NO_ENCONTRADA,
                operadorService.validarPago(99L)
        );

        verify(reservaRepository, never()).save(any(Reserva.class));
    }
}
