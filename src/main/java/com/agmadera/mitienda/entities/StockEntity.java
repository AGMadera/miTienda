package com.agmadera.mitienda.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "stock")
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private ProductoEntity producto;

    @Column(nullable = false)
    private int unidadesVendidas;
    @Column(nullable = false)
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
