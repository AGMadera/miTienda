package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.Strategy.PrecioStrategy;
import com.agmadera.mitienda.entities.ProductoEntity;
import com.agmadera.mitienda.exceptions.ProductoNoEncontradoException;
import com.agmadera.mitienda.exceptions.StockInsuficienteException;
import com.agmadera.mitienda.facade.ProductoFacade;
import com.agmadera.mitienda.helpers.CSVCargaMasivaHelper;
import com.agmadera.mitienda.models.CompraVentaDTO;
import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.models.StockDTO;
import com.agmadera.mitienda.models.response.ProductoPGResponse;
import com.agmadera.mitienda.models.response.ProductoTecResponse;
import com.agmadera.mitienda.populator.ProductoPopulator;
import com.agmadera.mitienda.services.ProductoService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class ProductoFacadeImpl implements ProductoFacade {

    private static final Logger logger = LoggerFactory.getLogger(ProductoFacadeImpl.class);
    private static final String MENSAJE_EXCEPTION_NO_ENCONTRADO= "producto con id no encontrado, ID: ";
    private static final String LOGGER_BUSCANDO_NOMBRE = "Buscando con nombre: {}";
    private static final String LOGGER_ACTUALIZANDO_PRODUCTO = "Se inicia actualizacion de producto con id: {}";
    private static final String LOGGER_ACTUALIZANDO_FIN_PRODUCTO = "Se finaliza actualizacion de producto con id: {}";
    private static final String LOGGER_ACTUALIZANDO_COMPATIBLES_PRODUCTO = "Actualizando unicamente compatibles del producto con ID: {}";
    private static final String LOGGER_STOCK_EN_CERO = "stock en 0";
    private static final String LOGGER_ACTUALIZANDO_STOCK = "Actualizando stock del producto con ID: {}";
    @Autowired
    private ProductoService service;
    //private final ProductoService service = new ProductoServiceImpl();
    @Autowired
    private ProductoPopulator populator;

    @Qualifier("margenFijoStrategy")
    @Autowired
    private PrecioStrategy precioStrategy;
    

    @Override
    @Transactional
    public ProductoDTO guardarProducto(ProductoDTO dto) {
        if(dto.getId() != null){
            logger.info("Producto a actaulizar {}",dto.getId());
            //actualizacion
            return actualizarProducto(dto);
        }
        /*ProductoDTO productoDTO = crearNuevoProductoConStock(dto);
        ProductoEntity productoEntity= populator.dto2Entity(productoDTO);
        return populator.entity2Dto(service.guardar(productoEntity));*/
        return populator.entity2Dto(service.guardar(populator.dto2Entity(crearNuevoProductoConStock(dto))));
    }

    @Override
    public void cargaMasivaProducto(MultipartFile archivo) {
        //TODO
        if (!CSVCargaMasivaHelper.hasCSVFormat(archivo)){
            //TODO archivo no cumple el formato
        }

        try {
            List<ProductoEntity> productoEntityList = CSVCargaMasivaHelper.csv2Productos(archivo.getInputStream());
            service.cargaMasiva(productoEntityList);
        }catch (IOException e){
            throw new RuntimeException("Error al cargar el archivo: "+e.getMessage());
        }
    }

    @Override
    @Transactional
    public void cargaMasivaProducto(List<ProductoDTO> dtoList) {

        for (ProductoDTO productoTempDto: dtoList){
            crearNuevoProductoConStock(productoTempDto);
        }
        List<ProductoEntity> productoEntityList = populator.listDto2entities(dtoList);
        service.cargaMasiva(productoEntityList);

    }

    @Override
    public ProductoDTO buscarId(Long id) {
        logger.info(LOGGER_BUSCANDO_NOMBRE,id);
        ProductoEntity productoEntityFind = service.buscarId(id).orElseThrow(() -> new ProductoNoEncontradoException(MENSAJE_EXCEPTION_NO_ENCONTRADO+id));

        return populator.entity2Dto(productoEntityFind);
    }

    @Override
    public List<ProductoDTO> buscarNombre(String nombre) {
        logger.info(LOGGER_BUSCANDO_NOMBRE+nombre);
        List<ProductoEntity> productoEntityList = service.buscarNombre(nombre);
        return populator.listEntity2dto(productoEntityList);
    }

    @Override
    public List<ProductoDTO> mostrarTodos() {
        List<ProductoEntity> productoEntities = service.mostrarTodos();
        return populator.listEntity2dto(productoEntities);
    }

    @Override
    @Cacheable (value = "producto", key = "#nombre")
    public List<ProductoTecResponse> buscarNombreTec(String nombre) {
        logger.info(LOGGER_BUSCANDO_NOMBRE+nombre);
        List<ProductoEntity> productoEntityList = service.buscarNombre(nombre);
        return populator.listEntity2ProductoTecResponses(productoEntityList);
    }

    @Override
    @Transactional
    public ProductoDTO actualizarProducto(ProductoDTO dto) {

        logger.info(LOGGER_ACTUALIZANDO_PRODUCTO,dto.getId());

        ProductoEntity productoEntityDb = service.buscarId(dto.getId()).orElseThrow(() -> new ProductoNoEncontradoException(MENSAJE_EXCEPTION_NO_ENCONTRADO+dto.getId())); //Se recupera de la DB

        ProductoDTO productoDTODb = populator.entity2Dto(productoEntityDb); //Se convierte a DTODb

        try {

            productoDTODb.getHistorialStockDTOS().add(dto.getHistorialStockDTOS().get(0)); //Se adiere historialStock de DTO a List del DTODb

        }catch (Exception ex){
            logger.info(LOGGER_ACTUALIZANDO_COMPATIBLES_PRODUCTO,dto.getId());
            productoDTODb.getCompatibles().addAll(dto.getCompatibles());
            ProductoEntity productoEntity = populator.dto2Entity(productoDTODb);

            return populator.entity2Dto(service.guardar(productoEntity));
        }

        if(dto.getCompatibles()!=null||!dto.getCompatibles().isEmpty()){
            productoDTODb.getCompatibles().addAll(dto.getCompatibles());
        }

        CompraVentaDTO compraVentaDTO = dto.getCompraVentaDTOS().get(0); //

        float costo = productoDTODb.getCostoReferencia();

        if(!dto.isUsarCostoReferencia()){
           costo = dto.getCompraVentaDTOS().get(0).getCosto();
           productoDTODb.setCostoReferencia(costo);
        }
        productoDTODb.setUsarCostoReferencia(dto.isUsarCostoReferencia());

        float precioPG = precioStrategy.calcularPrecioPG(costo);
        float precioTecnico = precioStrategy.calcularPrecioTecnico(costo);

        compraVentaDTO.setVentaPG(precioPG);
        compraVentaDTO.setVentaTecnico(precioTecnico);

        productoDTODb.getCompraVentaDTOS().add(compraVentaDTO);

        int unidadesIngresadas = dto.getHistorialStockDTOS().get(0).getUnidadesIngresadas();
        int unidadesExistencia = productoDTODb.getStockDTO().getUnidadesExistencia();

        productoDTODb.getStockDTO().setUnidadesExistencia(unidadesExistencia + unidadesIngresadas);

        ProductoEntity productoEntity= populator.dto2Entity(productoDTODb);
        logger.info(LOGGER_ACTUALIZANDO_FIN_PRODUCTO,productoEntity.getId());
        return populator.entity2Dto(service.guardar(productoEntity));
    }

    @Override
    @Cacheable (value = "producto", key = "#nombre")
    public List<ProductoPGResponse> buscarNombrePG(String nombre) {
        logger.info(LOGGER_BUSCANDO_NOMBRE+nombre);
        List<ProductoEntity> productoEntityList = service.buscarNombre(nombre);
        return populator.listEntity2ProductoPGResponses(productoEntityList);
    }

    @Override
    @Transactional
    public ProductoDTO actualizarStockVenta(ProductoDTO dto) {
        logger.info(LOGGER_ACTUALIZANDO_STOCK,dto.getId());
        ProductoEntity producto = populator.dto2Entity(dto);
        if(producto.getStockEntity().getUnidadesExistencia() < 0){
            logger.error(LOGGER_STOCK_EN_CERO);
            throw new StockInsuficienteException();
        }

        Optional<ProductoEntity> productoEntityOptional = service.buscarId(producto.getId());
        if (productoEntityOptional.isEmpty()){
            logger.error(MENSAJE_EXCEPTION_NO_ENCONTRADO+producto.getId());
            throw new ProductoNoEncontradoException(MENSAJE_EXCEPTION_NO_ENCONTRADO+producto.getId());
        }
        ProductoEntity productoDb = productoEntityOptional.get();
        productoDb.getStockEntity().setUnidadesVendidas(producto.getStockEntity().getUnidadesVendidas());
        productoDb.getStockEntity().setUnidadesExistencia(producto.getStockEntity().getUnidadesExistencia());

        return populator.entity2Dto(service.guardar(productoDb));

    }

    @Override
    @Transactional
    public ProductoDTO actualizarProducto(ProductoDTO dto, Long id) {
        Optional<ProductoEntity> productoEntityOptional = service.buscarId(id);
        if(productoEntityOptional.isEmpty()){
            throw new ProductoNoEncontradoException(MENSAJE_EXCEPTION_NO_ENCONTRADO +id);
        }
        return actualizarProducto(dto);
    }

    @Override
    public List<ProductoTecResponse> mostrarTodosTec() {
        List<ProductoEntity> productoEntities = service.mostrarTodos();

        return populator.listEntity2ProductoTecResponses(productoEntities);
    }

    @Override
    @Transactional
    public List<ProductoDTO> actualizarPrecios() {
        List<ProductoDTO> productoDTOS = mostrarTodos();
        productoDTOS.forEach(productoDTO ->{
            CompraVentaDTO nuevoCompraVentaDTO = new CompraVentaDTO();
            nuevoCompraVentaDTO.setVentaPG(precioStrategy.calcularPrecioPG(productoDTO.getCostoReferencia()));
            nuevoCompraVentaDTO.setVentaTecnico(precioStrategy.calcularPrecioTecnico(productoDTO.getCostoReferencia()));
            nuevoCompraVentaDTO.setCosto(productoDTO.getCostoReferencia());
            nuevoCompraVentaDTO.setFecha(new Date());
            productoDTO.getCompraVentaDTOS().add(nuevoCompraVentaDTO);
        });
        List<ProductoEntity> productoEntityList = service.cargaMasiva(populator.listDto2entities(productoDTOS));
        return populator.listEntity2dto(productoEntityList);
    }

    private int ingresarStock(ProductoDTO dto){
        int unidadesIngresadas;
        int unidadesExistencia;
        if(dto.getHistorialStockDTOS()==null||dto.getHistorialStockDTOS().isEmpty()){
            unidadesIngresadas = 0;
            unidadesExistencia = dto.getHistorialStockDTOS().get(dto.getHistorialStockDTOS().size()-1).getUnidadesIngresadas();
        }else {
            unidadesIngresadas = dto.getHistorialStockDTOS().get(dto.getHistorialStockDTOS().size() - 1).getUnidadesIngresadas();
            unidadesExistencia = dto.getStockDTO().getUnidadesExistencia();
        }

        return unidadesIngresadas + unidadesExistencia;
    }


    private ProductoDTO crearNuevoProductoConStock(ProductoDTO dto){
        StockDTO stockDTO = new StockDTO();
        dto.setStockDTO(stockDTO);
        dto.getStockDTO().setUnidadesExistencia(ingresarStock(dto));

        float costo = dto.getCompraVentaDTOS().get(dto.getCompraVentaDTOS().size() - 1).getCosto();

        float precioPG = precioStrategy.calcularPrecioPG(costo);
        float precioTecnico = precioStrategy.calcularPrecioTecnico(costo);

        dto.getCompraVentaDTOS().get(dto.getCompraVentaDTOS().size()-1).setVentaTecnico(precioTecnico);
        dto.getCompraVentaDTOS().get(dto.getCompraVentaDTOS().size()-1).setVentaPG(precioPG);

        dto.setCostoReferencia(costo);
        return dto;
    }


}
