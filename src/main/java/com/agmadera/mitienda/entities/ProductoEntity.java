package com.agmadera.mitienda.entities;

import com.agmadera.mitienda.exceptions.mensajes.ProductoMensajesError;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Getter
@Setter
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = ProductoMensajesError.NOMBRE_REQUERIDO)
    private String nombre;
    private String marca;
    @NotBlank(message = ProductoMensajesError.CALIDAD_REQUERIDA)
    private String calidad;
    private boolean marco;
    private String color;
    @Min(value = 0, message = ProductoMensajesError.COSTO_INVALIDO)
    private float costoReferencia;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "compatible_id", referencedColumnName = "id")
    private List<CompatibleEntity> compatibles;

    @Valid
    @NotNull(message = ProductoMensajesError.STOCK_REQUERIDO)
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "stock_id", referencedColumnName = "id")
    private StockEntity stockEntity;
    @Valid
    @NotEmpty(message = ProductoMensajesError.COMPRA_VENTA_REQUERIDA)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "compatible_id", referencedColumnName = "id")
    private List<CompraVentaEntity> compraVentaEntity;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "historialStock_id", referencedColumnName = "id")
    private List<HistorialStockEntity> historialStockEntity;

}
