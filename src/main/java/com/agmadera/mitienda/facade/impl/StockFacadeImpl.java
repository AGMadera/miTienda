package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.entities.ProductoEntity;
import com.agmadera.mitienda.facade.StockFacade;
import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.populator.ProductoPopulator;
import com.agmadera.mitienda.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StockFacadeImpl implements StockFacade {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoPopulator populator;

    @Override
    public ProductoDTO actualizarStockProducto(ProductoDTO dto, Long id) {

        ProductoEntity productoEntity = productoService.buscarId(id).get();
/*
        int unidadesVendidasDTO = dto.getStockDTO().getUnidadesVendidas();
        int unidadesSolicitadasDTO = dto.getStockDTO().getUnidadesSolicitadas();

        if(unidadesSolicitadasDTO > 0){
            //productoEntity.getStock().setUnidadesSolicitadas(unidadesSolicitadasDTO);
        }

        if(unidadesVendidasDTO > 0){
            productoEntity.getStockEntity().setUnidadesVendidas(unidadesVendidasDTO);
            //int unidadesExistencia = productoEntity.getStock().getUnidadesSolicitadas() - unidadesVendidasDTO;
            //productoEntity.getStock().setUnidadesExistencia(unidadesExistencia);
        }

        ProductoEntity productoGuardado = productoService.guardar(productoEntity);
        ProductoDTO productoDTOguardado = populator.entity2Dto(productoGuardado);

        return productoDTOguardado;*/
        return null;
    }
}
