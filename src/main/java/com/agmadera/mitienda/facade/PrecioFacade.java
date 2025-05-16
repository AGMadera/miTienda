package com.agmadera.mitienda.facade;

import com.agmadera.mitienda.models.CompraVentaDTO;
import com.agmadera.mitienda.models.ProductoDTO;

public interface PrecioFacade {
    void configurarPreciosProducto(ProductoDTO producto, float costo, CompraVentaDTO compraVentaDTO);
    float obtenerUltimoCosto(ProductoDTO producto);
    float obtenerCostoReferencia(ProductoDTO dto, ProductoDTO productoDTODb);

}
