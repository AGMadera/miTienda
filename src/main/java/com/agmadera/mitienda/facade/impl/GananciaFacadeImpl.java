package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.Enum.TipoGarantia;
import com.agmadera.mitienda.entities.GananciaEntity;
import com.agmadera.mitienda.entities.GananciaVentaEntity;
import com.agmadera.mitienda.facade.GananciaFacade;
import com.agmadera.mitienda.facade.ProductoFacade;
import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.models.ProductoVentaDTO;
import com.agmadera.mitienda.models.VentaDTO;
import com.agmadera.mitienda.services.GananciaService;
import com.agmadera.mitienda.services.GanaciaVentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class GananciaFacadeImpl implements GananciaFacade {

    private static final String INFO_PRODUCTO_PAGO_VALES = "Producto pagado con vale";
    private static final String INFO_ACTUALIZANDO_GANACIAS = "Actualizando venta por garantia, venta id: ";
    private static final String INFO_PRODUCTO_ID = " producto id: ";


    private final GananciaService gananciaService;
    private final GanaciaVentaService gananciaVentaService;
    private final ProductoFacade productoFacade;

    @Override
    @Transactional
    public void guardarGanacia(VentaDTO ventaDTO) {
        if (ventaDTO == null || ventaDTO.getProductoVentaDTOS() == null) {
            throw new IllegalArgumentException("VentaDTO o su lista de productos no pueden ser nulos");
        }

        List<GananciaEntity> ganaciaEntities = new ArrayList<>();
        GananciaAcumulada acumulados = new GananciaAcumulada();

        ventaDTO.getProductoVentaDTOS().forEach(productoVentaDTO -> {
            ProductoDTO productoDTO = productoFacade.buscarId(productoVentaDTO.getIdProductoRef());

            for (int i = 0; i < productoVentaDTO.getCantidad(); i++) {
                GananciaEntity gananciaEntity = crearGanaciaEntity(ventaDTO, productoVentaDTO, productoDTO);
                ganaciaEntities.add(gananciaEntity);

                acumulados.agregar(
                        productoDTO.getCostoReferencia(),
                        productoVentaDTO.getPrecio() - productoVentaDTO.getDescuento()
                );
            }
        });

        GananciaVentaEntity gananciaVentaEntity = crearGananciaVentaEntity(ventaDTO, acumulados);
        gananciaVentaService.guardarGananciaVenta(gananciaVentaEntity);
        gananciaService.guardarGanacias(ganaciaEntities);
    }

    // Clase auxiliar para acumulación
    private static class GananciaAcumulada {
        private float inversion = 0;
        private float totalVenta = 0;

        public void agregar(float compra, float venta) {
            inversion += compra;
            totalVenta += venta;
        }

        public float getInversion() { return inversion; }
        public float getTotalVenta() { return totalVenta; }
        public float getGanancia() { return totalVenta - inversion; }
    }

    private GananciaEntity crearGanaciaEntity(VentaDTO ventaDTO, ProductoVentaDTO productoVentaDTO, ProductoDTO productoDTO) {
        GananciaEntity entity = new GananciaEntity();
        entity.setIdProductoRef(productoVentaDTO.getIdProductoRef());
        entity.setIdVentaRef(ventaDTO.getId());
        entity.setFecha(ventaDTO.getFechaVenta());

        float compra = productoDTO.getCostoReferencia();
        float venta = productoVentaDTO.getPrecio() - productoVentaDTO.getDescuento() ;

        entity.setCompra(compra);
        entity.setVenta(venta);
        entity.setGanacia(venta - compra);

        if (ventaDTO.getFolioGarantia() != null) {
            log.info(INFO_PRODUCTO_PAGO_VALES);
            entity.setGarantia(true);
        }

        return entity;
    }

    private GananciaVentaEntity crearGananciaVentaEntity(VentaDTO ventaDTO, GananciaAcumulada acumulados) {
        GananciaVentaEntity entity = new GananciaVentaEntity();
        entity.setInversion(acumulados.getInversion());
        entity.setVendido(ventaDTO.getTotalGenreal());
        entity.setGanancia(ventaDTO.getTotalGenreal()-acumulados.getInversion());
        entity.setIdVenta(ventaDTO.getId());
        entity.setFecha(new Date());
        return entity;
    }

    @Override
    @Transactional
    public void actualizarGanacia(VentaDTO ventaDTO, Long idProdRef, TipoGarantia tipoGarantia) {
        if (ventaDTO == null || idProdRef == null || tipoGarantia == null) {
            throw new IllegalArgumentException("Parámetros no pueden ser nulos");
        }

        log.info("{}{}{}{}", INFO_ACTUALIZANDO_GANACIAS, ventaDTO.getId(), INFO_PRODUCTO_ID, idProdRef);

        gananciaService.buscarGanaciaVentas(ventaDTO.getId(), idProdRef).stream()
                .filter(ganancia -> ganancia.getGanacia() > 0)
                .findFirst()
                .ifPresent(ganancia -> actualizarGananciaPorGarantia(ganancia, tipoGarantia));
    }

    private void actualizarGananciaPorGarantia(GananciaEntity ganancia, TipoGarantia tipoGarantia) {
        if (tipoGarantia != TipoGarantia.VALE) {
            ganancia.setVenta(0);
            ganancia.setGanacia(-ganancia.getCompra());
        }
        ganancia.setGarantia(true);
        gananciaService.guardarGanacia(ganancia);
    }
}
