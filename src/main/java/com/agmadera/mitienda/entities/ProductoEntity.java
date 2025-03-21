package com.agmadera.mitienda.entities;

import jakarta.persistence.*;

import java.util.List;


@Entity
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nombre;
    private String marca;
    private String calidad;
    private boolean marco;
    private String color;

    private float costoReferencia;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "compatible_id", referencedColumnName = "id")
    private List<CompatibleEntity> compatibles;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "stock_id", referencedColumnName = "id")
    private StockEntity stockEntity;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "compatible_id", referencedColumnName = "id")
    private List<CompraVentaEntity> compraVentaEntity;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "historialStock_id", referencedColumnName = "id")
    private List<HistorialStockEntity> historialStockEntity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCalidad() {
        return calidad;
    }

    public void setCalidad(String calidad) {
        this.calidad = calidad;
    }

    public boolean isMarco() {
        return marco;
    }

    public void setMarco(boolean marco) {
        this.marco = marco;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public float getCostoReferencia() {
        return costoReferencia;
    }

    public void setCostoReferencia(float costoReferencia) {
        this.costoReferencia = costoReferencia;
    }

    public List<CompatibleEntity> getCompatibles() {
        return compatibles;
    }

    public void setCompatibles(List<CompatibleEntity> compatibles) {
        this.compatibles = compatibles;
    }

    public StockEntity getStockEntity() {
        return stockEntity;
    }

    public void setStockEntity(StockEntity stockEntity) {
        this.stockEntity = stockEntity;
    }

    public List<CompraVentaEntity> getCompraVentaEntity() {
        return compraVentaEntity;
    }

    public void setCompraVentaEntity(List<CompraVentaEntity> compraVentaEntity) {
        this.compraVentaEntity = compraVentaEntity;
    }

    public List<HistorialStockEntity> getHistorialStockEntity() {
        return historialStockEntity;
    }

    public void setHistorialStockEntity(List<HistorialStockEntity> historialStockEntity) {
        this.historialStockEntity = historialStockEntity;
    }
}
