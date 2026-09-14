package com.lancenter.lancenterhd.service;

import com.lancenter.lancenterhd.enums.Rol;
import com.lancenter.lancenterhd.model.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Pruebas del registro de usuarios. Son pruebas unitarias puras: no levantan
 * el contexto de Spring, asi no dependen de que exista una base de datos.
 */
class UsuarioServiceTest {

    private final UsuarioService servicio = new UsuarioService();

    @Test
    void laClaveNoSeGuardaEnTextoPlano() {
        Usuario usuario = servicio.registrar("Ana Torres", "ana@utp.pe", "987654321", "clave123");

        assertNotNull(usuario.getPasswordHash(), "El hash no debe ser nulo");
        assertNotEquals("clave123", usuario.getPasswordHash(), "La clave no debe guardarse tal cual");
    }

    @Test
    void unClienteRegistradoDesdeLaWebTieneRolCliente() {
        Usuario usuario = servicio.registrar("Luis Paredes", "luis@utp.pe", "912345678", "clave456");

        assertEquals(Rol.CLIENTE, usuario.getRol());
    }

    @Test
    void dosClientesConLaMismaClaveTienenHashesDistintos() {
        Usuario primero = servicio.registrar("Ana Torres", "ana@utp.pe", "987654321", "clave123");
        Usuario segundo = servicio.registrar("Luis Paredes", "luis@utp.pe", "912345678", "clave123");

        assertNotEquals(primero.getPasswordHash(), segundo.getPasswordHash(),
                "El hash debe incorporar el email para que dos claves iguales no coincidan");
    }
}
