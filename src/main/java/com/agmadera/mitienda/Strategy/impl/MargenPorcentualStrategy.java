package com.agmadera.mitienda.Strategy.impl;

import com.agmadera.mitienda.Strategy.PrecioStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MargenPorcentualStrategy implements PrecioStrategy {
    private final float margenPG;
    private final float margenTecnico;

    @Autowired
    public MargenPorcentualStrategy(
            @Value("${porcentaje.pg}") float margenPG, @Value("${porcentaje.tecnico}") float margenTecnico){
        this.margenPG = margenPG;
        this.margenTecnico = margenTecnico;

    }
    @Override
    public float calcularPrecioPG(float costo) {
        return generarPrecio(costo, margenPG);
    }

    @Override
    public float calcularPrecioTecnico(float costo) {
        return generarPrecio(costo,margenTecnico);
    }

    private float generarPrecio(float costo, float porcentajeGanacia){
        return costo * (1 + porcentajeGanacia);
    }
}
