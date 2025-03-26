package com.agmadera.mitienda.services;

import com.agmadera.mitienda.entities.GanaciaEntity;

import java.util.List;

public interface GanaciaService {
    void guardarGanacia(GanaciaEntity ganacia);
    List<GanaciaEntity> mostrarGanancias();

    void guardarGanacias(List<GanaciaEntity> ganaciaEntities);
}
