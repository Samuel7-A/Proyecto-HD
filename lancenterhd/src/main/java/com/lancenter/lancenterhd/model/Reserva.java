package com.lancenter.lancenterhd.model;

import com.lancenter.lancenterhd.enums.Canal;
import com.lancenter.lancenterhd.enums.EstadoReserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidad Reserva.
 * POJO simple, sin JPA y sin Lombok (getters/setters escritos a mano),
 * tal como se definió para la rama feature/reservas.
 */
public class Reserva {

    private Long id;
    private Long usuarioId;
    private Long maquinaId;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private EstadoReserva estado;
    private Canal canal;
    private BigDecimal costo;

    public Reserva() {
    }

    public Reserva(Long id, Long usuarioId, Long maquinaId, LocalDate fecha,
                   LocalTime horaInicio, LocalTime horaFin, EstadoReserva estado,
                   Canal canal, BigDecimal costo) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.maquinaId = maquinaId;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
        this.canal = canal;
        this.costo = costo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getMaquinaId() {
        return maquinaId;
    }

    public void setMaquinaId(Long maquinaId) {
        this.maquinaId = maquinaId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public Canal getCanal() {
        return canal;
    }

    public void setCanal(Canal canal) {
        this.canal = canal;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", maquinaId=" + maquinaId +
                ", fecha=" + fecha +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                ", estado=" + estado +
                ", canal=" + canal +
                ", costo=" + costo +
                '}';
    }
}