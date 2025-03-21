package com.agmadera.mitienda.entities;


public class ProductoVentaEntity {

    private Long id;
    private Long idProductoRef;
    private String nombre;
    private int cantidad;
    private float precio;
    private float descuento;
    private float total;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdProductoRef() {
        return idProductoRef;
    }

    public void setIdProductoRef(Long idProductoRef) {
        this.idProductoRef = idProductoRef;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public float getDescuento() {
        return descuento;
    }

    public void setDescuento(float descuento) {
        this.descuento = descuento;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
