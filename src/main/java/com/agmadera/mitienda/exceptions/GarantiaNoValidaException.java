package com.agmadera.mitienda.exceptions;

public class GarantiaNoValidaException extends RuntimeException{

    public GarantiaNoValidaException() {
        super("DIAS DE GARANTIA EXPIRADOS");
    }
    public GarantiaNoValidaException(String msg) {
        super(msg);
    }
}
