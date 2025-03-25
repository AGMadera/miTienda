package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.entities.GanaciaEntity;
import com.agmadera.mitienda.facade.GananciaFacade;
import com.agmadera.mitienda.facade.ProductoFacade;
import com.agmadera.mitienda.models.ProductoVentaDTO;
import com.agmadera.mitienda.models.VentaDTO;
import com.agmadera.mitienda.services.GanaciaService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class GananciaFacadeImpl implements GananciaFacade {

    @Autowired
    private GanaciaService ganaciaService;

    @Autowired
    private ProductoFacade productoFacade;

    @Override
    public void guardarGanacia(VentaDTO ventaDTO) {

        List<ProductoVentaDTO> ventaDTOProductoVentaDTOS = ventaDTO.getProductoVentaDTOS();
        int count=0;
        while (ventaDTOProductoVentaDTOS.iterator().hasNext()){
            if(count== ventaDTOProductoVentaDTOS.size()){
                break;
            }

        }
        ventaDTOProductoVentaDTOS.stream().map(productoVentaDTO -> {
            Long idProductoRef = productoVentaDTO.getIdProductoRef();
            Long ventaDTOId = ventaDTO.getId();
            Date fechaVenta = ventaDTO.getFechaVenta();
            //productoVentaDTO
            return null;
        });
        //GanaciaEntity ganaciaEntity = new GanaciaEntity(idVenta,idProducto,fecha);
    }
}
