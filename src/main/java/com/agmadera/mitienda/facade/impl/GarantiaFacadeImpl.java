package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.Enum.RazonGarantia;
import com.agmadera.mitienda.Enum.TipoGarantia;
import com.agmadera.mitienda.entities.GarantiaReembolso;
import com.agmadera.mitienda.entities.GarantiaVale;
import com.agmadera.mitienda.exceptions.GarantiaNoValidaException;
import com.agmadera.mitienda.exceptions.VentaNoEncontradaException;
import com.agmadera.mitienda.facade.*;
import com.agmadera.mitienda.models.*;
import com.agmadera.mitienda.models.response.ReembolsoResponse;
import com.agmadera.mitienda.models.response.ValeResponse;
import com.agmadera.mitienda.populator.GarantiaPopulator;
import com.agmadera.mitienda.services.GarantiaReembolsoService;
import com.agmadera.mitienda.services.GarantiaValeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GarantiaFacadeImpl implements GarantiaFacade {

    private static final String PRODUCTO_NO_ENCONTRADO_MSG = "Producto no encontrado en venta";
    private static final String GARANTIA_USADA_MSG = "Garantía ya usada";

    private final GarantiaReembolsoService reembolsoService;
    private final GarantiaValeService valeService;
    private final VentaFacade ventaFacade;
    private final ProductoFacade productoFacade;
    private final GarantiaPopulator garantiaPopulator;
    private final GananciaFacade gananciaFacade;
    private final StockFacade stockFacade;

    @Value("${garantia.dias}")
    private float diasGarantiaValidos;

    @Override
    public ReembolsoResponse guardarReembolso(GarantiaDTO reembolso) {
        validarGarantia(reembolso);
        VentaDTO ventaDTO = ventaFacade.buscarVenta(reembolso.getIdVenta());

        float cantidadReembolso = procesarReembolso(ventaDTO, reembolso);
        reembolso.setCantidadReembolso(cantidadReembolso);

        GarantiaReembolso garantiaReembolso = garantiaPopulator.dto2Entity(reembolso);
        GarantiaReembolso savedReembolso = reembolsoService.guardar(garantiaReembolso);

        actualizarGanancias(ventaDTO, reembolso.getIdProductoVenta(), TipoGarantia.REEMBOLSO);

        return construirReembolsoResponse(savedReembolso, reembolso.getIdProductoVenta());
    }

    @Override
    public ValeResponse crearVale(GarantiaDTO garantiaDTO) {
        VentaDTO ventaDTO = validarGarantia(garantiaDTO);
        float cantidadReembolso = procesarReembolso(ventaDTO, garantiaDTO);

        GarantiaVale garantiaVale = crearGarantiaVale(garantiaDTO, cantidadReembolso);
        GarantiaVale savedVale = valeService.guardar(garantiaVale);

        actualizarGanancias(ventaDTO, garantiaDTO.getIdProductoVenta(), garantiaDTO.getTipoGarantia());

        return garantiaPopulator.valeEntityToResponse(savedVale);
    }

    private VentaDTO validarGarantia(GarantiaDTO garantia) {
        VentaDTO ventaDTO = ventaFacade.buscarVenta(garantia.getIdVenta());

        LocalDate fechaVenta = toLocalDate(ventaDTO.getFechaVenta());
        LocalDate fechaGarantia = toLocalDate(garantia.getFecha());

        long diasTranscurridos = ChronoUnit.DAYS.between(fechaVenta, fechaGarantia);

        if (diasTranscurridos > diasGarantiaValidos) {
            log.warn("Garantía no válida. Días transcurridos: {}", diasTranscurridos);
            throw new GarantiaNoValidaException();
        }

        log.debug("Garantía válida. Días transcurridos: {}", diasTranscurridos);
        return ventaDTO;
    }

    private float procesarReembolso(VentaDTO ventaDTO, GarantiaDTO garantiaDTO) {
        ProductoVentaDTO productoVenta = buscarProductoEnVenta(ventaDTO, garantiaDTO.getIdProductoVenta())
                .orElseThrow(() -> new VentaNoEncontradaException(PRODUCTO_NO_ENCONTRADO_MSG));

        validarCantidadProducto(productoVenta);

        float precioProducto = productoVenta.getPrecio();
        actualizarProductoVenta(productoVenta, precioProducto);
        actualizarVenta(ventaDTO, precioProducto);

        if (garantiaDTO.getRazonGarantia() != RazonGarantia.displayMalEstado) {
            actualizarStockProducto(garantiaDTO.getIdProductoVenta());
        }else {
            registrarMerma(garantiaDTO.getIdProductoVenta());
        }


        return precioProducto;
    }

    private Optional<ProductoVentaDTO> buscarProductoEnVenta(VentaDTO ventaDTO, Long idProducto) {
        return ventaDTO.getProductoVentaDTOS().stream()
                .filter(p -> p.getIdProductoRef().equals(idProducto))
                .findFirst();
    }

    private void validarCantidadProducto(ProductoVentaDTO productoVenta) {
        if (productoVenta.getCantidad() <= 0) {
            log.error("Cantidad inválida para producto: {}", productoVenta.getIdProductoRef());
            throw new GarantiaNoValidaException(GARANTIA_USADA_MSG);
        }
    }

    private void actualizarProductoVenta(ProductoVentaDTO productoVenta, float precio) {
        productoVenta.setCantidad(productoVenta.getCantidad() - 1);
        productoVenta.setTotal(productoVenta.getTotal() - precio);
        productoVenta.setDescuento(precio);
    }

    private void actualizarVenta(VentaDTO ventaDTO, float precio) {
        ventaDTO.setDescuentosEnTotalGen(precio);
        ventaDTO.setTotalGenreal(ventaDTO.getTotalGenreal() - precio);
        ventaFacade.actualizarVentaGarantia(ventaDTO);
    }

    private void actualizarStockProducto(Long idProducto) {
        ProductoDTO productoDTO = productoFacade.buscarId(idProducto);
        productoDTO.getStockDTO().setUnidadesVendidas(productoDTO.getStockDTO().getUnidadesVendidas() - 1);
        productoDTO.getStockDTO().setUnidadesExistencia(productoDTO.getStockDTO().getUnidadesExistencia() + 1);

        HistorialStockDTO historialStock = new HistorialStockDTO();
        historialStock.setUnidadesIngresadas(1);
        historialStock.setFecha(new Date());
        productoDTO.getHistorialStockDTOS().add(historialStock);

        stockFacade.actualizarStockVenta(productoDTO);
    }

    private GarantiaVale crearGarantiaVale(GarantiaDTO garantiaDTO, float cantidadReembolso) {
        GarantiaVale garantiaVale = garantiaPopulator.dtoVale2Entity(garantiaDTO);
        garantiaVale.setSaldoInicial(cantidadReembolso);
        garantiaVale.setSaldoActual(cantidadReembolso);

        GarantiaVale savedVale = valeService.guardar(garantiaVale);
        garantiaVale.setFolio(generarFolioVale(savedVale));
        garantiaVale.setId(savedVale.getId());

        return garantiaVale;
    }

    private String generarFolioVale(GarantiaVale vale) {
        return String.format("%d-%d-%d", vale.getIdVenta(), vale.getIdProductoVenta(), vale.getId());
    }

    private ReembolsoResponse construirReembolsoResponse(GarantiaReembolso reembolso, Long idProducto) {
        ReembolsoResponse response = garantiaPopulator.entity2Response(reembolso);
        response.setProducto(obtenerNombreProducto(idProducto));
        return response;
    }

    private String obtenerNombreProducto(Long idProducto) {
        ProductoDTO productoDTO = productoFacade.buscarId(idProducto);
        return String.format("%s %s%s",
                productoDTO.getNombre(),
                productoDTO.getCalidad(),
                productoDTO.isMarco() ? " con marco" : "");
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void actualizarGanancias(VentaDTO ventaDTO, Long idProducto, TipoGarantia tipoGarantia) {
        gananciaFacade.actualizarGanacia(ventaDTO, idProducto, tipoGarantia);
    }

    private void registrarMerma(Long idProducto){
        stockFacade.registrarMerma(idProducto);
    }
}