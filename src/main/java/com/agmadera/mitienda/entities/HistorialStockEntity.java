package com.agmadera.mitienda.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class HistorialStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Date fecha;
    @Column(nullable = false)
    private int unidadesIngresadas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getUnidadesIngresadas() {
        return unidadesIngresadas;
    }

    public void setUnidadesIngresadas(int unidadesIngresadas) {
        this.unidadesIngresadas = unidadesIngresadas;
    }
}
