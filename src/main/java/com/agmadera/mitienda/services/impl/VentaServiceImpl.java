package com.agmadera.mitienda.services.impl;

import com.agmadera.mitienda.entities.ProductoVentaEntity;
import com.agmadera.mitienda.entities.VentaEntity;
import com.agmadera.mitienda.exceptions.VentaNoEncontradaException;
import com.agmadera.mitienda.models.VentaDTO;
import com.agmadera.mitienda.repository.VentaRepository;

import com.agmadera.mitienda.services.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository repository;

    @Override
    public VentaEntity guardarVenta(VentaEntity productoVentaEntity) {
        return repository.save(productoVentaEntity);

    }

    @Override
    public VentaEntity buscarVenta(Long id) {
        Optional<VentaEntity> optionalVentaEntity = repository.findById(id);
        if (optionalVentaEntity.isEmpty()){
            throw new VentaNoEncontradaException();
        }
        return optionalVentaEntity.get();
    }

}
