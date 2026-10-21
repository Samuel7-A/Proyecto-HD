package com.lancenter.lancenterhd.security;

import com.lancenter.lancenterhd.enums.Rol;
import com.lancenter.lancenterhd.model.Usuario;
import com.lancenter.lancenterhd.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica el flujo real de autenticacion (login / logout / registro) contra la
 * base de datos H2 de pruebas.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthFlujoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void prepararClienteDePrueba() {
        if (usuarioRepository.findByEmail("cliente@test.pe").isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setNombre("Cliente de Prueba");
            usuario.setEmail("cliente@test.pe");
            usuario.setCelular("998877665");
            usuario.setPasswordHash(passwordEncoder.encode("clave123"));
            usuario.setRol(Rol.CLIENTE);
            usuarioRepository.save(usuario);
        }
    }

    @Test
    void unClienteRegistradoPuedeIniciarSesionYVerSuNavbar() throws Exception {
        MvcResult login = mockMvc.perform(post("/auth/login")
                        .param("credencial", "cliente@test.pe")
                        .param("clave", "clave123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andReturn();

        MockHttpSession sesion = (MockHttpSession) login.getRequest().getSession();

        mockMvc.perform(get("/cliente/historial").session(sesion))
                .andExpect(status().isOk());

        String html = mockMvc.perform(get("/").session(sesion))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertTrue(html.contains("Mis Reservas"), "El cliente autenticado debe ver 'Mis Reservas'");
        assertTrue(html.contains("Cerrar sesi"), "Debe verse la opcion de cerrar sesion");
    }

    @Test
    void unClientePuedeCerrarSesion() throws Exception {
        MvcResult login = mockMvc.perform(post("/auth/login")
                        .param("credencial", "cliente@test.pe")
                        .param("clave", "clave123"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        MockHttpSession sesion = (MockHttpSession) login.getRequest().getSession();

        mockMvc.perform(post("/auth/logout").session(sesion))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?logout"));
    }

    @Test
    void unaClaveIncorrectaNoPermiteEntrar() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .param("credencial", "cliente@test.pe")
                        .param("clave", "clave-incorrecta"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?error"));
    }

    @Test
    void elRegistroCreaUnaCuentaConClaveCifradaYRolCliente() throws Exception {
        mockMvc.perform(post("/auth/registro")
                        .param("nombre", "Nuevo Cliente")
                        .param("email", "nuevo@test.pe")
                        .param("celular", "977766655")
                        .param("clave", "claveSegura"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?registrado=true"));

        Usuario creado = usuarioRepository.findByEmail("nuevo@test.pe").orElseThrow();
        assertNotEquals("claveSegura", creado.getPasswordHash(), "La clave no debe guardarse en texto plano");
        assertTrue(creado.getPasswordHash().startsWith("$2"), "La clave debe estar cifrada con BCrypt");
        assertEquals(Rol.CLIENTE, creado.getRol());
    }
}
