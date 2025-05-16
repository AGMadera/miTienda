package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.facade.StockFacade;
import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.models.StockDTO;
import com.agmadera.mitienda.populator.ProductoPopulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class StockFacadeImpl implements StockFacade {
    private ProductoPopulator populator;


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
  /*      log.info(LOGGER_ACTUALIZANDO_STOCK,dto.getId());
        //Se convierte dto a entityDto
        ProductoEntity producto = populator.dto2Entity(dto);
        //si hay las unidades de la entityDto son menores a 0
        if(producto.getStockEntity().getUnidadesExistencia() < 0){
            log.error(LOGGER_STOCK_EN_CERO);
            throw new StockInsuficienteException();
        }

        //Se obtiene la Entity del dto
        ProductoEntity productoDb = service.buscarId(producto.getId());

        //Se agregan las nuevas unidades vendidas y las unidades en existencia
        //Unidades vendidas hace referencia a las unidades que se han vendido de manera historica del producto no a las unidades a vender
        //Esa validacion se hace en VentaFacadeImpl

        //productoDb.getStockEntity().setUnidadesVendidas(producto.getStockEntity().getUnidadesVendidas());
        //productoDb.getStockEntity().setUnidadesExistencia(producto.getStockEntity().getUnidadesExistencia());

        //return populator.entity2Dto(service.guardar(productoDb));
        */
        return null;

    }

}
