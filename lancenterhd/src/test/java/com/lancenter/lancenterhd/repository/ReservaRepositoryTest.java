package com.lancenter.lancenterhd.repository;

import com.lancenter.lancenterhd.enums.Canal;
import com.lancenter.lancenterhd.enums.EstadoReserva;
import com.lancenter.lancenterhd.model.Reserva;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica que ReservaRepository es detectado por Spring Data JPA y que la
 * entidad Reserva se persiste y se recupera correctamente usando la base de
 * datos H2 en memoria configurada para las pruebas.
 */
@DataJpaTest
class ReservaRepositoryTest {

    @Autowired
    private ReservaRepository reservaRepository;

    @Test
    void guardaYRecuperaUnaReservaPorId() {
        Reserva reserva = new Reserva();
        reserva.setUsuarioId(1L);
        reserva.setMaquinaId(2L);
        reserva.setFecha(LocalDate.of(2026, 10, 20));
        reserva.setHoraInicio(LocalTime.of(18, 0));
        reserva.setHoraFin(LocalTime.of(19, 0));
        reserva.setEstado(EstadoReserva.PENDIENTE_PAGO);
        reserva.setCanal(Canal.WEB);
        reserva.setCosto(new BigDecimal("5.00"));

        Reserva guardada = reservaRepository.save(reserva);

        assertNotNull(guardada.getId(), "La reserva debe recibir un id autogenerado");

        Optional<Reserva> recuperada = reservaRepository.findById(guardada.getId());
        assertTrue(recuperada.isPresent(), "La reserva guardada debe poder recuperarse por id");

        Reserva valor = recuperada.get();
        assertEquals(1L, valor.getUsuarioId());
        assertEquals(2L, valor.getMaquinaId());
        assertEquals(LocalDate.of(2026, 10, 20), valor.getFecha());
        assertEquals(LocalTime.of(18, 0), valor.getHoraInicio());
        assertEquals(LocalTime.of(19, 0), valor.getHoraFin());
        assertEquals(EstadoReserva.PENDIENTE_PAGO, valor.getEstado());
        assertEquals(Canal.WEB, valor.getCanal());
        assertEquals(0, new BigDecimal("5.00").compareTo(valor.getCosto()));
    }
}
