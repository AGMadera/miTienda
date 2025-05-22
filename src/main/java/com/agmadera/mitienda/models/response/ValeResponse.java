package com.agmadera.mitienda.models.response;


import com.agmadera.mitienda.models.HistorialGarantiaValeDto;
import lombok.Data;

import java.util.List;

@Data
public class ValeResponse {
    private String folio;
    private float saldoInicial;
    private float saldoActual;
    private List<HistorialGarantiaValeDto> historialGarantiaValesDtos;
    private Long version;

}
