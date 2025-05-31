package com.agmadera.mitienda.facade.impl.handlers.impl;

import com.agmadera.mitienda.exceptions.DescuentoNoValidoException;
import com.agmadera.mitienda.exceptions.StockInsuficienteException;
import com.agmadera.mitienda.facade.ProductoFacade;
import com.agmadera.mitienda.facade.StockFacade;
import com.agmadera.mitienda.facade.impl.handlers.ProductoVentaHandler;
import com.agmadera.mitienda.models.CompraVentaDTO;
import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.models.ProductoVentaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductoVentaHandlerImpl implements ProductoVentaHandler {

    private final ProductoFacade productoFacade;
    private final StockFacade stockFacade;

    @Override
    @Transactional
    public ProductoVentaDTO procesarProductoOrden(ProductoVentaDTO productoVenta, boolean esTecnico) {
        ProductoDTO producto = productoFacade.buscarId(productoVenta.getIdProductoRef());

        validarDescuento(producto, productoVenta);
        validarStock(producto, productoVenta);

        productoVenta.setNombre(construirNombreProducto(producto));
        productoVenta.setPrecio(obtenerPrecioProducto(producto, esTecnico));
        productoVenta.setTotal(calcularTotalProducto(productoVenta));

        //actualizarStock(producto, productoVenta);

        return productoVenta;
    }

    @Override
    public void validarYActualizarStock(ProductoDTO producto, ProductoVentaDTO productoVenta) {
        validarStock(producto, productoVenta);
        actualizarStock(producto, productoVenta);
    }

    @Override
    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            rollbackFor = {Exception.class},
            timeout = 30 // 30 segundos máximo
    )
    public void procesarListaProductosEnTransaccion(List<ProductoVentaDTO> productos) {
        log.info("Procesando {} productos en transacción separada", productos.size());

        for (ProductoVentaDTO productoVenta : productos) {
            try {
                ProductoDTO producto = productoFacade.buscarId(productoVenta.getIdProductoRef());
                this.validarYActualizarStock(producto, productoVenta);
            } catch (StockInsuficienteException e) {
                log.error("Stock insuficiente para producto ID: {}", productoVenta.getIdProductoRef(), e);
                throw e; // Propaga para rollback
            } catch (Exception e) {
                log.error("Error procesando producto ID: {}", productoVenta.getIdProductoRef(), e);
                throw new RuntimeException("Error en procesamiento batch", e);
            }
        }
    }

    private void validarStock(ProductoDTO producto, ProductoVentaDTO productoVenta) {
        stockFacade.validarExistenciaStock(producto, productoVenta);
    }

    private void actualizarStock(ProductoDTO producto, ProductoVentaDTO productoVenta) {
        int cantidad = productoVenta.getCantidad();
        producto.getStockDTO().setUnidadesExistencia(
                producto.getStockDTO().getUnidadesExistencia() - cantidad);
        producto.getStockDTO().setUnidadesVendidas(
                producto.getStockDTO().getUnidadesVendidas() + cantidad);

        stockFacade.actualizarStockVenta(producto);
    }

    private String construirNombreProducto(ProductoDTO producto) {
        return String.format("%s %s%s",
                producto.getNombre(),
                producto.getCalidad(),
                producto.isMarco() ? " con marco" : "");
    }

    private float obtenerPrecioProducto(ProductoDTO producto, boolean esTecnico) {
        CompraVentaDTO ultimoPrecio = producto.getCompraVentaDTOS().get(
                producto.getCompraVentaDTOS().size() - 1);
        return esTecnico ? ultimoPrecio.getVentaTecnico() : ultimoPrecio.getVentaPG();
    }

    private float calcularTotalProducto(ProductoVentaDTO productoVenta) {
        return (productoVenta.getPrecio() - productoVenta.getDescuento()) * productoVenta.getCantidad();
    }

    private void validarDescuento(ProductoDTO producto, ProductoVentaDTO productoVenta) {
        if (productoVenta.getDescuento() > producto.getCostoReferencia()) {
            throw new DescuentoNoValidoException("Descuento no válido");
        }
    }
}