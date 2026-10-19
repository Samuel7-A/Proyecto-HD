package com.lancenter.lancenterhd.repository;

import com.lancenter.lancenterhd.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a las reservas registradas en el sistema.
 * Sigue el mismo patron que UsuarioRepository y MaquinaRepository.
 */
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}
