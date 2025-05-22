package com.agmadera.mitienda.entities;

import com.agmadera.mitienda.Enum.TipoPago;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class VentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_venta_id", referencedColumnName = "id")
    private List<ProductoVentaEntity> productoVentaEntity;
    private float totalGenrealAntesDesc;
    private float descuentosEnTotalGen;
    private float totalGenreal;
    private Date fechaVenta;

    @Enumerated(EnumType.STRING)
    private TipoPago tipoPago;

    private  boolean tecnico;


}
