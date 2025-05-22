package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.entities.ProductoEntity;
import com.agmadera.mitienda.exceptions.ProductoNoEncontradoException;
import com.agmadera.mitienda.exceptions.ProductoValidacionException;
import com.agmadera.mitienda.exceptions.mensajes.ProductoMensajesError;
import com.agmadera.mitienda.facade.PrecioFacade;
import com.agmadera.mitienda.facade.ProductoFacade;
import com.agmadera.mitienda.facade.impl.helper.ProductoActualizacionBuilder;
import com.agmadera.mitienda.facade.impl.helper.ProductoHelper;
import com.agmadera.mitienda.models.CompraVentaDTO;
import com.agmadera.mitienda.models.HistorialStockDTO;
import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.models.response.ProductoPGResponse;
import com.agmadera.mitienda.models.response.ProductoTecResponse;
import com.agmadera.mitienda.populator.ProductoPopulator;
import com.agmadera.mitienda.services.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductoFacadeImpl implements ProductoFacade {

    // -------------------- CONSTANTES --------------------
    private static final String LOGGER_BUSCANDO = "Buscando producto {}: {}";
    private static final String LOGGER_ACTUALIZACION_INICIO = "Iniciando actualización producto ID: {}";
    private static final String LOGGER_ACTUALIZACION_FIN = "Finalizada actualización producto ID: {}";
    //private static final String LOGGER_STOCK_INSUFICIENTE = "Stock insuficiente para producto ID: {}";
    private static final String CACHE_PRODUCTOS = "productos";

    // -------------------- DEPENDENCIAS --------------------
    private final ProductoService service;
    private final ProductoPopulator populator;
    private final PrecioFacade precioFacade;
    private final ProductoHelper productoHelper;
    private final ProductoActualizacionBuilder actualizacionBuilder;

    // -------------------- OPERACIONES CRUD --------------------
    @Override
    @Transactional
    @CacheEvict(value = CACHE_PRODUCTOS, key = "#dto.id")
    public ProductoDTO guardarProducto(@Valid ProductoDTO dto) {
        Objects.requireNonNull(dto, ProductoMensajesError.PRODUCTO_NULO);

        return dto.getId() != null
                ? actualizarProducto(dto)
                : crearNuevoProducto(dto);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_PRODUCTOS, key = "#dto.id")
    public ProductoDTO actualizarProducto(@Valid ProductoDTO dto) {
        try {
            log.info(LOGGER_ACTUALIZACION_INICIO, dto.getId());
            ProductoDTO productoActualizado = procesarActualizacion(dto);
            log.info(LOGGER_ACTUALIZACION_FIN, dto.getId());
            return productoActualizado;
        } catch (ProductoNoEncontradoException ex) {
            log.error(ProductoMensajesError.PRODUCTO_NO_ENCONTRADO);
            throw new ProductoValidacionException(ProductoMensajesError.PRODUCTO_NO_ENCONTRADO);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_PRODUCTOS, key = "#dto.id")
    public ProductoDTO actualizarProducto(ProductoDTO dto, Long id) {
        try {
            service.buscarId(id);
            return actualizarProducto(dto);
        } catch (ProductoNoEncontradoException ex) {
            log.error("Error al actualizar producto con ID: {}", id);
            throw new ProductoValidacionException(ProductoMensajesError.PRODUCTO_NO_ENCONTRADO);
        }
    }

    // -------------------- OPERACIONES MASIVAS --------------------
    @Override
    @Transactional
    @CacheEvict(value = CACHE_PRODUCTOS, allEntries = true)
    public void cargaMasivaProducto(@Valid List<ProductoDTO> dtos) {
        Objects.requireNonNull(dtos, ProductoMensajesError.PRODUCTOS_NULOS);

        if(dtos.isEmpty()) {
            log.warn(ProductoMensajesError.PRODUCTOS_NULOS);
            return;
        }
        try{
            List<ProductoEntity> entidades = dtos.stream()
                    .map(productoHelper::inicializarProducto)
                    .map(populator::dto2Entity)
                    .toList();

            service.cargaMasiva(entidades);
        } catch (Exception e) {
            log.error("Error en carga masiva de productos: {}", e.getMessage());
            throw new ProductoValidacionException("Error al procesar carga masiva: " + e.getMessage());
        }
    }

    // -------------------- CONSULTAS --------------------
    @Override
    @Transactional(readOnly = true)
    public ProductoDTO buscarId(Long id) {
        try {
            log.info(LOGGER_BUSCANDO, "por ID", id);
            return populator.entity2Dto(service.buscarId(id));
        } catch (ProductoNoEncontradoException e) {
            log.error(ProductoMensajesError.PRODUCTO_NO_ENCONTRADO);
            throw new ProductoNoEncontradoException(ProductoMensajesError.PRODUCTO_NO_ENCONTRADO);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> buscarNombre(String nombre) {
        log.info(LOGGER_BUSCANDO, "por nombre", nombre);
        return populator.listEntity2dto(service.buscarNombre(nombre));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> mostrarTodos() {
        return populator.listEntity2dto(service.mostrarTodos());
    }

    // -------------------- CONSULTAS ESPECIALIZADAS --------------------
    @Override
    @CacheEvict(value = CACHE_PRODUCTOS, allEntries = true)
    @Transactional(readOnly = true)
    public List<ProductoTecResponse> buscarNombreTec(String nombre) {
        log.info(LOGGER_BUSCANDO, "técnico por nombre", nombre);
        return populator.listEntity2ProductoTecResponses(service.buscarNombre(nombre));
    }

    @Override
    @CacheEvict(value = CACHE_PRODUCTOS, allEntries = true)
    @Transactional(readOnly = true)
    public List<ProductoPGResponse> buscarNombrePG(String nombre) {
        log.info(LOGGER_BUSCANDO, "PG por nombre", nombre);
        return populator.listEntity2ProductoPGResponses(service.buscarNombre(nombre));
    }

    @Override
    @Transactional(readOnly = true)
    @CacheEvict(value = CACHE_PRODUCTOS, allEntries = true)
    public List<ProductoTecResponse> mostrarTodosTec() {
        return populator.listEntity2ProductoTecResponses(service.mostrarTodos());
    }

    // -------------------- OPERACIONES ESPECIALES --------------------
   /* @Override
    @Transactional
    public ProductoDTO actualizarStockVenta(ProductoDTO dto) {
        log.info("Actualizando stock producto ID: {}", dto.getId());
        ProductoEntity producto = populator.dto2Entity(dto);
        validarStock(producto);
        ProductoEntity productoDb = service.buscarId(producto.getId());
        actualizarStock(productoDb, producto);

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

    // -------------------- MÉTODOS PRIVADOS --------------------
    private ProductoDTO crearNuevoProducto(ProductoDTO dto) {
        ProductoDTO productoInicializado = productoHelper.inicializarProducto(dto);
        return guardarYConvertir(productoInicializado);
    }

    private ProductoDTO procesarActualizacion(ProductoDTO dto) {
        ProductoEntity entityDb = service.buscarId(dto.getId());
        ProductoDTO dbDto = populator.entity2Dto(entityDb);
        ProductoDTO productoActualizado = actualizacionBuilder
                .determinarTipoActualizacion(dto)
                .buildUpdate(dto, dbDto);
        return guardarYConvertir(productoActualizado);
    }

    private ProductoDTO guardarYConvertir(ProductoDTO dto) {
        validarYCrearFechas(dto);
        return populator.entity2Dto(service.guardar(populator.dto2Entity(dto)));
    }
    private void validarYCrearFechas(ProductoDTO dto){
        List<HistorialStockDTO> historialStockDTOS = dto.getHistorialStockDTOS();
        historialStockDTOS.forEach(historialStockDTO -> {
            if(historialStockDTO.getFecha() == null){
                historialStockDTO.setFecha(new Date());
            }
        });
        List<CompraVentaDTO> compraVentaDTOS = dto.getCompraVentaDTOS();
        compraVentaDTOS.forEach(compraVentaDTO -> {
            if(compraVentaDTO.getFecha()==null){
                compraVentaDTO.setFecha(new Date());
            }
        });

    }
    /*private void validarStock(ProductoEntity producto) {
        if (producto.getStockEntity().getUnidadesExistencia() < 0) {
            log.error(LOGGER_STOCK_INSUFICIENTE, producto.getId());
            throw new StockInsuficienteException();
        }
    }

    private void actualizarStock(ProductoEntity destino, ProductoEntity origen) {
        destino.getStockEntity().setUnidadesVendidas(origen.getStockEntity().getUnidadesVendidas());
        destino.getStockEntity().setUnidadesExistencia(origen.getStockEntity().getUnidadesExistencia());
    }*/

    private CompraVentaDTO crearNuevoPrecio(ProductoDTO producto) {
        CompraVentaDTO nuevoPrecio = new CompraVentaDTO();
        precioFacade.configurarPreciosProducto(producto, producto.getCostoReferencia(), nuevoPrecio);
        nuevoPrecio.setCosto(producto.getCostoReferencia());
        nuevoPrecio.setFecha(new Date());
        return nuevoPrecio;
    }
}
