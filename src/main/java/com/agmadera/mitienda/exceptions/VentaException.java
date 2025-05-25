package com.agmadera.mitienda.exceptions;

import com.agmadera.mitienda.models.VentaDTO;

public class VentaException extends RuntimeException {
    private final VentaDTO ventaDTO;

    public VentaException(String message, VentaDTO ventaDTO, Throwable cause) {
        super(message, cause);
        this.ventaDTO = ventaDTO;
    }

    public VentaDTO getVentaDTO() {
        return ventaDTO;
    }

}