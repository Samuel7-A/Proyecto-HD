package com.lancenter.lancenterhd.controller;

import com.lancenter.lancenterhd.model.Usuario;
import com.lancenter.lancenterhd.repository.UsuarioRepository;
import com.lancenter.lancenterhd.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OperadorControllerTest {

    private final UsuarioService usuarioService = mock(UsuarioService.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final OperadorController controller = new OperadorController(usuarioService, usuarioRepository);

    @Test
    void mostrarConsolaAgregaUnaListaVaciaCuandoNoHayRepositorioDeReservas() {
        Model modelo = mock(Model.class);

        assertEquals("operador/consola", controller.mostrarConsola(12L, modelo));
        verify(modelo).addAttribute("reservas", List.of());
        verify(modelo).addAttribute("operadorId", 12L);
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
