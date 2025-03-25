package com.agmadera.mitienda.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class GanaciaEntity {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;
    private Long idVentaRef;
    private Long idProductoRef;
    private Date fecha;
    private float compra;
    private float venta;
    private float ganacia;

    public GanaciaEntity(Long idVentaRef, Long idProductoRef, Date fecha) {
        this.idVentaRef = idVentaRef;
        this.idProductoRef = idProductoRef;
        this.fecha = fecha;
    }

    public GanaciaEntity() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdVentaRef() {
        return idVentaRef;
    }

    public void setIdVentaRef(Long idVentaRef) {
        this.idVentaRef = idVentaRef;
    }

    public Long getIdProductoRef() {
        return idProductoRef;
    }

    public void setIdProductoRef(Long idProductoRef) {
        this.idProductoRef = idProductoRef;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public float getCompra() {
        return compra;
    }

    public void setCompra(float compra) {
        this.compra = compra;
    }

    public float getVenta() {
        return venta;
    }

    public void setVenta(float venta) {
        this.venta = venta;
    }

    public float getGanacia() {
        return ganacia;
    }

    public void setGanacia(float ganacia) {
        this.ganacia = ganacia;
    }
}
