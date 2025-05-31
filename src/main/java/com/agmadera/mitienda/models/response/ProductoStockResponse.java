package com.agmadera.mitienda.models.response;

import lombok.Data;

@Data
public class ProductoStockResponse {
    private String nombre;
    private String calidad;
    private boolean marco;
    private float costoReferencia;
}
