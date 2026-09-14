package com.lancenter.lancenterhd.model;

import com.lancenter.lancenterhd.enums.Rol;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Usuario del sistema (Cliente, Operador o Administrador).
 *
 * Los clientes que llegan al mostrador se registran con nombre y celular,
 * sin contrasena: en ese caso passwordHash queda nulo y la cuenta no tiene
 * acceso web hasta que el cliente defina una clave.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(unique = true)
    private String email;

    /**
     * Clave natural del cliente presencial. Es unico porque es el dato con el
     * que el operador lo identifica en su siguiente visita.
     */
    @Column(unique = true)
    private String celular;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    public Usuario() {
    }

    public Usuario(Long id, String nombre, String email, String celular, String passwordHash, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.celular = celular;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
