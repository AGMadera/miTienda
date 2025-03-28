package com.agmadera.mitienda.services.impl;

import com.agmadera.mitienda.entities.ProductoEntity;
import com.agmadera.mitienda.repository.ProductoRepository;
import com.agmadera.mitienda.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    ProductoRepository repository;


    @Override
    public ProductoEntity guardar(ProductoEntity producto) {


        return repository.save(producto);

    }

    @Override
    public Optional<List<ProductoEntity>> buscarModelo(String modelo) {
        return repository.findByNombreContains(modelo);

    }

    @Override
    public Optional<ProductoEntity> buscarId(long id) {
        return repository.findById(id);
    }

    @Override
    public List<ProductoEntity> cargaMasiva(List<ProductoEntity> productoEntityList) {
            return repository.saveAll(productoEntityList);
    }

    @Override
    public List<ProductoEntity> buscarNombre(String nombre) {
        //List<ProductoEntity> productoEntities = repository.findByNombreContains(nombre).get();
        List<ProductoEntity> productoEntities = repository.findByNombreContainsIgnoreCaseOrCompatiblesNombreContainsIgnoreCase(nombre,nombre).get();
        return productoEntities;
    }

    @Override
    public List<ProductoEntity> mostrarTodos() {
        return repository.findAll();
    }

}
