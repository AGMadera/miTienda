package com.agmadera.mitienda.exceptions;

public class ProductoNoEncontradoException extends RuntimeException{
    private String message;

    public ProductoNoEncontradoException() {
    }
    public ProductoNoEncontradoException(String msg) {
        super(msg);
        this.message= msg;
    }

}
