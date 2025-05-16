package com.agmadera.mitienda.exceptions.mensajes;

public class ProductoMensajesError {
    static final String MSG_EX_VALID_NOMBRE= "Nombre en blanco o nulo";
    static final String MSG_EX_VALID_PRECIOS= "Los precios deben de ser mayores a 0";
    static final String PRODUCTO_NO_ENCONTADO = "No se encontró producto con %s: %s";

    static final String COMPRAVENTA_NULO = "El producto debe tener al menos un registro de compra/venta";
    static final String PLEGABLE_NULO = "El objeto pageable no puede ser nulo";

    static final String PRECIOS_INVALIDOS = "Validación fallida: Precios inválidos - PG: {}, Técnico: {}";


    // Validación de campos
    public static final String NOMBRE_REQUERIDO = "El nombre del producto es requerido";
    public static final String MARCA_REQUERIDA = "La marca del producto es requerida";
    public static final String CALIDAD_REQUERIDA = "La calidad del producto es requerida";
    public static final String COLOR_REQUERIDO = "El color del producto es requerido";
    public static final String COSTO_INVALIDO = "El costo de referencia debe ser mayor a 0";
    public static final String PRODUCTOS_NULOS = "La lista de productos no puede ser nula o vacía";
    public static final String PRODUCTO_NULO = "El producto no puede ser nulo";

    // Relaciones
    public static final String STOCK_REQUERIDO = "El stock del producto es requerido";
    public static final String COMPATIBLES_REQUERIDOS = "Debe especificar al menos un producto compatible";
    public static final String COMPRA_VENTA_REQUERIDA = "Debe existir al menos un registro de compra/venta";

    // Búsquedas
    public static final String PRODUCTO_NO_ENCONTRADO = "No se encontró producto con %s: %s";
    public static final String PRODUCTO_NO_ENCONTRADO_NOMBRE = "No se encontraron productos con nombre: %s";
}
