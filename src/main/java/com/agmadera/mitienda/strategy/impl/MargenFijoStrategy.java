package com.agmadera.mitienda.strategy.impl;

import com.agmadera.mitienda.strategy.PrecioStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MargenFijoStrategy implements PrecioStrategy {
    private final float margenPG;
    private final float margenTecnico;

    @Autowired
    public MargenFijoStrategy(
            @Value("${precio.pg}") float margenPG, @Value("${precio.tecnico}") float margenTecnico){
        this.margenPG = margenPG;
        this.margenTecnico = margenTecnico;

    }


    @Override
    public float calcularPrecioPG(float costo) {
        return generarPrecio(costo,margenPG);
    }

    @Override
    public float calcularPrecioTecnico(float costo) {
        return generarPrecio(costo,margenTecnico);
    }

    private float generarPrecio(float costo, float margenFijoGanancia){
        double costoAjustado = Math.ceil(costo / 10) * 10;
        float ajustado = (float) costoAjustado;
        float precioPreFinal = ajustado + margenFijoGanancia;

        return (float) (Math.ceil(precioPreFinal / 10) * 10);

    }
}
