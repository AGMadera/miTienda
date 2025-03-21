package com.agmadera.mitienda.models;

import com.agmadera.mitienda.entities.ProductoEntity;

public class StockDTO {
    private Long id;
    private ProductoEntity producto;
    private int unidadesVendidas;
    private int unidadesExistencia;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductoEntity getProducto() {
        return producto;
    }

    public void setProducto(ProductoEntity producto) {
        this.producto = producto;
    }

    public int getUnidadesVendidas() {
        return unidadesVendidas;
    }

    public void setUnidadesVendidas(int unidadesVendidas) {
        this.unidadesVendidas = unidadesVendidas;
    }

    public int getUnidadesExistencia() {
        return unidadesExistencia;
    }

    public void setUnidadesExistencia(int unidadesExistencia) {
        this.unidadesExistencia = unidadesExistencia;
    }
}
