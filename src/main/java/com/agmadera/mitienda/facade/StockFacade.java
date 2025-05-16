package com.agmadera.mitienda.facade;


import com.agmadera.mitienda.models.ProductoDTO;

public interface StockFacade {
    //ProductoDTO actualizarStockProducto(ProductoDTO dto, Long id);
    int calcularStockExistencia(ProductoDTO dto, ProductoDTO productoDtoExistente);
    void inicializarStock(ProductoDTO dto, ProductoDTO productoExistente);

    ProductoDTO actualizarStockVenta(ProductoDTO dto);

}
