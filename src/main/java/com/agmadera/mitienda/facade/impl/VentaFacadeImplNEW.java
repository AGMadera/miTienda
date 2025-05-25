package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.entities.VentaEntity;
import com.agmadera.mitienda.exceptions.StockInsuficienteException;
import com.agmadera.mitienda.exceptions.VentaException;
import com.agmadera.mitienda.facade.DescuentoFacade;
import com.agmadera.mitienda.facade.GananciaFacade;
import com.agmadera.mitienda.facade.VentaFacade;
import com.agmadera.mitienda.facade.impl.builders.VentaResponseBuilder;
import com.agmadera.mitienda.facade.impl.factories.VentaDtoFactory;
import com.agmadera.mitienda.facade.impl.handlers.ProductoVentaHandler;
import com.agmadera.mitienda.facade.impl.validators.VentaValidator;
import com.agmadera.mitienda.models.VentaDTO;
import com.agmadera.mitienda.models.request.VentaRequest;
import com.agmadera.mitienda.populator.VentaPopulator;
import com.agmadera.mitienda.services.VentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
@Primary
public class VentaFacadeImplNEW implements VentaFacade {

    // Dependencias
    private final VentaService ventaService;
    private final VentaPopulator ventaPopulator;
    private final GananciaFacade gananciaFacade;
    private final ProductoVentaHandler productoVentaHandler;
    private final VentaResponseBuilder responseBuilder;
    private final VentaValidator ventaValidator;
    private final VentaDtoFactory ventaDtoFactory;
    private final DescuentoFacade descuentoFacade;


    // Métodos principales
    @Override
    public VentaDTO guardarVenta(VentaDTO ventaDTO) {
        try {
            log.info("Iniciando Validacion");
            ventaValidator.validarVenta(ventaDTO);
            float descuentoTotal = descuentoFacade.calcularDescuento(ventaDTO);
            productoVentaHandler.procesarListaProductosEnTransaccion(ventaDTO.getProductoVentaDTOS());
            VentaEntity ventaGuardada = guardarVentaCompleta(ventaDTO, descuentoTotal);
            registrarGananciaAsync(ventaGuardada, descuentoTotal);
            log.info("Creando respuesta");
            return responseBuilder.construirRespuestaVenta(ventaGuardada, ventaDTO, descuentoTotal);
        } catch (StockInsuficienteException e) {
            log.error("Fallo de stock - Productos: {}", ventaDTO.getProductoVentaDTOS(), e);
            throw new VentaException("Stock insuficiente", ventaDTO, e);
        } catch (RuntimeException e) {
            log.error("Error técnico procesando venta", e);
            throw new VentaException("Error interno", ventaDTO, e);
        }
    }

    // Métodos de soporte transaccional

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarGananciaAsync(VentaEntity venta, float descuentoTotal) {
        try {
            VentaDTO ventaDTO = ventaPopulator.entity2Dto(venta);
            ventaDTO.setDescuentosEnTotalGen(descuentoTotal);
            gananciaFacade.guardarGanacia(ventaDTO);
        } catch (Exception e) {
            log.warn("Error no crítico al registrar ganancia: {}", e.getMessage());
        }
    }
    private VentaEntity guardarVentaCompleta(VentaDTO ventaDTO, float descuentoTotal) {
        float totalFinal = ventaDTO.getTotalGenrealAntesDesc() - descuentoTotal;
        ventaDTO.setTotalGenreal(totalFinal);
        ventaDTO.setDescuentosEnTotalGen(descuentoTotal);
        return ventaService.guardarVenta(ventaPopulator.dto2Entity(ventaDTO));
    }
    @Override
    public VentaDTO orden(VentaRequest ventaRequest) {
        return ventaDtoFactory.crearDesdeRequest(ventaRequest);
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

}