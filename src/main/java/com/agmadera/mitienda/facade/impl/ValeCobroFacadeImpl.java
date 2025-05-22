package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.entities.GarantiaVale;
import com.agmadera.mitienda.entities.HistorialGarantiaVale;
import com.agmadera.mitienda.facade.ValeCobroFacade;
import com.agmadera.mitienda.models.VentaDTO;
import com.agmadera.mitienda.models.response.ValeResponse;
import com.agmadera.mitienda.populator.GarantiaPopulator;
import com.agmadera.mitienda.services.GarantiaValeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class ValeCobroFacadeImpl implements ValeCobroFacade {

    private final GarantiaValeService valeService;
    private final GarantiaPopulator garantiaPopulator;

    public static String INFO_BUSCANDO_VALE = "Buscado vale con folio: ";
    public static String INFO_ACTUALIZANDO_VALE = "Actualizando vale con folio: ";

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    public ValeResponse cobroVale(ValeResponse valeResponse, VentaDTO ventaDTO) {
        try {
            log.info(INFO_BUSCANDO_VALE + valeResponse.getFolio());

            GarantiaVale garantiaValeDb = valeService.buscarValeFolio(valeResponse.getFolio());

            // Crear NUEVA instancia de HistorialGarantiaVale en lugar de reutilizar la existente
            HistorialGarantiaVale nuevoHistorial = new HistorialGarantiaVale();
            // Copiar los valores necesarios del historial existente
            nuevoHistorial.setIdProductoVenta(ventaDTO.getProductoVentaDTOS().get(0).getIdProductoRef());
            nuevoHistorial.setSaldo(valeResponse.getSaldoActual());
            nuevoHistorial.setFecha(new Date());
            // ... otros campos necesarios

            // Asociar el nuevo historial al vale
            garantiaValeDb.getHistorialVales().add(nuevoHistorial);
            garantiaValeDb.setSaldoActual(valeResponse.getSaldoActual());

            log.info(INFO_ACTUALIZANDO_VALE + valeResponse.getFolio());
            GarantiaVale valeGuardado = valeService.guardar(garantiaValeDb);

            return garantiaPopulator.valeEntityToResponse(valeGuardado);
        } catch (Exception e) {
            log.error("Error al procesar cobro de vale", e);
            throw e;
        }
    }

    @Override
    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    public ValeResponse buscarVale(String folio) {
        log.info(INFO_BUSCANDO_VALE + folio);
        GarantiaVale vale = valeService.buscarValeFolio(folio);
        ValeResponse response = garantiaPopulator.valeEntityToResponse(vale);
        // Incluir versión en la respuesta
        response.setVersion(vale.getVersion());
        return response;
    }
}
