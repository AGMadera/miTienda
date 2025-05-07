package com.agmadera.mitienda.models.response;


import com.agmadera.mitienda.models.HistorialGarantiaValeDto;
import java.util.List;

public class ValeResponse {
    private String folio;
    private float saldoInicial;
    private float saldoActual;
    private List<HistorialGarantiaValeDto> historialGarantiaValesDtos;

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public float getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(float saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public float getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(float saldoActual) {
        this.saldoActual = saldoActual;
    }

    public List<HistorialGarantiaValeDto> getHistorialGarantiaValesDtos() {
        return historialGarantiaValesDtos;
    }

    public void setHistorialGarantiaValesDtos(List<HistorialGarantiaValeDto> historialGarantiaValesDtos) {
        this.historialGarantiaValesDtos = historialGarantiaValesDtos;
    }
}
