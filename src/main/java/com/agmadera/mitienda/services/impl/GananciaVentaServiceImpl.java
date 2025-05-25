package com.agmadera.mitienda.services.impl;

import com.agmadera.mitienda.entities.GananciaVentaEntity;
import com.agmadera.mitienda.repositories.GananciaVentaRepository;
import com.agmadera.mitienda.services.GanaciaVentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GananciaVentaServiceImpl implements GanaciaVentaService {

    private final GananciaVentaRepository gananciaVentaRepository;

    @Override
    public void guardarGananciaVenta(GananciaVentaEntity gananciaVentaEntity) {
        Objects.requireNonNull(gananciaVentaEntity);
        try {
            gananciaVentaRepository.save(gananciaVentaEntity);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
