package com.agmadera.mitienda.entities;

import com.agmadera.mitienda.Enum.RazonGarantia;
import jakarta.persistence.*;

import java.util.Date;

@MappedSuperclass
public abstract class Garantia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idVenta;
    private Long idProductoVenta;
    private Date fecha;

    @Enumerated(EnumType.STRING)
    private RazonGarantia razonGarantia;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
    }

    public Long getIdProductoVenta() {
        return idProductoVenta;
    }

    public void setIdProductoVenta(Long idProductoVenta) {
        this.idProductoVenta = idProductoVenta;
    }

    public RazonGarantia getRazonGarantia() {
        return razonGarantia;
    }

    public void setRazonGarantia(RazonGarantia razonGarantia) {
        this.razonGarantia = razonGarantia;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
