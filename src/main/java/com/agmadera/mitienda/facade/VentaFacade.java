package com.agmadera.mitienda.facade;

import com.agmadera.mitienda.entities.VentaEntity;
import com.agmadera.mitienda.models.VentaDTO;
import com.agmadera.mitienda.models.request.VentaRequest;

public interface VentaFacade {
    VentaDTO guardarVenta(VentaDTO ventaDTO);
    VentaDTO orden(VentaRequest ventaRequest);
    VentaDTO buscarVenta(Long id);
}
