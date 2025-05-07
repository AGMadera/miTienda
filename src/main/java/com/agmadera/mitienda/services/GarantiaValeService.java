package com.agmadera.mitienda.services;

import com.agmadera.mitienda.entities.GarantiaVale;

public interface GarantiaValeService {
    GarantiaVale guardar(GarantiaVale vale);
    GarantiaVale buscarValeFolio(String folio);
}
