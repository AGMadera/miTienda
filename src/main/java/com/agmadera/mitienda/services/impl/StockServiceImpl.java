package com.agmadera.mitienda.services.impl;

import com.agmadera.mitienda.entities.ProductoEntity;
import com.agmadera.mitienda.entities.StockEntity;
import com.agmadera.mitienda.repository.StockRepository;
import com.agmadera.mitienda.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    StockRepository repository;

    @Override
    public StockEntity guardarStock(StockEntity stock) {
        return repository.save(stock);

    }

    @Override
    public StockEntity actualizar(StockEntity stock) {
        return null;
    }

    @Override
    public StockEntity buscarStock(ProductoEntity producto) {
        return repository.findByProducto(producto);
    }
}
