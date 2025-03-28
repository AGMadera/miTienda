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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class ProductoFacadeImpl implements ProductoFacade {

    Logger logger = LoggerFactory.getLogger(ProductoFacadeImpl.class);
    @Autowired
    private ProductoService service;

    @Autowired
    private ProductoPopulator populator;

    @Value("${precio.tecnico}")
    private float PRECIO_TECNICO;

    @Value("${precio.pg}")
    private float PRECIO_PUBLICO_GENERAL;

    private String noEncontradoError= "producto con id no encontrado, id:";

    @Override
    public ProductoDTO guardarProducto(ProductoDTO dto) {
        if(dto.getId() != null){
            //actualizacion
           return actualizarProducto(dto);
        }
        StockDTO stockDTO = new StockDTO();
        dto.setStockDTO(stockDTO);
        dto.getStockDTO().setUnidadesExistencia(ingresarStock(dto));

        float costo = dto.getCompraVentaDTOS().get(dto.getCompraVentaDTOS().size() - 1).getCosto();


        float precioPG = generarPrecio(costo, PRECIO_PUBLICO_GENERAL);
        float precioTecnico = generarPrecio(costo, PRECIO_TECNICO);


        dto.getCompraVentaDTOS().get(dto.getCompraVentaDTOS().size()-1).setVentaTecnico(precioTecnico);
        dto.getCompraVentaDTOS().get(dto.getCompraVentaDTOS().size()-1).setVentaPG(precioPG);

        dto.setCostoReferencia(costo);

        ProductoEntity productoEntity= populator.dto2Entity(dto);
        ProductoDTO productoDTO= populator.entity2Dto(service.guardar(productoEntity));
        return productoDTO;
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
    public void cargaMasivaProducto(List<ProductoDTO> dtoList) {


        int i=0;
        while (dtoList.iterator().hasNext()){
            if(i==dtoList.size()){
                break;
            }
            ProductoDTO productoTempDto = dtoList.get(i);
            StockDTO stockDTO = new StockDTO();
            productoTempDto.setStockDTO(stockDTO);
            productoTempDto.getStockDTO().setUnidadesExistencia(ingresarStock(productoTempDto));

            float costo = productoTempDto.getCompraVentaDTOS().get(productoTempDto.getCompraVentaDTOS().size() - 1).getCosto();


            float precioPG = generarPrecio(costo, PRECIO_PUBLICO_GENERAL);
            float precioTecnico = generarPrecio(costo, PRECIO_TECNICO);


            productoTempDto.getCompraVentaDTOS().get(productoTempDto.getCompraVentaDTOS().size()-1).setVentaTecnico(precioTecnico);
            productoTempDto.getCompraVentaDTOS().get(productoTempDto.getCompraVentaDTOS().size()-1).setVentaPG(precioPG);

            productoTempDto.setCostoReferencia(costo);
            i++;
        }
        List<ProductoEntity> productoEntityList = populator.listDto2entities(dtoList);
        List<ProductoEntity> productoEntitiesGuardados = service.cargaMasiva(productoEntityList);

    }

    @Override
    public ProductoDTO buscarId(Long id) {
        logger.info("Buscando con nombre= "+id);
        Optional<ProductoEntity> productoEntityFind = service.buscarId(id);
        if(productoEntityFind.isEmpty()){
            logger.info("no encontrado= "+id);
            //lanzar exception
            throw new ProductoNoEncontradoException(noEncontradoError+ id);
        }
        return populator.entity2Dto(productoEntityFind.get());
    }

    @Override
    public List<ProductoDTO> buscarNombre(String nombre) {
        logger.info("Buscando con nombre= "+nombre);
        List<ProductoEntity> productoEntityList = service.buscarNombre(nombre);
        List<ProductoDTO> productoDTOS = populator.listEntity2dto(productoEntityList);
        return productoDTOS;
    }

    @Override
    public List<ProductoDTO> mostrarTodos() {
        List<ProductoEntity> productoEntities = service.mostrarTodos();
        /*
        int i = 0;
        while (productoEntities.iterator().hasNext()){
            if (i==productoEntities.size()){
                break;
            }
            ProductoEntity producto = productoEntities.get(i);
            int tamCV = producto.getCompraVentaEntity().size();
            float costo = producto.getCompraVentaEntity().get(0).getCosto();
            producto.setCostoReferencia(costo);
            i++;

        }*/

        List<ProductoDTO> productosDTOS = populator.listEntity2dto(productoEntities);
        return productosDTOS;
    }

    @Override
    public List<ProductoTecResponse> buscarNombreTec(String nombre) {
        logger.info("Buscando con nombre= "+nombre);
        List<ProductoEntity> productoEntityList = service.buscarNombre(nombre);
        List<ProductoTecResponse> productoTecRespons = populator.listEntity2ProductoTecResponses(productoEntityList);
        return productoTecRespons;
    }

    @Override
    public ProductoDTO actualizarProducto(ProductoDTO dto) {

        logger.info("Actulizando producto con id= "+dto.getId());

        ProductoEntity productoEntityDb = service.buscarId(dto.getId()).get(); //Se recupera de la DB

        ProductoDTO productoDTODb = populator.entity2Dto(productoEntityDb); //Se convierte a DTODb

        productoDTODb.getHistorialStockDTOS().add(dto.getHistorialStockDTOS().get(0)); //Se adiere historialStock de DTO a List del DTODb

        CompraVentaDTO compraVentaDTO = dto.getCompraVentaDTOS().get(0); //

        /*
        float costo = dto.getCompraVentaDTOS().get(0).getCosto();

        if(costo<productoDTODb.getCostoReferencia()){
           costo= productoDTODb.getCostoReferencia();
        }else {
            productoDTODb.setCostoReferencia(costo);
        }
        */

        float costo = productoDTODb.getCostoReferencia();

        if(!dto.isUsarCostoReferencia()){
           costo = dto.getCompraVentaDTOS().get(0).getCosto();
           productoDTODb.setCostoReferencia(costo);
        }

        float precioPG = generarPrecio(costo, PRECIO_PUBLICO_GENERAL);
        float precioTecnico = generarPrecio(costo, PRECIO_TECNICO);

        compraVentaDTO.setVentaPG(precioPG);
        compraVentaDTO.setVentaTecnico(precioTecnico);



        productoDTODb.getCompraVentaDTOS().add(compraVentaDTO);

        int unidadesIngresadas = dto.getHistorialStockDTOS().get(0).getUnidadesIngresadas();

        int unidadesExistencia = productoDTODb.getStockDTO().getUnidadesExistencia();
        productoDTODb.getStockDTO().setUnidadesExistencia(unidadesExistencia + unidadesIngresadas);


        ProductoEntity productoEntity= populator.dto2Entity(productoDTODb);
        ProductoDTO productoDTO= populator.entity2Dto(service.guardar(productoEntity));
        return productoDTO;
    }

    @Override
    public List<ProductoPGResponse> buscarNombrePG(String nombre) {
        logger.info("Buscando con nombre= "+nombre);
        List<ProductoEntity> productoEntityList = service.buscarNombre(nombre);
        List<ProductoPGResponse> productoPGRespons = populator.listEntity2ProductoPGResponses(productoEntityList);
        return productoPGRespons;
    }

    @Override
    public ProductoDTO actualizarStock(ProductoDTO dto) {
        ProductoEntity producto = populator.dto2Entity(dto);
        int unidadesExistencia = producto.getStockEntity().getUnidadesExistencia();

        if(unidadesExistencia < 0){
            logger.error("stock en 0");
            throw new StockInsuficienteException();

        }
        Optional<ProductoEntity> productoEntityOptional = service.buscarId(producto.getId());
        if (!productoEntityOptional.isPresent()){
            throw new ProductoNoEncontradoException(noEncontradoError+producto.getId());
        }
        ProductoEntity productoDb = productoEntityOptional.get();
        productoDb.getStockEntity().setUnidadesVendidas(producto.getStockEntity().getUnidadesVendidas());
        productoDb.getStockEntity().setUnidadesExistencia(producto.getStockEntity().getUnidadesExistencia());

        return populator.entity2Dto(service.guardar(productoDb));

    }

    private int ingresarStock(ProductoDTO dto){
        int unidadesIngresadas;
        int unidadesExistencia;
        if(dto.getHistorialStockDTOS().isEmpty()){
            unidadesIngresadas = 0;
            unidadesExistencia = dto.getHistorialStockDTOS().get(dto.getHistorialStockDTOS().size()-1).getUnidadesIngresadas();
        }else {
            unidadesIngresadas = dto.getHistorialStockDTOS().get(dto.getHistorialStockDTOS().size() - 1).getUnidadesIngresadas();
            unidadesExistencia = dto.getStockDTO().getUnidadesExistencia();
        }


        return unidadesIngresadas + unidadesExistencia;
    }

    private float generarPrecio(float costo, float precio){
        double costoAjustado = Math.ceil(costo / 10) * 10;
        float ajustado = (float) costoAjustado;
        float precioFinal = ajustado + precio;

        return precioFinal;
    }


}
