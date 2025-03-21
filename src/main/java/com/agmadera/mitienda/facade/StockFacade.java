package com.agmadera.mitienda.facade;


import com.agmadera.mitienda.models.ProductoDTO;

public interface StockFacade {
    ProductoDTO actualizarStockProducto(ProductoDTO dto, Long id);

}
