package com.agmadera.mitienda.facade.impl.handlers;

import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.models.ProductoVentaDTO;

import java.util.List;

public interface ProductoVentaHandler {
    ProductoVentaDTO procesarProductoOrden(ProductoVentaDTO productoVenta, boolean esTecnico);
    void validarYActualizarStock(ProductoDTO producto, ProductoVentaDTO productoVenta);
    void procesarListaProductosEnTransaccion(List<ProductoVentaDTO> productos);

}
