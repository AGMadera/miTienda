package com.agmadera.mitienda.exceptions;

public class ProductoValidacionException extends RuntimeException{
    private String message;

    public ProductoValidacionException() {
    }

    public ProductoValidacionException(String msg){
        super(msg);
        this.message=msg;
    }
}
