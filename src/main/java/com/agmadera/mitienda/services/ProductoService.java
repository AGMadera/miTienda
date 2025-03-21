package com.agmadera.mitienda.services;

import com.agmadera.mitienda.entities.ProductoEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
    ProductoEntity guardar(ProductoEntity producto);
    Optional<List<ProductoEntity>> buscarModelo(String modelo);
    Optional<ProductoEntity> buscarId(long id);
    List<ProductoEntity> cargaMasiva(List<ProductoEntity> list);

    List<ProductoEntity> buscarNombre(String nombre);

    List<ProductoEntity> mostrarTodos();

}
