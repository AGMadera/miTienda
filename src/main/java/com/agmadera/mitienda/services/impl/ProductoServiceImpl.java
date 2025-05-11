package com.agmadera.mitienda.services.impl;

import com.agmadera.mitienda.entities.ProductoEntity;
import com.agmadera.mitienda.exceptions.ProductoNoEncontradoException;
import com.agmadera.mitienda.exceptions.mensajes.ProductoMensajesError;
import com.agmadera.mitienda.repository.ProductoRepository;
import com.agmadera.mitienda.services.ProductoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
@AllArgsConstructor
public class ProductoServiceImpl implements ProductoService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ProductoServiceImpl.class);


    private final ProductoRepository repository;


    @Override
    @Transactional
    @CacheEvict(value = "productos", allEntries = true)
    public ProductoEntity guardar(@Valid ProductoEntity producto) {
        LOGGER.debug("Guardando producto");
        return repository.save(producto);

    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoEntity> buscarModelo(@Valid String modelo) {
        Objects.requireNonNull(modelo, "El modelo no puede ser nulo");
        return repository.findByNombreContains(modelo)
                .orElseThrow(() -> {
                    String errorMsg = String.format(ProductoMensajesError.PRODUCTO_NO_ENCONTRADO, "modelo", modelo);
                    LOGGER.error(errorMsg);
                    return new ProductoNoEncontradoException(errorMsg);
                });
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "productos", key = "#id")
    public ProductoEntity buscarId( long id) {
        return repository.findById(id).orElseThrow(() -> {
            String errorMsg = String.format(ProductoMensajesError.PRODUCTO_NO_ENCONTRADO, "id", id);
            LOGGER.error(errorMsg);
            throw new ProductoNoEncontradoException(errorMsg);
        });

    }

    @Override
    @Transactional
    @CacheEvict(value = "productos", allEntries = true)
    public List<ProductoEntity> cargaMasiva(@Valid List<ProductoEntity> productoEntityList) {
        Objects.requireNonNull(productoEntityList, ProductoMensajesError.PRODUCTOS_NULOS);
        if (productoEntityList.isEmpty()) {
            LOGGER.warn(ProductoMensajesError.PRODUCTOS_NULOS);
            throw new IllegalArgumentException(ProductoMensajesError.PRODUCTOS_NULOS);
        }
        return repository.saveAll(productoEntityList);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "productos", key = "#nombre")
    public List<ProductoEntity> buscarNombre( String nombre) {
        Objects.requireNonNull(nombre, ProductoMensajesError.NOMBRE_REQUERIDO);
           return repository.findByNombreContainsIgnoreCaseOrCompatiblesNombreContainsIgnoreCase(nombre, nombre)
            .orElseThrow(() -> new ProductoNoEncontradoException(
                    String.format(ProductoMensajesError.PRODUCTO_NO_ENCONTRADO, "nombre", nombre)
            ));

    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoEntity> mostrarTodos() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoEntity> mostrarTodosPaginados(Pageable pageable) {
        Objects.requireNonNull(pageable, "El objeto pageable no puede ser nulo");
        return repository.findAll(pageable);
    }

    @Override
    public Page<ProductoEntity> buscarNombre(String nombre, Pageable pageable) {
        return repository.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    @Override
    @EntityGraph(attributePaths = {"compatibles"})
    public Page<ProductoEntity> buscarNombreOCompatibles(String nombre, Pageable pageable) {
        return repository.findByNombreOrCompatibles(nombre, pageable);
    }

}
