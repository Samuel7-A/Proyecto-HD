package com.lancenter.lancenterhd.security;

import com.lancenter.lancenterhd.model.Usuario;
import com.lancenter.lancenterhd.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Carga al usuario desde la base de datos a partir de la credencial del
 * formulario de login (correo o celular).
 *
 * Las cuentas presenciales creadas por el operador no tienen clave todavia, asi
 * que se rechazan como si no existieran hasta que el cliente active su cuenta.
 */
@Service
public class ServicioDetallesUsuario implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public ServicioDetallesUsuario(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String credencial) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(credencial)
                .orElseGet(() -> usuarioRepository.findByCelular(credencial).orElse(null));

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + credencial);
        }
        if (usuario.getPasswordHash() == null) {
            throw new UsernameNotFoundException("La cuenta aun no tiene clave de acceso: " + credencial);
        }
        return new UsuarioDetalles(usuario);
    }
}
