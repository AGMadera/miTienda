package com.agmadera.mitienda.strategies;

import com.agmadera.mitienda.models.VentaDTO;

public interface PagoStrategy {
    float calcularDescuento(VentaDTO ventaDTO);

    default void validar(VentaDTO ventaDTO) {
        // Validaciones básicas por defecto
    }
}
