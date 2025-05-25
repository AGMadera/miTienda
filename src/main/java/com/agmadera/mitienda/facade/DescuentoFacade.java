package com.agmadera.mitienda.facade;

import com.agmadera.mitienda.models.VentaDTO;

public interface DescuentoFacade {
    float calcularDescuento(VentaDTO ventaDTO);
}
