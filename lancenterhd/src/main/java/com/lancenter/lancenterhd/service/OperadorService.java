
package com.lancenter.lancenterhd.service;

import com.lancenter.lancenterhd.enums.EstadoReserva;
import com.lancenter.lancenterhd.model.Maquina;
import com.lancenter.lancenterhd.model.Reserva;
import com.lancenter.lancenterhd.model.Usuario;
import com.lancenter.lancenterhd.repository.MaquinaRepository;
import com.lancenter.lancenterhd.repository.ReservaRepository;
import com.lancenter.lancenterhd.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class OperadorService {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final MaquinaRepository maquinaRepository;

    public OperadorService(UsuarioService usuarioService,
                           UsuarioRepository usuarioRepository,
                           ReservaRepository reservaRepository,
                           MaquinaRepository maquinaRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
        this.maquinaRepository = maquinaRepository;
    }

    public List<Reserva> obtenerReservas() {
        return reservaRepository.findAll();
    }


    public Map<Long, String> obtenerNombresClientes() {
        return usuarioRepository.findAll().stream()
                .collect(Collectors.toMap(Usuario::getId, Usuario::getNombre));
    }


    public Map<Long, String> obtenerCodigosMaquinas() {
        return maquinaRepository.findAll().stream()
                .collect(Collectors.toMap(Maquina::getId, Maquina::getCodigo));
    }

    public Usuario registrarPresencial(String nombre, String celular, Long operadorId) {
        Usuario usuario = usuarioService.registrarPresencial(nombre, celular, operadorId);
        return usuarioRepository.save(usuario);
    }


    public ResultadoCambioEstado validarPago(Long reservaId) {
        return cambiarEstado(
                reservaId,
                EstadoReserva.PENDIENTE_PAGO,
                EstadoReserva.PAGADO
        );
    }


    public ResultadoCambioEstado marcarNoShow(Long reservaId) {
        return cambiarEstado(
                reservaId,
                EstadoReserva.PENDIENTE_PAGO,
                EstadoReserva.VENCIDA
        );
    }


    private ResultadoCambioEstado cambiarEstado(Long reservaId,
                                                EstadoReserva estadoEsperado,
                                                EstadoReserva estadoNuevo) {

        return reservaRepository.findById(reservaId)
                .map(reserva -> {
                    if (reserva.getEstado() != estadoEsperado) {
                        return ResultadoCambioEstado.ESTADO_NO_PERMITIDO;
                    }

                    reserva.setEstado(estadoNuevo);
                    reservaRepository.save(reserva);
                    return ResultadoCambioEstado.ACTUALIZADO;
                })
                .orElse(ResultadoCambioEstado.NO_ENCONTRADA);
    }

    public enum ResultadoCambioEstado {
        ACTUALIZADO,
        NO_ENCONTRADA,
        ESTADO_NO_PERMITIDO
    }
}
