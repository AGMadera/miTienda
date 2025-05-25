package com.agmadera.mitienda.strategies.impl;

import com.agmadera.mitienda.models.VentaDTO;
import com.agmadera.mitienda.strategies.PagoStrategy;


public class PagoTerminalStrategy implements PagoStrategy {
    @Override
    public float calcularDescuento(VentaDTO ventaDTO) {
        return ventaDTO.getDescuentosEnTotalGen();
    }

    @Override
    public void validar(VentaDTO ventaDTO) {
        return;
    }
}
