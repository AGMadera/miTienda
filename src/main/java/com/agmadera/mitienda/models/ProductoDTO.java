package com.agmadera.mitienda.models;


import com.agmadera.mitienda.exceptions.mensajes.ProductoMensajesError;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ProductoDTO {

    private Long id;
    @NotBlank(message = ProductoMensajesError.NOMBRE_REQUERIDO)
    private String nombre;
    @NotBlank(message = ProductoMensajesError.CALIDAD_REQUERIDA)
    private String calidad;
    private boolean marco;
    private String marca;
    private String color;
    private float costoReferencia;

    private boolean usarCostoReferencia;
    private List<CompatibleDTO> compatibles;
    private StockDTO stockDTO;
    private List<HistorialStockDTO> historialStockDTOS;
    private List<CompraVentaDTO> compraVentaDTOS;


}
