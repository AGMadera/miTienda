package com.agmadera.mitienda.facade;

import com.agmadera.mitienda.models.response.ProductoStockResponse;

import java.util.List;

public interface AnalisisDatosFacade {

    List<ProductoStockResponse> sinStockYDemanda();
    List<ProductoStockResponse> sinStock();
    List<ProductoStockResponse> masVendidos();
}
