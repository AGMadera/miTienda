package com.agmadera.mitienda.services.impl;

import com.agmadera.mitienda.entities.ProductoVentaEntity;
import com.agmadera.mitienda.repository.ProductoVentaRepository;
import com.agmadera.mitienda.services.ProductoVentaService;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductoVentaServiceImpl implements ProductoVentaService {

    @Autowired
    private ProductoVentaRepository repository;

    @Override
    public ProductoVentaEntity guardarVenta(ProductoVentaEntity productoVentaEntity) {
        return repository.save(productoVentaEntity);

    }
}
