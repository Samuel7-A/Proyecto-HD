package com.lancenter.lancenterhd.config;

import com.lancenter.lancenterhd.enums.EstadoMaquina;
import com.lancenter.lancenterhd.model.Maquina;
import com.lancenter.lancenterhd.repository.MaquinaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Inserta maquinas de prueba al arrancar, solo si la tabla esta vacia.
 */
@Configuration
public class CargaDatosMaquinas {

    @Bean
    CommandLineRunner cargarMaquinas(MaquinaRepository repositorio) {
        return args -> {
            if (repositorio.count() > 0) {
                return;
            }
            repositorio.saveAll(List.of(
                new Maquina("M-01", "PC Gamer High-End, RTX 4070, 32GB RAM - Zona VIP", EstadoMaquina.DISPONIBLE),
                new Maquina("M-02", "PC Gamer High-End, RTX 4060, 16GB RAM - Zona VIP", EstadoMaquina.DISPONIBLE),
                new Maquina("M-03", "PC Estandar, GTX 1660, 16GB RAM - Zona General", EstadoMaquina.OCUPADA),
                new Maquina("M-04", "PC Estandar, GTX 1650, 8GB RAM - Zona General", EstadoMaquina.DISPONIBLE),
                new Maquina("M-05", "PC Basica, Intel UHD, 8GB RAM - Zona General", EstadoMaquina.MANTENIMIENTO),
                new Maquina("M-06", "PC Streaming, RTX 4070 Ti, 32GB RAM - Zona Streaming", EstadoMaquina.DISPONIBLE)
            ));
        };
    }
}