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
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminControllerTest {

    private final MaquinaRepository maquinaRepository = mock(MaquinaRepository.class);
    private final ReservaRepository reservaRepository = mock(ReservaRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final AdminController controller =
            new AdminController(maquinaRepository, reservaRepository, usuarioRepository);

    @Test
    void mostrarPanelDevuelveLaVistaDelPanel() {
        Model modelo = mock(Model.class);
        sinDatos();

        assertEquals("admin/panel", controller.mostrarPanel(modelo));
    }

    @Test
    void mostrarPanelCuentaLasMaquinasPorEstado() {
        Model modelo = mock(Model.class);
        when(maquinaRepository.findAll()).thenReturn(List.of(
                maquina(1L, "M-01", EstadoMaquina.DISPONIBLE),
                maquina(2L, "M-02", EstadoMaquina.DISPONIBLE),
                maquina(3L, "M-03", EstadoMaquina.OCUPADA),
                maquina(4L, "M-04", EstadoMaquina.MANTENIMIENTO)));
        when(reservaRepository.findAll()).thenReturn(List.of());
        when(usuarioRepository.findAll()).thenReturn(List.of());

        controller.mostrarPanel(modelo);

        verify(modelo).addAttribute("totalMaquinas", 4);
        verify(modelo).addAttribute("maquinasDisponibles", 2L);
        verify(modelo).addAttribute("maquinasOcupadas", 1L);
        verify(modelo).addAttribute("maquinasMantenimiento", 1L);
    }

    @Test
    void mostrarPanelSumaSoloLoEfectivamenteCobrado() {
        Model modelo = mock(Model.class);
        when(maquinaRepository.findAll()).thenReturn(List.of());
        when(usuarioRepository.findAll()).thenReturn(List.of());
        when(reservaRepository.findAll()).thenReturn(List.of(
                reserva(EstadoReserva.PAGADO, "15.00"),
                reserva(EstadoReserva.COMPLETADA, "10.50"),
                reserva(EstadoReserva.PENDIENTE_PAGO, "99.00"),
                reserva(EstadoReserva.VENCIDA, "99.00"),
                reserva(EstadoReserva.CANCELADA, "99.00")));

        controller.mostrarPanel(modelo);

        // Solo PAGADO y COMPLETADA suman: 15.00 + 10.50.
        verify(modelo).addAttribute("ingresos", new BigDecimal("25.50"));
    }

    @Test
    void mostrarPanelNoFallaSiUnaReservaCobradaNoTieneCosto() {
        Model modelo = mock(Model.class);
        when(maquinaRepository.findAll()).thenReturn(List.of());
        when(usuarioRepository.findAll()).thenReturn(List.of());
        when(reservaRepository.findAll()).thenReturn(List.of(
                reserva(EstadoReserva.PAGADO, null)));

        controller.mostrarPanel(modelo);

        verify(modelo).addAttribute("ingresos", BigDecimal.ZERO);
    }

    @Test
    void mostrarPanelCuentaLosClientesRegistrados() {
        Model modelo = mock(Model.class);
        when(maquinaRepository.findAll()).thenReturn(List.of());
        when(reservaRepository.findAll()).thenReturn(List.of());
        when(usuarioRepository.findAll()).thenReturn(List.of(
                usuario(1L, "Ana Torres", Rol.CLIENTE),
                usuario(2L, "Luis Paredes", Rol.CLIENTE),
                usuario(3L, "Rosa Diaz", Rol.OPERADOR),
                usuario(4L, "Admin", Rol.ADMIN)));

        controller.mostrarPanel(modelo);

        verify(modelo).addAttribute("totalUsuarios", 4);
        verify(modelo).addAttribute("totalClientes", 2L);
    }

    @Test
    void mostrarPanelPasaLasTraduccionesYLosColores() {
        Model modelo = mock(Model.class);
        sinDatos();

        controller.mostrarPanel(modelo);

        verify(modelo).addAttribute(eq("nombresClientes"), any());
        verify(modelo).addAttribute(eq("codigosMaquinas"), any());
        verify(modelo).addAttribute(eq("coloresEstado"), any());
    }

    private void sinDatos() {
        when(maquinaRepository.findAll()).thenReturn(List.of());
        when(reservaRepository.findAll()).thenReturn(List.of());
        when(usuarioRepository.findAll()).thenReturn(List.of());
    }

    private Maquina maquina(Long id, String codigo, EstadoMaquina estado) {
        Maquina maquina = new Maquina();
        maquina.setId(id);
        maquina.setCodigo(codigo);
        maquina.setEstado(estado);
        return maquina;
    }

    private Usuario usuario(Long id, String nombre, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setRol(rol);
        return usuario;
    }

    private Reserva reserva(EstadoReserva estado, String costo) {
        Reserva reserva = new Reserva();
        reserva.setEstado(estado);
        reserva.setCosto(costo == null ? null : new BigDecimal(costo));
        return reserva;
    }
}
