package com.lancenter.lancenterhd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion de autenticacion y autorizacion.
 *
 * Matriz de acceso:
 * <ul>
 *   <li>Publico: inicio, login, registro, disponibilidad y recursos estaticos.</li>
 *   <li>CLIENTE: reserva e historial ({@code /cliente/reservar}, {@code /cliente/historial}).</li>
 *   <li>OPERADOR: consola ({@code /operador/**}).</li>
 *   <li>ADMIN: panel ({@code /admin/**}).</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SeguridadConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/error").permitAll()
                .requestMatchers("/auth/login", "/auth/registro").permitAll()
                .requestMatchers("/cliente/disponibilidad").permitAll()
                .requestMatchers("/cliente/reservar", "/cliente/historial").hasRole("CLIENTE")
                .requestMatchers("/operador/**").hasRole("OPERADOR")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .usernameParameter("credencial")
                .passwordParameter("clave")
                .defaultSuccessUrl("/", true)
                .failureUrl("/auth/login?error")
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll())
            // CSRF se desactiva en esta etapa para no romper los formularios
            // existentes; queda como pendiente agregar los tokens en una
            // iteracion posterior.
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
