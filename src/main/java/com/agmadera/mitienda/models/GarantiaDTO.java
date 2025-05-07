package com.agmadera.mitienda.models;

import com.agmadera.mitienda.Enum.RazonGarantia;
import com.agmadera.mitienda.Enum.TipoGarantia;

import java.util.Date;

public class GarantiaDTO {

    private Long id;
    private Long idVenta;
    private Long idProductoVenta;
    private Date fecha;
    private RazonGarantia razonGarantia;

    private TipoGarantia tipoGarantia;

    private float cantidadReembolso;



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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public RazonGarantia getRazonGarantia() {
        return razonGarantia;
    }

    public void setRazonGarantia(RazonGarantia razonGarantia) {
        this.razonGarantia = razonGarantia;
    }

    public float getCantidadReembolso() {
        return cantidadReembolso;
    }

    public void setCantidadReembolso(float cantidadReembolso) {
        this.cantidadReembolso = cantidadReembolso;
    }

    public TipoGarantia getTipoGarantia() {
        return tipoGarantia;
    }

    public void setTipoGarantia(TipoGarantia tipoGarantia) {
        this.tipoGarantia = tipoGarantia;
    }
}
