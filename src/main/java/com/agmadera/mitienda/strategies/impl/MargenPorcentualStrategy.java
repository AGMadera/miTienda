package com.agmadera.mitienda.strategies.impl;

import com.agmadera.mitienda.strategies.PrecioStrategy;
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
        return redondearArriba(generarPrecio(costo, margenPG));
    }

    @Override
    public float calcularPrecioTecnico(float costo) {
        return redondearArriba(generarPrecio(costo,margenTecnico));
    }

    private float generarPrecio(float costo, float porcentajeGanacia){
        return costo * (1 + porcentajeGanacia);
    }

    private float redondearArriba(float valor) {
        return (float) (Math.ceil(valor /10)* 10);
    }
}
