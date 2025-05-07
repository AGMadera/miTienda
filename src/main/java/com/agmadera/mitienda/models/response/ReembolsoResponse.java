package com.agmadera.mitienda.models.response;

import java.util.Date;

public class ReembolsoResponse {

    private Long id;
    private Long idCompra;
    private String producto;
    private float cantidadReembolso;
    private Date fecha;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Long idCompra) {
        this.idCompra = idCompra;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public float getCantidadReembolso() {
        return cantidadReembolso;
    }

    public void setCantidadReembolso(float cantidadReembolso) {
        this.cantidadReembolso = cantidadReembolso;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
