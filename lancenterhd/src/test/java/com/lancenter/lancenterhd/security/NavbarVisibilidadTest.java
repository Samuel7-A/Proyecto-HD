package com.lancenter.lancenterhd.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Comprueba que el navbar compartido muestra u oculta enlaces segun el estado
 * de autenticacion y el rol.
 */
@SpringBootTest
@AutoConfigureMockMvc
class NavbarVisibilidadTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void elNavbarDeUnVisitanteAnonimoNoMuestraEnlacesAdministrativos() throws Exception {
        String html = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertTrue(html.contains("Registrarme"), "Debe verse el enlace de registro");
        assertFalse(html.contains("Consola Operador"), "Un anonimo no debe ver la consola del operador");
        assertFalse(html.contains("Panel Admin"), "Un anonimo no debe ver el panel de administracion");
        assertFalse(html.contains("Abrir Consola"), "Un anonimo no debe ver la tarjeta del operador");
    }

    @Test
    void elNavbarDeUnClienteMuestraSusEnlacesYNoLosDeOtrosRoles() throws Exception {
        String html = mockMvc.perform(get("/").with(user("cliente@test.pe").roles("CLIENTE")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertTrue(html.contains("Mis Reservas"), "El cliente debe ver 'Mis Reservas'");
        assertFalse(html.contains("Consola Operador"), "El cliente no debe ver la consola del operador");
        assertFalse(html.contains("Panel Admin"), "El cliente no debe ver el panel de administracion");
    }

    @Test
    void elNavbarDeUnOperadorMuestraSuConsolaYNoElPanel() throws Exception {
        String html = mockMvc.perform(get("/").with(user("operador@test.pe").roles("OPERADOR")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertTrue(html.contains("Consola Operador"), "El operador debe ver su consola");
        assertFalse(html.contains("Panel Admin"), "El operador no debe ver el panel de administracion");
        assertFalse(html.contains("Mis Reservas"), "El operador no debe ver 'Mis Reservas'");
    }

    @Test
    void elNavbarDeUnAdminMuestraElPanelYNoOtrosRoles() throws Exception {
        String html = mockMvc.perform(get("/").with(user("admin@test.pe").roles("ADMIN")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertTrue(html.contains("Panel Admin"), "El administrador debe ver el panel");
        assertFalse(html.contains("Consola Operador"), "El admin no debe ver la consola del operador");
        assertFalse(html.contains("Mis Reservas"), "El admin no debe ver 'Mis Reservas'");
    }
}
