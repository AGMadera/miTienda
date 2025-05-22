package com.agmadera.mitienda.exceptions;

public class VentaValidacionException extends RuntimeException {
    private String message;

    public VentaValidacionException() {
    }

    public VentaValidacionException(String msg) {
        super(msg);
        this.message= msg;
    }

}
