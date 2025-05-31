package com.agmadera.mitienda.strategies.impl;

import com.agmadera.mitienda.strategies.PrecioStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MargenMixtoStrategy implements PrecioStrategy {
    private final float margenPG;
    private final float margenTecnico;
    public MargenMixtoStrategy(@Value("${porcentaje.pg}") float margenPG, @Value("${precio.tecnico}") float margenTecnico) {
        this.margenPG = margenPG;
        this.margenTecnico = margenTecnico;
    }

    @Override
    public float calcularPrecioPG(float costo) {

        return redondearArriba(generarPrecioPorcentual(costo, margenPG));
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

    private float generarPrecioPorcentual(float costo, float porcentajeGanacia){
        return costo * (1 + porcentajeGanacia);
    }
    private float redondearArriba(float valor) {
        return (float) (Math.ceil(valor /10)* 10);
    }
}
