package com.agmadera.mitienda.exceptions;

public class VentaNoEncontradaException extends RuntimeException{
    private String message;

    public VentaNoEncontradaException() {
    }
    public VentaNoEncontradaException(String msg) {
        super(msg);
        this.message= msg;
    }
}
