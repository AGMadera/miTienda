package com.agmadera.mitienda.models;

import java.util.Date;

public class HistorialGarantiaValeDto {
    private Long idProductoVenta;
    private float saldo;
    private Date fecha;

    // Constructor público sin argumentos
    public HistorialGarantiaValeDto() {
    }

    // Constructor privado para Builder
    private HistorialGarantiaValeDto(Builder builder) {
        this.idProductoVenta = builder.idProductoVenta;
        this.saldo = builder.saldo;
        this.fecha = builder.fecha;
    }

    // Setters públicos (necesarios para ModelMapper)
    public void setIdProductoVenta(Long idProductoVenta) {
        this.idProductoVenta = idProductoVenta;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    // Getters
    public Long getIdProductoVenta() {
        return idProductoVenta;
    }

    public float getSaldo() {
        return saldo;
    }

    public Date getFecha() {
        return fecha;
    }

    // Builder pattern
    public static class Builder {
        private Long idProductoVenta;
        private float saldo;
        private Date fecha;

        public Builder idProductoVenta(Long idProductoVenta) {
            this.idProductoVenta = idProductoVenta;
            return this;
        }

        public Builder saldo(float saldo) {
            this.saldo = saldo;
            return this;
        }

        public Builder fecha(Date fecha) {
            this.fecha = fecha;
            return this;
        }

        public HistorialGarantiaValeDto build() {
            return new HistorialGarantiaValeDto(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}