package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.entities.GanaciaEntity;
import com.agmadera.mitienda.facade.GananciaFacade;
import com.agmadera.mitienda.facade.ProductoFacade;
import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.models.ProductoVentaDTO;
import com.agmadera.mitienda.models.VentaDTO;
import com.agmadera.mitienda.services.GanaciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GananciaFacadeImpl implements GananciaFacade {

    @Autowired
    private GanaciaService ganaciaService;

    @Autowired
    private ProductoFacade productoFacade;

    @Override
    public void guardarGanacia(VentaDTO ventaDTO) {

        List<ProductoVentaDTO> productoVentaDTOS = ventaDTO.getProductoVentaDTOS();

        List<GanaciaEntity> ganaciaEntities = new ArrayList<>();

        productoVentaDTOS.stream().map(productoVentaDTO -> {

            for (int cantidad = productoVentaDTO.getCantidad();cantidad>0;cantidad--){

                Long idProductoRef = productoVentaDTO.getIdProductoRef();
                Long ventaDTOId = ventaDTO.getId();
                Date fechaVenta = ventaDTO.getFechaVenta();

                ProductoDTO productoDTO = productoFacade.buscarId(idProductoRef);

                float compra = productoDTO.getCostoReferencia();
                float venta = productoVentaDTO.getPrecio()-productoVentaDTO.getDescuento();
                float ganacia = venta - compra;

                GanaciaEntity ganaciaEntity = new GanaciaEntity();
                ganaciaEntity.setIdProductoRef(idProductoRef);
                ganaciaEntity.setIdVentaRef(ventaDTOId);
                ganaciaEntity.setVenta(venta);
                ganaciaEntity.setCompra(compra);
                ganaciaEntity.setGanacia(ganacia);
                ganaciaEntity.setFecha(fechaVenta);
                ganaciaEntities.add(ganaciaEntity);
            }

           return null;
        }).collect(Collectors.toList());

        ganaciaService.guardarGanacias(ganaciaEntities);
    }
}
