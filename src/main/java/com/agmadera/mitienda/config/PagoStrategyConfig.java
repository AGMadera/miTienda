package com.agmadera.mitienda.config;

import com.agmadera.mitienda.Enum.TipoPago;
import com.agmadera.mitienda.facade.impl.helper.ValeHelper;
import com.agmadera.mitienda.strategies.PagoStrategy;
import com.agmadera.mitienda.strategies.impl.PagoEfectivoStrategy;
import com.agmadera.mitienda.strategies.impl.PagoTerminalStrategy;
import com.agmadera.mitienda.strategies.impl.PagoTransferenciaStrategy;
import com.agmadera.mitienda.strategies.impl.PagoValeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PagoStrategyConfig {

    private final ValeHelper valeHelper; // Dependencia requerida

    @Bean
    public PagoValeStrategy pagoValeStrategy() {
        return new PagoValeStrategy(valeHelper); // Pasa la dependencia requerida
    }

    @Bean
    public PagoEfectivoStrategy pagoEfectivoStrategy() {
        return new PagoEfectivoStrategy(/* dependencias si las necesita */);
    }

    @Bean
    public PagoTransferenciaStrategy pagoTransferenciaStrategy() {
        return new PagoTransferenciaStrategy (/* dependencias si las necesita */);
    }
    @Bean
    public PagoTerminalStrategy pagoTerminalStrategy() {
        return new PagoTerminalStrategy (/* dependencias si las necesita */);
    }
    // ... otros beans de estrategias

    @Bean
    public Map<TipoPago, PagoStrategy> paymentStrategies(
            PagoValeStrategy pagoValeStrategy,
            PagoEfectivoStrategy pagoEfectivoStrategy,
            PagoTerminalStrategy pagoTerminalStrategy,
            PagoTransferenciaStrategy pagoTransferenciaStrategy) {

        return Map.of(
                TipoPago.VALE, pagoValeStrategy,
                TipoPago.EFECTIVO, pagoEfectivoStrategy,
                TipoPago.TERMINAL, pagoTerminalStrategy,
                TipoPago.TRANSFERENCIA, pagoTransferenciaStrategy
        );
    }
}
