package com.lancenter.lancenterhd.security;

import com.lancenter.lancenterhd.enums.EstadoMaquina;
import com.lancenter.lancenterhd.model.Maquina;
import com.lancenter.lancenterhd.repository.MaquinaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica la autorizacion por roles a nivel de rutas HTTP.
 *
 * Ocultar enlaces en el navbar no es suficiente: estas pruebas comprueban que
 * el servidor rechaza (403) o redirige (302) segun el rol de quien pide.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SeguridadWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MaquinaRepository maquinaRepository;

    private Long maquinaId;

    @BeforeEach
    void prepararMaquina() {
        Maquina maquina = maquinaRepository.findAll().stream()
                .filter(m -> "M-TEST-SEC".equals(m.getCodigo()))
                .findFirst()
                .orElseGet(() -> maquinaRepository.save(
                        new Maquina("M-TEST-SEC", "Maquina de prueba de seguridad", EstadoMaquina.DISPONIBLE)));
        maquinaId = maquina.getId();
    }

    @Test
    void unVisitanteAnonimoPuedeVerLasRutasPublicas() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
        mockMvc.perform(get("/cliente/disponibilidad")).andExpect(status().isOk());
        mockMvc.perform(get("/auth/login")).andExpect(status().isOk());
        mockMvc.perform(get("/auth/registro")).andExpect(status().isOk());
    }

    @Test
    void unVisitanteAnonimoEsRedirigidoALoginEnRutasProtegidas() throws Exception {
        mockMvc.perform(get("/admin/panel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
        mockMvc.perform(get("/operador/consola"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
        mockMvc.perform(get("/cliente/historial"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
        mockMvc.perform(get("/cliente/reservar").param("maquinaId", String.valueOf(maquinaId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    @Test
    void unClienteAccedeASusRutasPeroNoALasDeOtrosRoles() throws Exception {
        mockMvc.perform(get("/cliente/historial").with(user("cliente@test.pe").roles("CLIENTE")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/cliente/reservar").param("maquinaId", String.valueOf(maquinaId))
                        .with(user("cliente@test.pe").roles("CLIENTE")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/admin/panel").with(user("cliente@test.pe").roles("CLIENTE")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/operador/consola").with(user("cliente@test.pe").roles("CLIENTE")))
                .andExpect(status().isForbidden());
    }

    @Test
    void unOperadorAccedeASuConsolaPeroNoAlPanelNiAlHistorial() throws Exception {
        mockMvc.perform(get("/operador/consola").with(user("operador@test.pe").roles("OPERADOR")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/admin/panel").with(user("operador@test.pe").roles("OPERADOR")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/cliente/historial").with(user("operador@test.pe").roles("OPERADOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void unAdminAccedeAlPanelPeroNoALaConsola() throws Exception {
        mockMvc.perform(get("/admin/panel").with(user("admin@test.pe").roles("ADMIN")))
                .andExpect(status().isOk());
        mockMvc.perform(get("/operador/consola").with(user("admin@test.pe").roles("ADMIN")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/cliente/historial").with(user("admin@test.pe").roles("ADMIN")))
                .andExpect(status().isForbidden());
    }
}
