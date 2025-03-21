package com.agmadera.mitienda.models;

import java.util.Date;

public class CompraVentaDTO {
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
