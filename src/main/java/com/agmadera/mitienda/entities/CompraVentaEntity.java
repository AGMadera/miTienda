package com.agmadera.mitienda.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class CompraVentaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private float costo;
    private float ventaTecnico;
    private float ventaPG;
    private Date fecha;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getCosto() {
        return costo;
    }

    public void setCosto(float costo) {
        this.costo = costo;
    }

    public float getVentaTecnico() {
        return ventaTecnico;
    }

    public void setVentaTecnico(float ventaTecnico) {
        this.ventaTecnico = ventaTecnico;
    }

    public float getVentaPG() {
        return ventaPG;
    }

    public void setVentaPG(float ventaPG) {
        this.ventaPG = ventaPG;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
