package com.agmadera.mitienda.services.impl;

import com.agmadera.mitienda.entities.ProductoVentaEntity;
import com.agmadera.mitienda.entities.VentaEntity;
import com.agmadera.mitienda.repository.VentaRepository;

import com.agmadera.mitienda.services.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository repository;

    @Override
    public VentaEntity guardarVenta(VentaEntity productoVentaEntity) {
        return repository.save(productoVentaEntity);

    }

}
