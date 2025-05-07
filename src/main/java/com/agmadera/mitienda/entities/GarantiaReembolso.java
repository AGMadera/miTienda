package com.agmadera.mitienda.entities;

import jakarta.persistence.Entity;

@Entity
public class GarantiaReembolso extends Garantia{
    //Este tipo de garantia solo se aplica para tecnico de la tienda, es decir merma del producto

    private float cantidadReembolso;

    public float getCantidadReembolso() {
        return cantidadReembolso;
    }

    public void setCantidadReembolso(float cantidadReembolso) {
        this.cantidadReembolso = cantidadReembolso;
    }
}
