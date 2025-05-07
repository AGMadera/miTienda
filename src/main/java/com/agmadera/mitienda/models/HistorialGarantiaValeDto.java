package com.agmadera.mitienda.models;

import java.util.Date;

public class HistorialGarantiaValeDto {
    private Long id;
    private float saldo;
    private Date fecha;
    private Long idProductoVenta;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Long getIdProductoVenta() {
        return idProductoVenta;
    }

    public void setIdProductoVenta(Long idProductoVenta) {
        this.idProductoVenta = idProductoVenta;
    }
}
