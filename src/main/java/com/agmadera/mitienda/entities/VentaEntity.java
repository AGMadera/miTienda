package com.agmadera.mitienda.entities;

import java.util.Date;
import java.util.List;

public class VentaEntity {

    private Long id;
    private List<ProductoVentaEntity> productoVentaEntity;
    private float totalGenrealAntesDesc;
    private float descuentosEnTotalGen;
    private float totalGenreal;
    private Date fechaVenta;
    private String mensaje;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ProductoVentaEntity> getProductoVentaEntity() {
        return productoVentaEntity;
    }

    public void setProductoVentaEntity(List<ProductoVentaEntity> productoVentaEntity) {
        this.productoVentaEntity = productoVentaEntity;
    }

    public float getTotalGenrealAntesDesc() {
        return totalGenrealAntesDesc;
    }

    public void setTotalGenrealAntesDesc(float totalGenrealAntesDesc) {
        this.totalGenrealAntesDesc = totalGenrealAntesDesc;
    }

    public float getDescuentosEnTotalGen() {
        return descuentosEnTotalGen;
    }

    public void setDescuentosEnTotalGen(float descuentosEnTotalGen) {
        this.descuentosEnTotalGen = descuentosEnTotalGen;
    }

    public float getTotalGenreal() {
        return totalGenreal;
    }

    public void setTotalGenreal(float totalGenreal) {
        this.totalGenreal = totalGenreal;
    }

    public Date getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Date fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
