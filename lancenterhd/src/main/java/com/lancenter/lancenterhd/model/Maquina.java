package com.lancenter.lancenterhd.model;

import com.lancenter.lancenterhd.enums.EstadoMaquina;
import jakarta.persistence.*;

@Entity
@Table(name = "maquinas")
public class Maquina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    private String especificaciones;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMaquina estado;

    public Maquina() {
    }

    public Maquina(String codigo, String especificaciones, EstadoMaquina estado) {
        this.codigo = codigo;
        this.especificaciones = especificaciones;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getEspecificaciones() { return especificaciones; }
    public void setEspecificaciones(String especificaciones) { this.especificaciones = especificaciones; }

    public EstadoMaquina getEstado() { return estado; }
    public void setEstado(EstadoMaquina estado) { this.estado = estado; }
}