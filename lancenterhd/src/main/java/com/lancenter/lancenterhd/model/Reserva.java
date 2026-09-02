package com.lancenter.lancenterhd.model;

import java.time.LocalDate;
import java.time.LocalTime;


public class Reserva {

    private Long id;
    private Long usuarioId;
    private Long maquinaId;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String estado;
    private String canal;
    private Double costo;

    public Reserva() {
    }

    public Reserva(Long id, Long usuarioId, Long maquinaId, LocalDate fecha,
                   LocalTime horaInicio, LocalTime horaFin, String estado,
                   String canal, Double costo) {
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

    /**
     * Estados posibles de una reserva:
     * PENDIENTE, CONFIRMADA, EN_CURSO, FINALIZADA, CANCELADA, NO_SHOW
     */
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Canal por el que se realizó la reserva: ONLINE o PRESENCIAL
     */
    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
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
                ", estado='" + estado + '\'' +
                ", canal='" + canal + '\'' +
                ", costo=" + costo +
                '}';
    }
}