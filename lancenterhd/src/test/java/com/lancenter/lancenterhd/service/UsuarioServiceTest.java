package com.lancenter.lancenterhd.service;

import com.lancenter.lancenterhd.enums.Rol;
import com.lancenter.lancenterhd.model.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

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
    void dosCuentasConLaMismaClaveGuardanHashesDistintos() {
        Usuario primero = servicio.registrar("Ana Torres", "ana@utp.pe", "987654321", "clave123");
        Usuario segundo = servicio.registrar("Luis Paredes", "luis@utp.pe", "912345678", "clave123");

        assertNotEquals(primero.getPasswordHash(), segundo.getPasswordHash(),
                "BCrypt usa una sal aleatoria por cuenta, asi que los hashes no coinciden");
    }

    @Test
    void laClaveCorrectaIngresaYLaIncorrectaNo() {
        Usuario usuario = servicio.registrar("Ana Torres", "ana@utp.pe", "987654321", "clave123");

        assertTrue(servicio.claveCorrecta(usuario, "clave123"), "La clave correcta debe validar");
        assertFalse(servicio.claveCorrecta(usuario, "otraClave"), "Una clave distinta no debe validar");
    }

    @Test
    void unaCuentaSinContrasenaNoPuedeIngresar() {
        Usuario presencial = new Usuario();
        presencial.setNombre("Cliente de mostrador");
        presencial.setCelular("999888777");
        presencial.setRol(Rol.CLIENTE);

        assertFalse(servicio.claveCorrecta(presencial, "cualquiera"),
                "El cliente registrado por el operador no tiene acceso web");
    }

    @Test
    void unClientePresencialSeRegistraSinContrasenaNiCorreo() {
        Usuario usuario = servicio.registrarPresencial(
                "Cliente de mostrador",
                "999888777",
                10L
        );

        assertEquals("Cliente de mostrador", usuario.getNombre());
        assertEquals("999888777", usuario.getCelular());
        assertEquals(Rol.CLIENTE, usuario.getRol());
        assertEquals(10L, usuario.getOperadorId());
        assertNull(usuario.getEmail());
        assertNull(usuario.getPasswordHash());
    }

    @Test
    void unClientePresencialNoPuedeIngresarAntesDeActivarse() {
        Usuario usuario = servicio.registrarPresencial(
                "Cliente de mostrador",
                "999888777",
                10L
        );

        assertFalse(
                servicio.claveCorrecta(usuario, "cualquierClave"),
                "Una cuenta presencial sin activar no debe poder iniciar sesion"
        );
    }

}
