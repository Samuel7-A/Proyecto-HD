package com.lancenter.lancenterhd.service;

import com.lancenter.lancenterhd.enums.Rol;
import com.lancenter.lancenterhd.model.Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Registro y gestion de usuarios.
 *
 * Las contrasenas se guardan con BCrypt, que aplica una sal aleatoria por
 * usuario y un factor de costo. La clave original nunca se almacena ni se
 * puede recuperar a partir del hash.
 */
@Service
public class UsuarioService {

    private final PasswordEncoder codificador = new BCryptPasswordEncoder();

    /**
     * Registra un cliente que llega desde la web.
     */
    public Usuario registrar(String nombre, String email, String celular, String clave) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setCelular(celular);
        usuario.setPasswordHash(codificador.encode(clave));
        usuario.setRol(Rol.CLIENTE);
        return usuario;
    }

    /**
     * Registra un cliente que llega presencialmente al mostrador.
     * La cuenta nace sin correo ni contrasena y puede activarse despues.
     */
    public Usuario registrarPresencial(String nombre, String celular, Long operadorId) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCelular(celular);
        usuario.setEmail(null);
        usuario.setPasswordHash(null);
        usuario.setRol(Rol.CLIENTE);
        usuario.setOperadorId(operadorId);
        return usuario;
    }

    /**
     * Comprueba una clave contra el hash guardado.
     * Devuelve falso si la cuenta no tiene contrasena, como ocurre con los
     * clientes que el operador registra en el mostrador.
     */
    public boolean claveCorrecta(Usuario usuario, String clave) {
        if (usuario == null || usuario.getPasswordHash() == null) {
            return false;
        }
        return codificador.matches(clave, usuario.getPasswordHash());
    }
}
