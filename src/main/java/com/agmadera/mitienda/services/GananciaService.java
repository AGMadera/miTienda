package com.agmadera.mitienda.services;

import com.agmadera.mitienda.entities.GananciaEntity;

import java.util.List;

public interface GananciaService {
    void guardarGanacia(GananciaEntity ganacia);
    List<GananciaEntity> mostrarGanancias();

    void guardarGanacias(List<GananciaEntity> ganaciaEntities);

    List<GananciaEntity> buscarGanaciaVentas(Long idVenta, Long idProdRef);
}
