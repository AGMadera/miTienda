package com.agmadera.mitienda.services;

import com.agmadera.mitienda.entities.ProductoEntity;
import com.agmadera.mitienda.entities.StockEntity;

public interface StockService {
    StockEntity guardarStock(StockEntity stock);
    StockEntity actualizar(StockEntity stock);
    StockEntity buscarStock(ProductoEntity producto);
}
