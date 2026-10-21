package com.lancenter.lancenterhd.security;

import com.lancenter.lancenterhd.model.Usuario;
import com.lancenter.lancenterhd.repository.UsuarioRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Resuelve el {@link Usuario} autenticado en la sesion actual.
 *
 * Los controladores usan este componente en lugar de leer parametros del
 * navegador (como {@code operadorId} o {@code celular}) para saber quien esta
 * operando, de modo que el rol y la identidad salen siempre de la sesion.
 */
@Component
public class SesionUsuario {

    private final UsuarioRepository usuarioRepository;

    public SesionUsuario(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        String credencial = auth.getName();
        return usuarioRepository.findByEmail(credencial)
                .or(() -> usuarioRepository.findByCelular(credencial));
    }
}
