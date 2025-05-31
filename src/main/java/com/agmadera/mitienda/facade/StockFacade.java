package com.agmadera.mitienda.facade;


import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.models.ProductoVentaDTO;

public interface StockFacade {
    //ProductoDTO actualizarStockProducto(ProductoDTO dto, Long id);
    int calcularStockExistencia(ProductoDTO dto, ProductoDTO productoDtoExistente);
    void inicializarStock(ProductoDTO dto, ProductoDTO productoExistente);
    ProductoDTO actualizarStockVenta(ProductoDTO dto);
    void validarExistenciaStock(ProductoDTO productoDTO, ProductoVentaDTO productoVentaDTO);
    void registrarMerma(Long idProducto);

}
