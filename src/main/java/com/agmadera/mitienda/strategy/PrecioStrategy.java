package com.agmadera.mitienda.strategy;

public interface PrecioStrategy {
    float calcularPrecioPG(float costo);
    float calcularPrecioTecnico(float costo);
}
