package com.agmadera.mitienda.services;

import com.agmadera.mitienda.entities.ProductoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductoService {
    ProductoEntity guardar(ProductoEntity producto);
    List<ProductoEntity> buscarModelo(String modelo);
    ProductoEntity buscarId(long id);
    List<ProductoEntity> cargaMasiva(List<ProductoEntity> list);

    List<ProductoEntity> buscarNombre(String nombre);

    List<ProductoEntity> mostrarTodos();

    Page<ProductoEntity> mostrarTodosPaginados(Pageable pageable);

    // Método existente modificado para paginación
    Page<ProductoEntity> buscarNombre(String nombre, Pageable pageable);

    // Nuevo método para búsqueda paginada con compatibles
    Page<ProductoEntity> buscarNombreOCompatibles(String nombre, Pageable pageable);

}
