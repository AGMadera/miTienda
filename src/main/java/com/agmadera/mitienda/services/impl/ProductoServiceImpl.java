package com.agmadera.mitienda.services.impl;

import com.agmadera.mitienda.entities.CompraVentaEntity;
import com.agmadera.mitienda.entities.ProductoEntity;
import com.agmadera.mitienda.exceptions.ProductoNoEncontradoException;
import com.agmadera.mitienda.exceptions.ProductoValidacionException;
import com.agmadera.mitienda.repository.ProductoRepository;
import com.agmadera.mitienda.services.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {


    private static final Logger LOGGER = LoggerFactory.getLogger(ProductoServiceImpl.class);

    private static final String MSG_EX_PRODUCTO_NO_ENCONTRADO= "No se encontro producto con id:";
    private static final String MSG_EX_VALID_NOMBRE= "Nombre en blanco o nulo";
    private static final String MSG_EX_VALID_PRECIOS= "Los precios deben de ser mayores a 0";



    //@Autowired
    //ProductoRepository repository;
    private final ProductoRepository repository;
    @Autowired
    public ProductoServiceImpl(ProductoRepository repository){
        this.repository = repository;
    }


    @Override
    @Transactional
    public ProductoEntity guardar(ProductoEntity producto) {
        LOGGER.debug("Validando producto");
        validarProducto(producto);
        return repository.save(producto);

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<ProductoEntity>> buscarModelo(String modelo) {
        return Optional.ofNullable(repository.findByNombreContains(modelo).orElseThrow(() -> new ProductoNoEncontradoException(MSG_EX_PRODUCTO_NO_ENCONTRADO+modelo)));

    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductoEntity> buscarId(long id) {
        return Optional.ofNullable(repository.findById(id).orElseThrow(() -> new ProductoNoEncontradoException(MSG_EX_PRODUCTO_NO_ENCONTRADO+id)));

    }

    @Override
    @Transactional
    public List<ProductoEntity> cargaMasiva(List<ProductoEntity> productoEntityList) {
            return repository.saveAll(productoEntityList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoEntity> buscarNombre(String nombre) {
        //List<ProductoEntity> productoEntities = repository.findByNombreContains(nombre).get();
        //List<ProductoEntity> productoEntities = repository.findByNombreContainsIgnoreCaseOrCompatiblesNombreContainsIgnoreCase(nombre,nombre).get();
        //return productoEntities;
        return repository.findByNombreContainsIgnoreCaseOrCompatiblesNombreContainsIgnoreCase(nombre,nombre).orElseThrow(ProductoNoEncontradoException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoEntity> mostrarTodos() {
        return repository.findAll();
    }

    private void validarProducto(ProductoEntity producto){
        if (producto.getNombre() == null || producto.getNombre().isBlank()){
            throw new ProductoValidacionException(MSG_EX_VALID_NOMBRE);
        }
        CompraVentaEntity ultimaCompraVenta = producto.getCompraVentaEntity().get(producto.getCompraVentaEntity().size() - 1);
        if (ultimaCompraVenta.getVentaPG() <= 0 ||ultimaCompraVenta.getVentaTecnico() <= 0){
            throw new ProductoValidacionException("");
        }

    }

}
