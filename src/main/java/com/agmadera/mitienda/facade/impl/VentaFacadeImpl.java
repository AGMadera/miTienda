package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.Enum.TipoPago;
import com.agmadera.mitienda.entities.VentaEntity;
import com.agmadera.mitienda.exceptions.DescuentoNoValidoException;
import com.agmadera.mitienda.exceptions.GarantiaNoValidaException;
import com.agmadera.mitienda.facade.*;
import com.agmadera.mitienda.models.*;
import com.agmadera.mitienda.models.request.VentaRequest;
import com.agmadera.mitienda.models.response.ValeResponse;
import com.agmadera.mitienda.populator.VentaPopulator;
import com.agmadera.mitienda.services.VentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class VentaFacadeImpl implements VentaFacade {

    // Constantes para logs y mensajes
    private static final String LOG_GUARDANDO_VENTA = "Procesando venta con {} productos";
    private static final String LOG_PROCESANDO_PRODUCTO = "Procesando producto {}/{}";
    private static final String ERROR_DESCUENTO_INVALIDO = "Descuento no válido";
    private static final String ERROR_GARANTIA_CANTIDAD = "Pago con vale solo admite 1 producto";
    private static final String ERROR_GARANTIA_SALDO = "Saldo de vale es 0 o insuficiente";
    private static final String ERROR_STOCK_INSUFICIENTE = "Stock insuficiente para el producto";

    // Dependencias
    private final VentaService ventaService;
    private final ProductoFacade productoFacade;
    private final VentaPopulator ventaPopulator;
    private final GananciaFacade gananciaFacade;
    private final ValeCobroFacade garantiaFacade;
    private final StockFacade stockFacade;

    @Value("${mensaje.venta}")
    private String mensajeVenta;

@Override
@Transactional
public VentaDTO guardarVenta(VentaDTO ventaDTO) {
    validarVentaDTO(ventaDTO);
    List<ProductoVentaDTO> productos = ventaDTO.getProductoVentaDTOS();

    log.info("Procesando venta con {} productos", productos.size());

    // 1. Calcular descuentos (incluyendo vales si aplica)
    float descuentoTotal = calcularDescuentosTotales(ventaDTO);

    // 2. Validar que el descuento no exceda el total
    validarDescuentos(descuentoTotal, ventaDTO.getTotalGenrealAntesDesc());

    // 3. Procesar productos (actualizar stocks)
    procesarProductosVenta(productos);

    // 4. Calcular total final con descuentos aplicados
    float totalFinal = ventaDTO.getTotalGenrealAntesDesc() - descuentoTotal;
    ventaDTO.setTotalGenreal(totalFinal);
    ventaDTO.setDescuentosEnTotalGen(descuentoTotal); // Asegurar que se guarda el descuento

    // 5. Guardar la venta en el sistema
    VentaEntity ventaGuardada = ventaService.guardarVenta(ventaPopulator.dto2Entity(ventaDTO));

    // 6. Construir respuesta
    VentaDTO respuesta = construirRespuestaVenta(ventaGuardada, ventaDTO, descuentoTotal);

    // 7. Registrar ganancia
    gananciaFacade.guardarGanacia(respuesta);

    return respuesta;
}

    private float calcularDescuentosTotales(VentaDTO ventaDTO) {
        float descuentoTotal = ventaDTO.getDescuentosEnTotalGen();

        // Si es pago con vale, procesar descuento adicional
        if (ventaDTO.getTipoPago().equals(TipoPago.VALE) && ventaDTO.getFolioGarantia() != null) {
            descuentoTotal += procesarDescuentoPorVale(ventaDTO);
        }

        return descuentoTotal;
    }

    private float procesarDescuentoPorVale(VentaDTO ventaDTO) {
        if (ventaDTO.getProductoVentaDTOS().size() > 1) {
            throw new GarantiaNoValidaException("Pago con vale solo admite 1 producto");
        }

        ValeResponse vale = garantiaFacade.buscarVale(ventaDTO.getFolioGarantia());
        validarSaldoVale(vale.getSaldoActual());

        float descuentoVale = Math.min(vale.getSaldoActual(), ventaDTO.getTotalGenrealAntesDesc());

        // Actualizar vale
        vale.setSaldoActual(vale.getSaldoActual() - descuentoVale);
        vale.getHistorialGarantiaValesDtos().add(crearHistorialGarantia(vale, ventaDTO, descuentoVale));
        garantiaFacade.cobroVale(vale, ventaDTO);

        return descuentoVale;
    }

    private void validarDescuentos(float descuentoTotal, float totalAntesDescuento) {
        if (descuentoTotal > totalAntesDescuento) {
            throw new DescuentoNoValidoException("El descuento no puede ser mayor al total");
        }
    }

    private VentaDTO construirRespuestaVenta(VentaEntity ventaGuardada, VentaDTO ventaOriginal, float descuentoTotal) {
        VentaDTO respuesta = ventaPopulator.entity2Dto(ventaGuardada);
        respuesta.setMensaje(mensajeVenta);

        // Asegurar que los descuentos se reflejen en la respuesta
        respuesta.setDescuentosEnTotalGen(descuentoTotal);

        if (ventaOriginal.getTipoPago().equals(TipoPago.VALE)) {
            // Aplicar descuento del vale al primer producto
            if (!respuesta.getProductoVentaDTOS().isEmpty()) {
                respuesta.getProductoVentaDTOS().get(0).setDescuento(
                        respuesta.getProductoVentaDTOS().get(0).getDescuento() +
                                (descuentoTotal - ventaOriginal.getDescuentosEnTotalGen())
                );
            }
            respuesta.setFolioGarantia(ventaOriginal.getFolioGarantia());
        }

        return respuesta;
    }

    @Override
    public VentaDTO orden(VentaRequest ventaRequest) {
        VentaDTO ventaDTO = new VentaDTO();
        ventaDTO.setTecnico(ventaRequest.isTecnico());

        List<ProductoVentaDTO> productos = ventaRequest.getProductoVentaDTOS().stream()
                .map(p -> procesarProductoOrden(p, ventaRequest.isTecnico()))
                .collect(Collectors.toList());

        double totalGeneral = productos.stream()
                .mapToDouble(ProductoVentaDTO::getTotal)
                .sum();

        ventaDTO.setProductoVentaDTOS(productos);
        ventaDTO.setTotalGenrealAntesDesc((float) totalGeneral);

        return ventaDTO;
    }

    @Override
    public VentaDTO buscarVenta(Long id) {
        return ventaPopulator.entity2Dto(ventaService.buscarVenta(id));
    }

    @Override
    public VentaDTO actualizarVentaGarantia(VentaDTO ventaDTO) {
        return ventaPopulator.entity2Dto(
                ventaService.guardarVenta(ventaPopulator.dto2Entity(ventaDTO))
        );
    }

    // Métodos privados auxiliares

    private void validarVentaDTO(VentaDTO ventaDTO) {
        Objects.requireNonNull(ventaDTO, "El DTO de venta no puede ser nulo");
        Objects.requireNonNull(ventaDTO.getProductoVentaDTOS(), "La lista de productos no puede ser nula");
    }

    private float procesarPagoConVale(VentaDTO ventaDTO) {
        if (ventaDTO.getProductoVentaDTOS().size() > 1) {
            throw new GarantiaNoValidaException(ERROR_GARANTIA_CANTIDAD);
        }

        ValeResponse vale = garantiaFacade.buscarVale(ventaDTO.getFolioGarantia());
        validarSaldoVale(vale.getSaldoActual());

        float descuento = calcularDescuentoVale(vale.getSaldoActual(), ventaDTO.getTotalGenrealAntesDesc());
        actualizarVale(vale, descuento, ventaDTO);

        return descuento;
    }

    private void validarSaldoVale(float saldo) {
        if (saldo <= 0) {
            throw new GarantiaNoValidaException(ERROR_GARANTIA_SALDO);
        }
    }

    private float calcularDescuentoVale(float saldo, float totalVenta) {
        return Math.min(saldo, totalVenta);
    }

    private void actualizarVale(ValeResponse vale, float descuento, VentaDTO ventaDTO) {
        vale.setSaldoActual(vale.getSaldoActual() - descuento);
        vale.getHistorialGarantiaValesDtos().add(crearHistorialGarantia(vale, ventaDTO, descuento));
        garantiaFacade.cobroVale(vale, ventaDTO);
    }

    private HistorialGarantiaValeDto crearHistorialGarantia(ValeResponse vale, VentaDTO ventaDTO, float descuento) {
        return HistorialGarantiaValeDto.builder()
                .saldo(vale.getSaldoActual())
                .idProductoVenta(ventaDTO.getProductoVentaDTOS().get(0).getIdProductoRef())
                .fecha(new Date())
                .build();
    }

    private void procesarProductosVenta(List<ProductoVentaDTO> productos) {
        for (int i = 0; i < productos.size(); i++) {
            ProductoVentaDTO productoVenta = productos.get(i);
            log.info(LOG_PROCESANDO_PRODUCTO, i+1, productos.size());

            ProductoDTO producto = productoFacade.buscarId(productoVenta.getIdProductoRef());
            actualizarStockProducto(producto, productoVenta.getCantidad());
        }
    }

    private void actualizarStockProducto(ProductoDTO producto, int cantidadVendida) {
        producto.getStockDTO().setUnidadesExistencia(producto.getStockDTO().getUnidadesExistencia() - cantidadVendida);
        producto.getStockDTO().setUnidadesVendidas(producto.getStockDTO().getUnidadesVendidas() + cantidadVendida);
        stockFacade.actualizarStockVenta(producto);
    }

    private VentaEntity guardarVentaEnSistema(VentaDTO ventaDTO, float descuentoTotal) {
        ventaDTO.setTotalGenreal(ventaDTO.getTotalGenrealAntesDesc() - descuentoTotal);
        return ventaService.guardarVenta(ventaPopulator.dto2Entity(ventaDTO));
    }

    private VentaDTO construirRespuesta(VentaEntity ventaGuardada, VentaDTO ventaOriginal, float descuentoGarantia) {
        VentaDTO respuesta = ventaPopulator.entity2Dto(ventaGuardada);
        respuesta.setMensaje(mensajeVenta);

        if (ventaOriginal.getTipoPago().equals(TipoPago.VALE)) {
            respuesta.getProductoVentaDTOS().get(0).setDescuento(descuentoGarantia);
            respuesta.setFolioGarantia(ventaOriginal.getFolioGarantia());
        }

        gananciaFacade.guardarGanacia(respuesta);
        return respuesta;
    }

    private ProductoVentaDTO procesarProductoOrden(ProductoVentaDTO productoVenta, boolean esTecnico) {
        ProductoDTO producto = productoFacade.buscarId(productoVenta.getIdProductoRef());

        stockFacade.validarExistenciaStock(producto, productoVenta);
        validarDescuento(producto, productoVenta);

        productoVenta.setIdProductoRef(productoVenta.getIdProductoRef());
        productoVenta.setNombre(construirNombreProducto(producto));
        productoVenta.setPrecio(obtenerPrecioProducto(producto, esTecnico));
        productoVenta.setTotal(calcularTotalProducto(productoVenta));

        return productoVenta;
    }

    private String construirNombreProducto(ProductoDTO producto) {
        return String.format("%s %s%s",
                producto.getNombre(),
                producto.getCalidad(),
                producto.isMarco() ? " con marco" : "");
    }

    private float obtenerPrecioProducto(ProductoDTO producto, boolean esTecnico) {
        CompraVentaDTO ultimoPrecio = producto.getCompraVentaDTOS().get(producto.getCompraVentaDTOS().size() - 1);
        return esTecnico ? ultimoPrecio.getVentaTecnico() : ultimoPrecio.getVentaPG();
    }

    private float calcularTotalProducto(ProductoVentaDTO productoVenta) {
        return (productoVenta.getPrecio() - productoVenta.getDescuento()) * productoVenta.getCantidad();
    }

    private void validarDescuento(ProductoDTO producto, ProductoVentaDTO productoVenta) {
        if (productoVenta.getDescuento() > producto.getCostoReferencia()) {
            throw new DescuentoNoValidoException(ERROR_DESCUENTO_INVALIDO);
        }
    }
}