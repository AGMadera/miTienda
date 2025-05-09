package com.agmadera.mitienda.facade.impl;

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
import com.agmadera.mitienda.strategy.PrecioStrategy;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
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
    private static final String LOGGER_PRODUCTO_NO_ENCONTRADO= "producto con id no encontrado, ID: {}";
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
    @Autowired
    @Qualifier("margenFijoStrategy")
    private PrecioStrategy precioStrategy;
    

    @Override
    @Transactional
    public ProductoDTO guardarProducto(ProductoDTO dto) {
        if(dto.getId() != null){
            logger.info(LOGGER_ACTUALIZANDO_PRODUCTO,dto.getId());
            //actualizacion
            return actualizarProducto(dto);
        }
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

        dtoList.forEach(this::crearNuevoProductoConStock);
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
        logger.info(LOGGER_BUSCANDO_NOMBRE,nombre);
        List<ProductoEntity> productoEntityList = service.buscarNombre(nombre);
        return populator.listEntity2dto(productoEntityList);
    }

    @Override
    public List<ProductoDTO> mostrarTodos() {
        List<ProductoEntity> productoEntities = service.mostrarTodos();
        return populator.listEntity2dto(productoEntities);
    }

    @Override
    @CacheEvict(value = {"producto"}, allEntries = true)
    public List<ProductoTecResponse> buscarNombreTec(String nombre) {
        logger.info(LOGGER_BUSCANDO_NOMBRE,nombre);
        List<ProductoEntity> productoEntityList = service.buscarNombre(nombre);
        return populator.listEntity2ProductoTecResponses(productoEntityList);
    }

    @Override
    @Transactional
    public ProductoDTO actualizarProducto(ProductoDTO dto) {
        logger.info(LOGGER_ACTUALIZANDO_PRODUCTO,dto.getId());
        //Se busca un productoDto en la base de datos si no  esta lanza una excepcion
        ProductoEntity productoEntityDb = service.buscarId(dto.getId()).orElseThrow(() -> new ProductoNoEncontradoException(MENSAJE_EXCEPTION_NO_ENCONTRADO+dto.getId())); //Se recupera de la DB

        //Se convierte a DTODb
        ProductoDTO productoDTODb = populator.entity2Dto(productoEntityDb);

        //Se verifica que el historialStock no sea vacio o nulo
        if(dto.getHistorialStockDTOS() != null && !dto.getHistorialStockDTOS().isEmpty()){
            //Si es diferente de nulo o diferente de vacio
            //Se agrega el nuevo historialStock
            productoDTODb.getHistorialStockDTOS().add(dto.getHistorialStockDTOS().get(0));
        }else {
            //Si es nulo o vacio
            logger.info(LOGGER_ACTUALIZANDO_COMPATIBLES_PRODUCTO,dto.getId());
            //Se agregan los compatibles
            //Se manda llamar metodo que hace la funcion de agregar compatibles
            agregarCompatibles(productoDTODb,dto);
            ProductoEntity productoEntity = populator.dto2Entity(productoDTODb);

            //Se retorna el productoDto
            return populator.entity2Dto(service.guardar(productoEntity));
        }

        //Se manda llamar metodo que hace la funcion de agregar compatibles
        agregarCompatibles(productoDTODb,dto);

        float costo = productoDTODb.getCostoReferencia();

        if(!dto.isUsarCostoReferencia()){
           costo = dto.getCompraVentaDTOS().get(0).getCosto();
           productoDTODb.setCostoReferencia(costo);
        }

        configurarPrecios(productoDTODb,costo, dto.getCompraVentaDTOS().get(0));
        productoDTODb.getCompraVentaDTOS().add(dto.getCompraVentaDTOS().get(0));

        productoDTODb.getStockDTO().setUnidadesExistencia(calcularStockExistencia(dto,productoDTODb));

        ProductoEntity productoEntity= populator.dto2Entity(productoDTODb);
        logger.info(LOGGER_ACTUALIZANDO_FIN_PRODUCTO,productoEntity.getId());
        return populator.entity2Dto(service.guardar(productoEntity));
    }

    @Override
    @CacheEvict(value = {"producto"}, allEntries = true)
    public List<ProductoPGResponse> buscarNombrePG(String nombre) {
        logger.info(LOGGER_BUSCANDO_NOMBRE,nombre);
        List<ProductoEntity> productoEntityList = service.buscarNombre(nombre);
        return populator.listEntity2ProductoPGResponses(productoEntityList);
    }

    @Override
    @Transactional
    public ProductoDTO actualizarStockVenta(ProductoDTO dto) {
        logger.info(LOGGER_ACTUALIZANDO_STOCK,dto.getId());
        //Se convierte dto a entityDto
        ProductoEntity producto = populator.dto2Entity(dto);
        //si hay las unidades de la entityDto son menores a 0
        if(producto.getStockEntity().getUnidadesExistencia() < 0){
            logger.error(LOGGER_STOCK_EN_CERO);
            throw new StockInsuficienteException();
        }
        //Se busca la entityDto en Optional<EntityDB> por id
        Optional<ProductoEntity> productoEntityOptional = service.buscarId(producto.getId());
        //Si el optional esta vacio lanza error
        if (productoEntityOptional.isEmpty()){
            logger.error(LOGGER_PRODUCTO_NO_ENCONTRADO,producto.getId());
            throw new ProductoNoEncontradoException(MENSAJE_EXCEPTION_NO_ENCONTRADO+producto.getId());
        }
        //Se obtiene la Entity del optional
        ProductoEntity productoDb = productoEntityOptional.get();
        /*
        //Se agregan las nuevas unidades vendidas y las unidades en existencia
        //Unidades vendidas hace referencia a las unidades que se han vendido de manera historica del producto no a las unidades a vender
        //Esa validacion se hace en VentaFacadeImpl
        */
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
            float costoReferencia = productoDTO.getCostoReferencia();
            configurarPrecios(productoDTO, costoReferencia, nuevoCompraVentaDTO);
            nuevoCompraVentaDTO.setCosto(costoReferencia);
            nuevoCompraVentaDTO.setFecha(new Date());
            productoDTO.getCompraVentaDTOS().add(nuevoCompraVentaDTO);
        });
        List<ProductoEntity> productoEntityList = service.cargaMasiva(populator.listDto2entities(productoDTOS));
        return populator.listEntity2dto(productoEntityList);
    }

    private int calcularStockExistencia(ProductoDTO dto, ProductoDTO productoDTODb){
        int unidadesIngresadas;
        int unidadesExistencia;
        if (productoDTODb != null){
            unidadesIngresadas = dto.getHistorialStockDTOS().get(0).getUnidadesIngresadas();
            unidadesExistencia = productoDTODb.getStockDTO().getUnidadesExistencia();
            productoDTODb.getStockDTO().setUnidadesExistencia(unidadesExistencia + unidadesIngresadas);
        }else {
            unidadesIngresadas = dto.getHistorialStockDTOS().get(dto.getHistorialStockDTOS().size() - 1).getUnidadesIngresadas();
            unidadesExistencia = dto.getStockDTO().getUnidadesExistencia();
        }

        return unidadesIngresadas + unidadesExistencia;
    }


    private ProductoDTO crearNuevoProductoConStock(ProductoDTO dto){
        StockDTO stockDTO = new StockDTO();
        dto.setStockDTO(stockDTO);
        dto.getStockDTO().setUnidadesExistencia(calcularStockExistencia(dto,null));
        dto.setCostoReferencia(dto.getCompraVentaDTOS().get(dto.getCompraVentaDTOS().size() - 1).getCosto());

        configurarPrecios(dto,dto.getCompraVentaDTOS().get(dto.getCompraVentaDTOS().size() - 1).getCosto(),dto.getCompraVentaDTOS().get(dto.getCompraVentaDTOS().size() - 1));

        return dto;
    }

    private void configurarPrecios(ProductoDTO dto, float costo, CompraVentaDTO compraVentaDTO){

        float precioPG = precioStrategy.calcularPrecioPG(costo);
        float precioTecnico = precioStrategy.calcularPrecioTecnico(costo);
        compraVentaDTO.setVentaTecnico(precioTecnico);
        compraVentaDTO.setVentaPG(precioPG);
        dto.setCostoReferencia(dto.getCostoReferencia());

    }

    private void agregarCompatibles(ProductoDTO productoDTODb, ProductoDTO dto){
        if(dto.getCompatibles()==null||dto.getCompatibles().isEmpty()){
            //Si lo son se lanza excepcion
            throw new IllegalArgumentException("Debe contener al menos un valor en compatible");
        }else {
            productoDTODb.getCompatibles().addAll(dto.getCompatibles());
        }

    }

}
