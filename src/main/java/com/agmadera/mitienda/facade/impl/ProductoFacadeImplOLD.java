package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.entities.ProductoEntity;
import com.agmadera.mitienda.exceptions.ProductoNoEncontradoException;
import com.agmadera.mitienda.exceptions.ProductoValidacionException;
import com.agmadera.mitienda.exceptions.StockInsuficienteException;
import com.agmadera.mitienda.exceptions.mensajes.ProductoMensajesError;
import com.agmadera.mitienda.facade.PrecioFacade;
import com.agmadera.mitienda.facade.ProductoFacade;
import com.agmadera.mitienda.facade.impl.helper.ProductoActualizacionBuilder;
import com.agmadera.mitienda.facade.impl.helper.ProductoHelper;
import com.agmadera.mitienda.models.CompraVentaDTO;
import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.models.response.ProductoPGResponse;
import com.agmadera.mitienda.models.response.ProductoTecResponse;
import com.agmadera.mitienda.populator.ProductoPopulator;
import com.agmadera.mitienda.services.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

//@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductoFacadeImplOLD implements ProductoFacade {


    private static final String LOGGER_BUSCANDO_NOMBRE = "Buscando con nombre: {}";
    private static final String LOGGER_ACTUALIZANDO_PRODUCTO = "Se inicia actualizacion de producto con id: {}";
    private static final String LOGGER_ACTUALIZANDO_FIN_PRODUCTO = "Se finaliza actualizacion de producto con id: {}";
    private static final String LOGGER_ACTUALIZANDO_COMPATIBLES_PRODUCTO = "Actualizando unicamente compatibles del producto con ID: {}";
    private static final String LOGGER_STOCK_EN_CERO = "stock en 0 del producto: {}";
    private static final String LOGGER_ACTUALIZANDO_STOCK = "Actualizando stock del producto con ID: {}";
    private static final String LOGGER_ERROR_ACTUALIZAR_PRODUCTO ="Error al actualizar el producto";
    private static final String CACHE_NOMBRE_PRODUCTO = "producto";
    private static final String LOGGER_BUSCANDO = "Buscando producto {}: {}";

    //-----------------Dependencias-----------------------
    private final  ProductoService service;
    private final ProductoPopulator populator;
    private final PrecioFacade precioFacade;
    private final ProductoHelper productoHelper;
    private final ProductoActualizacionBuilder actualizacionBuilder;

    //----------------------------CRUD----------------------------------------------
    @Override
    @Transactional
    @CacheEvict(value = CACHE_NOMBRE_PRODUCTO, allEntries = true)
    public ProductoDTO guardarProducto(@Valid ProductoDTO dto) {
        Objects.requireNonNull(dto, ProductoMensajesError.PRODUCTO_NULO);
        if(dto.getId() != null){
            log.info(LOGGER_ACTUALIZANDO_PRODUCTO,dto.getId());
            return actualizarProducto(dto);
        }
        ProductoDTO productoDTONuevo = productoHelper.inicializarProducto(dto);
        return saveProduct(productoDTONuevo);
    }

    @Override
    @Transactional
    public ProductoDTO actualizarProducto(@Valid ProductoDTO dto) {
        try {
            log.info(LOGGER_ACTUALIZANDO_PRODUCTO,dto.getId());
            ProductoEntity productoEntityDb = service.buscarId(dto.getId());
            ProductoDTO productoDTODb = populator.entity2Dto(productoEntityDb);
            ProductoDTO productoActualizado = actualizacionBuilder.determinarTipoActualizacion(dto).buildUpdate(dto, productoDTODb);
            log.info(LOGGER_ACTUALIZANDO_FIN_PRODUCTO, dto.getId());

            return saveProduct(productoActualizado);
        }catch (ProductoNoEncontradoException ex){
            log.error(ProductoMensajesError.PRODUCTO_NO_ENCONTRADO);
            throw new ProductoValidacionException(ProductoMensajesError.PRODUCTO_NO_ENCONTRADO);
        }
    }

    @Override
    @Transactional
    public ProductoDTO actualizarProducto(ProductoDTO dto, Long id) {
        try{
            service.buscarId(id);
            return actualizarProducto(dto);
        }catch (ProductoNoEncontradoException ex){
            log.error(LOGGER_ERROR_ACTUALIZAR_PRODUCTO);
            throw new ProductoValidacionException(ProductoMensajesError.PRODUCTO_NO_ENCONTRADO);
        }
    }

    //------------------------------Opereciones Masivas
    @Override
    @Transactional
    @CacheEvict(value = CACHE_NOMBRE_PRODUCTO, allEntries = true)
    public void cargaMasivaProducto(@Valid List<ProductoDTO> dtoList) {
        Objects.requireNonNull(dtoList, ProductoMensajesError.PRODUCTOS_NULOS);
        List<ProductoEntity> entidades = dtoList.stream()
                .map(productoHelper::inicializarProducto)
                .map(populator::dto2Entity)
                .toList();
        service.cargaMasiva(entidades);

    }
    //---------------------------------Busqueda---------------------------------
    @Override
    @Transactional(readOnly = true)
    public ProductoDTO buscarId(Long id){
        try{
            log.info(LOGGER_BUSCANDO, "por ID",id);
            return populator.entity2Dto(service.buscarId(id));
        }catch (ProductoNoEncontradoException e){
           log.error(ProductoMensajesError.PRODUCTO_NO_ENCONTRADO);
           throw new ProductoNoEncontradoException(ProductoMensajesError.PRODUCTO_NO_ENCONTRADO);
        }
    }
    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarNombre(String nombre) {
        log.info(LOGGER_BUSCANDO_NOMBRE,nombre);
        return populator.listEntity2dto(service.buscarNombre(nombre));
    }
    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> mostrarTodos() {
        return populator.listEntity2dto( service.mostrarTodos());
    }
    @Override
    @CacheEvict(value = CACHE_NOMBRE_PRODUCTO, allEntries = true)
    @Transactional(readOnly = true)
    public List<ProductoTecResponse> buscarNombreTec(String nombre) {
        log.info(LOGGER_BUSCANDO,"prodcuto para tecnico por nombre",nombre);
        return populator.listEntity2ProductoTecResponses(service.buscarNombre(nombre));
    }
    @Override
    @CacheEvict(value = CACHE_NOMBRE_PRODUCTO, allEntries = true)
    @Transactional(readOnly = true)
    public List<ProductoPGResponse> buscarNombrePG(String nombre) {
        log.info(LOGGER_BUSCANDO,"por nombre: ",nombre);
        List<ProductoEntity> productoEntityList = service.buscarNombre(nombre);
        return populator.listEntity2ProductoPGResponses(productoEntityList);
    }
    @Override
    @Transactional(readOnly = true)
    public List<ProductoTecResponse> mostrarTodosTec() {
        List<ProductoEntity> productoEntities = service.mostrarTodos();

        return populator.listEntity2ProductoTecResponses(productoEntities);
    }

    //--------------------Operaciones Especiales
    /*@Override
    @Transactional
    public ProductoDTO actualizarStockVenta(ProductoDTO dto) {
        log.info(LOGGER_ACTUALIZANDO_STOCK,dto.getId());
        ProductoEntity producto = populator.dto2Entity(dto);
        validarStock(producto);
        ProductoEntity productoDb = service.buscarId(producto.getId());
        actualizarStock(productoDb,producto);
        return populator.entity2Dto(service.guardar(productoDb));

    }*/
    @Override
    @Transactional
    public List<ProductoDTO> actualizarPreciosTodos() {
        List<ProductoDTO> productos = mostrarTodos();

        productos.forEach(producto -> {
            CompraVentaDTO nuevoPrecio = crearNuevoPrecio(producto);
            producto.getCompraVentaDTOS().add(nuevoPrecio);
        });

        List<ProductoEntity> entidades = populator.listDto2entities(productos);
        return populator.listEntity2dto(service.cargaMasiva(entidades));

    }
    //-------------------Metodos Privados--------------
    private ProductoDTO saveProduct(ProductoDTO dto) {
        return populator.entity2Dto(service.guardar(populator.dto2Entity(dto)));
    }
    private CompraVentaDTO crearNuevoPrecio(ProductoDTO producto) {
        CompraVentaDTO nuevoPrecio = new CompraVentaDTO();
        precioFacade.configurarPreciosProducto(producto, producto.getCostoReferencia(), nuevoPrecio);
        nuevoPrecio.setCosto(producto.getCostoReferencia());
        nuevoPrecio.setFecha(new Date());
        return nuevoPrecio;
    }
    private void validarStock(ProductoEntity producto) {
        if (producto.getStockEntity().getUnidadesExistencia() < 0) {
            log.error(LOGGER_STOCK_EN_CERO, producto.getId());
            throw new StockInsuficienteException();
        }
    }
    private void actualizarStock(ProductoEntity destino, ProductoEntity origen) {
        destino.getStockEntity().setUnidadesVendidas(origen.getStockEntity().getUnidadesVendidas());
        destino.getStockEntity().setUnidadesExistencia(origen.getStockEntity().getUnidadesExistencia());
    }

}
