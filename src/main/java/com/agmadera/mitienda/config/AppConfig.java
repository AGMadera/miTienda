package com.agmadera.mitienda.config;

import com.agmadera.mitienda.Strategy.PrecioStrategy;
import com.agmadera.mitienda.Strategy.impl.MargenFijoStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    public PrecioStrategy precioStrategy(@Value("${precio.pg}") float margenPG, @Value("${precio.tecnico}") float margenTecnico){
        return new MargenFijoStrategy(margenPG,margenTecnico);
    }
}
