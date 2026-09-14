package com.lancenter.lancenterhd.service;

import com.lancenter.lancenterhd.enums.Rol;
import com.lancenter.lancenterhd.model.Usuario;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Registro y gestion de usuarios.
 *
 * Nota sobre el hash: se usa SHA-256 con el email como sal para no agregar
 * dependencias al proyecto. El paso siguiente es migrar a BCrypt mediante
 * spring-security-crypto, que es el algoritmo adecuado para contrasenas.
 */
@Service
public class UsuarioService {

    /**
     * Registra un cliente que llega desde la web.
     * La clave se guarda hasheada; nunca en texto plano.
     */
    public Usuario registrar(String nombre, String email, String celular, String clave) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setCelular(celular);
        usuario.setPasswordHash(hash(clave, email));
        usuario.setRol(Rol.CLIENTE);
        return usuario;
    }

    /**
     * Verifica una clave contra el hash guardado.
     * Se usa el email como sal, igual que en el registro.
     */
    public boolean claveCorrecta(Usuario usuario, String clave) {
        if (usuario.getPasswordHash() == null) {
            return false;
        }
        return usuario.getPasswordHash().equals(hash(clave, usuario.getEmail()));
    }

    private String hash(String clave, String sal) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String entrada = clave + (sal == null ? "" : sal);
            byte[] salida = digest.digest(entrada.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(salida);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo de hash no disponible", e);
        }
    }
}
