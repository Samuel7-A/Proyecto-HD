package com.lancenter.lancenterhd.config;

import com.lancenter.lancenterhd.enums.Rol;
import com.lancenter.lancenterhd.model.Usuario;
import com.lancenter.lancenterhd.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Cuentas de prueba SOLO para desarrollo.
 *
 * Se activan unicamente con el perfil {@code dev}
 * ({@code --spring.profiles.active=dev}) y se crean solo si no existen, de modo
 * que nunca se ejecutan en produccion ni pisan usuarios reales.
 *
 * Credenciales (exclusivas de desarrollo, no usar en produccion):
 * <ul>
 *   <li>ADMIN: admin@lancenter.pe / admin123</li>
 *   <li>OPERADOR: operador@lancenter.pe / operador123</li>
 *   <li>CLIENTE: cliente@lancenter.pe / cliente123</li>
 * </ul>
 */
@Configuration
@Profile("dev")
public class DatosDesarrollo {

    @Bean
    CommandLineRunner sembrarUsuariosDesarrollo(UsuarioRepository repositorio, PasswordEncoder codificador) {
        return args -> {
            crearSiNoExiste(repositorio, codificador, "Administrador", "admin@lancenter.pe", "999000111", "admin123", Rol.ADMIN);
            crearSiNoExiste(repositorio, codificador, "Operador", "operador@lancenter.pe", "999000222", "operador123", Rol.OPERADOR);
            crearSiNoExiste(repositorio, codificador, "Cliente", "cliente@lancenter.pe", "999000333", "cliente123", Rol.CLIENTE);
        };
    }

    private void crearSiNoExiste(UsuarioRepository repositorio, PasswordEncoder codificador,
                                 String nombre, String email, String celular, String clave, Rol rol) {
        if (repositorio.findByEmail(email).isPresent() || repositorio.findByCelular(celular).isPresent()) {
            return;
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setCelular(celular);
        usuario.setPasswordHash(codificador.encode(clave));
        usuario.setRol(rol);
        repositorio.save(usuario);
    }
}
