package com.agmadera.mitienda.facade;

import com.agmadera.mitienda.entities.VentaEntity;
import com.agmadera.mitienda.models.VentaDTO;

public interface VentaFacade {
    VentaDTO guardarVenta(VentaDTO ventaDTO);
}
