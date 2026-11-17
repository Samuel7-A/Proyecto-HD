package com.lancenter.lancenterhd.config;

import com.lancenter.lancenterhd.security.UsuarioDetalles;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Expone a todas las vistas el estado de autenticacion para que el navbar
 * muestre u oculte enlaces segun el rol, sin necesidad de duplicar logica en
 * cada controlador.
 */
@ControllerAdvice
public class AtributosVista {

    @ModelAttribute("rolActual")
    public String rolActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (esAnonimo(auth)) {
            return null;
        }
        if (auth.getPrincipal() instanceof UsuarioDetalles detalles) {
            return detalles.getRol().name();
        }
        // Respaldo para pruebas con @WithMockUser, donde el principal no es
        // UsuarioDetalles sino un User de Spring Security.
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring("ROLE_".length()))
                .findFirst()
                .orElse(null);
    }

    @ModelAttribute("nombreUsuario")
    public String nombreUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (esAnonimo(auth)) {
            return null;
        }
        if (auth.getPrincipal() instanceof UsuarioDetalles detalles) {
            return detalles.getNombre();
        }
        return auth.getName();
    }

    @ModelAttribute("autenticado")
    public boolean autenticado() {
        return !esAnonimo(SecurityContextHolder.getContext().getAuthentication());
    }

    private boolean esAnonimo(Authentication auth) {
        return auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken;
    }
}
