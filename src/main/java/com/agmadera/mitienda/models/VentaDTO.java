package com.agmadera.mitienda.models;


import com.agmadera.mitienda.Enum.TipoPago;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class VentaDTO {

    private Long id;
    private List<ProductoVentaDTO> productoVentaDTOS;
    private float totalGenrealAntesDesc;
    private float descuentosEnTotalGen;
    private float totalGenreal;
    private Date fechaVenta;
    private TipoPago tipoPago;
    private String mensaje;
    private boolean tecnico;
    private String folioGarantia;


}
