package com.agmadera.mitienda.models;

import java.util.Date;

public class HistorialStockDTO {
    private Long id;
    private Date fecha;
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
