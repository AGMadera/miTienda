package com.agmadera.mitienda.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class GarantiaVale extends Garantia{
    private String folio;
    private float saldoInicial;
    private float saldoActual;
    @Version
    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long version = 0L;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "historial_garantia_vale", referencedColumnName = "id")
    private List<HistorialGarantiaVale> historialVales;

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public float getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(float saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public float getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(float saldoActual) {
        this.saldoActual = saldoActual;
    }

    public List<HistorialGarantiaVale> getHistorialVales() {
        return historialVales;
    }

    public void setHistorialVales(List<HistorialGarantiaVale> historialVales) {
        this.historialVales = historialVales;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
