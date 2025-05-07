package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.Enum.TipoGarantia;
import com.agmadera.mitienda.entities.GanaciaEntity;
import com.agmadera.mitienda.facade.GananciaFacade;
import com.agmadera.mitienda.facade.ProductoFacade;
import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.models.ProductoVentaDTO;
import com.agmadera.mitienda.models.VentaDTO;
import com.agmadera.mitienda.services.GanaciaService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GananciaFacadeImpl implements GananciaFacade {

    Logger logger = LoggerFactory.getLogger(GananciaFacadeImpl.class);
    private static final String INFO_PRODUCTO_PAGO_VALES = "Producto pagado con vale";
    private static final String INFO_ACTUALIZANDO_GANACIAS = "Actualizando venta por garantia, venta id: ";
    private static final String INFO_PRODUCTO_ID = " producto id: ";

    @Autowired
    private GanaciaService ganaciaService;

    @Autowired
    private ProductoFacade productoFacade;

    @Override
    @Transactional
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
                if (ventaDTO.getFolioGarantia()!=null){
                    logger.info(INFO_PRODUCTO_PAGO_VALES);
                    ganaciaEntity.setGarantia(true);
                }
                ganaciaEntities.add(ganaciaEntity);
            }

           return null;
        }).collect(Collectors.toList());

        ganaciaService.guardarGanacias(ganaciaEntities);
    }

    @Override
    @Transactional
    public void actualizarGanacia(VentaDTO ventaDTO, Long idProdRef, TipoGarantia tipoGarantia) {
        List<GanaciaEntity> ganaciaEntities = ganaciaService.buscarGanaciaVentas(ventaDTO.getId(), idProdRef);
        logger.info(INFO_ACTUALIZANDO_GANACIAS+ventaDTO.getId()+INFO_PRODUCTO_ID+ idProdRef);
        for (GanaciaEntity ganancia:ganaciaEntities){
            if (ganancia.getGanacia() > 0 ){
                if (tipoGarantia.ordinal() != TipoGarantia.VALE.ordinal()) {
                    ganancia.setVenta(0);
                    ganancia.setGanacia(-ganancia.getCompra());
                }
                ganancia.setGarantia(true);
                ganaciaService.guardarGanacia(ganancia);
                break;
            }
        }

    }

}
