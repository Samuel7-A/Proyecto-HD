package com.lancenter.lancenterhd.repository;

import com.lancenter.lancenterhd.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Acceso a los usuarios registrados.
 * El celular es la clave con la que el operador busca a un cliente que vuelve.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByCelular(String celular);
}
