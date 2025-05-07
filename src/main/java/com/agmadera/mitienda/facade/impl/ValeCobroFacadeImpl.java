package com.agmadera.mitienda.facade.impl;

import com.agmadera.mitienda.entities.GarantiaVale;
import com.agmadera.mitienda.entities.HistorialGarantiaVale;
import com.agmadera.mitienda.facade.ValeCobroFacade;
import com.agmadera.mitienda.models.response.ValeResponse;
import com.agmadera.mitienda.populator.GarantiaPopulator;
import com.agmadera.mitienda.services.GarantiaValeService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValeCobroFacadeImpl implements ValeCobroFacade {

    Logger logger = LoggerFactory.getLogger(ValeCobroFacadeImpl.class);
    @Autowired
    private GarantiaValeService valeService;

    @Autowired
    private GarantiaPopulator garantiaPopulator;

    public static String INFO_BUSCANDO_VALE = "Buscado vale con folio: ";
    public static String INFO_ACTUALIZANDO_VALE = "Actualizando vale con folio: ";

    @Override
    @Transactional
    public ValeResponse cobroVale(ValeResponse valeResponse) {
        logger.info(INFO_BUSCANDO_VALE+valeResponse.getFolio());

        GarantiaVale garantiaValeDb = valeService.buscarValeFolio(valeResponse.getFolio());

        GarantiaVale garantiaValeEntity = garantiaPopulator.responseToValeEntity(valeResponse);
        int tamHstrialVales = garantiaValeEntity.getHistorialVales().size();
        HistorialGarantiaVale historialGarantiaVale = garantiaValeEntity.getHistorialVales().get(tamHstrialVales - 1);
        garantiaValeDb.getHistorialVales().add(historialGarantiaVale);
        garantiaValeDb.setSaldoActual(garantiaValeEntity.getSaldoActual());

        logger.info(INFO_ACTUALIZANDO_VALE+valeResponse.getFolio());
        GarantiaVale valeGuardado = valeService.guardar(garantiaValeDb);

        return garantiaPopulator.valeEntityToResponse(valeGuardado);

    }

    @Override
    public ValeResponse buscarVale(String folio) {
        logger.info(INFO_BUSCANDO_VALE+folio);
        return garantiaPopulator.valeEntityToResponse(valeService.buscarValeFolio(folio));
    }
}
