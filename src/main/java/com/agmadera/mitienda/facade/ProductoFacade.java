package com.agmadera.mitienda.facade;

import com.agmadera.mitienda.models.ProductoDTO;
import com.agmadera.mitienda.models.response.ProductoPGResponse;
import com.agmadera.mitienda.models.response.ProductoTecResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductoFacade {
    ProductoDTO guardarProducto(ProductoDTO dto);
    void cargaMasivaProducto(MultipartFile archivo);
    void cargaMasivaProducto(List<ProductoDTO> dtoList);

    ProductoDTO buscarId(Long id);

    List<ProductoDTO> buscarNombre(String nombre);

    //ProductoDTO actualizarProducto(ProductoDTO dto, Long id);

    List<ProductoDTO> mostrarTodos();

    List<ProductoTecResponse> buscarNombreTec(String nombre);

    ProductoDTO actualizarProducto(ProductoDTO dto);

    List<ProductoPGResponse> buscarNombrePG(String nombre);

    ProductoDTO actualizarStock(ProductoDTO dto);
    ProductoDTO actualizarProducto(ProductoDTO dto,Long id);

    List<ProductoTecResponse> mostrarTodosTec();

    List<ProductoDTO> actualizarPrecios();
}
