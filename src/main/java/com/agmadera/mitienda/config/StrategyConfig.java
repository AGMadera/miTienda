package com.agmadera.mitienda.config;

import com.agmadera.mitienda.strategy.PrecioStrategy;
import com.agmadera.mitienda.strategy.impl.MargenFijoStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StrategyConfig {
    //@Bean("margenFijoStrategy")
    public PrecioStrategy precioStrategy(@Value("${precio.pg}") float margenPG, @Value("${precio.tecnico}") float margenTecnico){
        return new MargenFijoStrategy(margenPG,margenTecnico);
    }
}
