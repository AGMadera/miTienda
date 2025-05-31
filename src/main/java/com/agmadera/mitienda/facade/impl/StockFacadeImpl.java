package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.entities.ProductoEntity;
import com.agmadera.mitienda.entities.StockEntity;
import com.agmadera.mitienda.exceptions.StockInsuficienteException;
import com.agmadera.mitienda.facade.StockFacade;
import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.models.ProductoVentaDTO;
import com.agmadera.mitienda.models.StockDTO;
import com.agmadera.mitienda.populator.ProductoPopulator;
import com.agmadera.mitienda.services.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class StockFacadeImpl implements StockFacade {
    private static final String LOGGER_STOCK_INSUFICIENTE = "Stock insuficiente para producto ID: {}";
    private final String LOGGER_NO_HAY_EXISTENCIA = "No hay unidades en existencia";
    //private final String LOGGER_EXISTENCIA_NO_SUFICIENTE = "No hay suficientes unidades";
    private final ProductoPopulator populator;
    private final ProductoService productoService;



    @Override
    public int calcularStockExistencia(ProductoDTO dto, ProductoDTO productoDtoExistente) {
        int unidadesIngresadas;
        int unidadesExistencia;

        if(productoDtoExistente != null){
            unidadesIngresadas = dto.getHistorialStockDTOS().get(0).getUnidadesIngresadas();
            unidadesExistencia = productoDtoExistente.getStockDTO().getUnidadesExistencia();
            productoDtoExistente.getStockDTO().setUnidadesExistencia(unidadesExistencia + unidadesIngresadas);
        }else{
            unidadesIngresadas = dto.getHistorialStockDTOS().get(dto.getHistorialStockDTOS().size() - 1).getUnidadesIngresadas();
            unidadesExistencia = dto.getStockDTO().getUnidadesExistencia();
        }

        return unidadesIngresadas + unidadesExistencia;
    }
    public void inicializarStock(ProductoDTO dto, ProductoDTO productoExistente) {
        StockDTO stockDTO = new StockDTO();
        dto.setStockDTO(stockDTO);
        dto.getStockDTO().setUnidadesExistencia(calcularStockExistencia(dto, productoExistente));
    }

    @Transactional
    public ProductoDTO actualizarStockVenta(ProductoDTO dto) {

        log.info("Actualizando stock producto ID: {} por Venta", dto.getId());
        ProductoEntity producto = populator.dto2Entity(dto);
        validarStock(producto);
        ProductoEntity productoDb = productoService.buscarId(producto.getId());
        actualizarStock(productoDb, producto);

        return populator.entity2Dto(productoService.guardar(productoDb));

    }

    @Override
    public void validarExistenciaStock(ProductoDTO productoDTO, ProductoVentaDTO productoVentaDTO) {
        if (productoDTO.getStockDTO().getUnidadesExistencia() == 0 ||
                productoDTO.getStockDTO().getUnidadesExistencia()<productoVentaDTO.getCantidad()){
            if (productoDTO.getStockDTO().getUnidadesExistencia() == 0){
                log.error(LOGGER_NO_HAY_EXISTENCIA);
                throw new StockInsuficienteException();
            }
            log.error(LOGGER_STOCK_INSUFICIENTE);
            throw new StockInsuficienteException(productoVentaDTO.getIdProductoRef(), productoDTO.getStockDTO().getUnidadesExistencia());
        }
    }

    @Override
    public void registrarMerma(Long idProducto) {
        log.info("Registrando merma del producto: {}",idProducto);
        ProductoEntity productoEntity = productoService.buscarId(idProducto);
        StockEntity stockEntity = productoEntity.getStockEntity();
        stockEntity.setUnidadesVendidas(stockEntity.getUnidadesVendidas()-1);
        stockEntity.setMerma(stockEntity.getMerma()+1);
        productoService.guardar(productoEntity);
    }

    private void validarStock(ProductoEntity producto) {
        if (producto.getStockEntity().getUnidadesExistencia() < 0) {
            log.error(LOGGER_STOCK_INSUFICIENTE, producto.getId());
            throw new StockInsuficienteException();
        }
    }

    private void actualizarStock(ProductoEntity destino, ProductoEntity origen) {
        destino.getStockEntity().setUnidadesVendidas(origen.getStockEntity().getUnidadesVendidas());
        destino.getStockEntity().setUnidadesExistencia(origen.getStockEntity().getUnidadesExistencia());
    }




}
