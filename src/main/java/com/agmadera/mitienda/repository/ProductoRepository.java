package com.agmadera.mitienda.repository;

import com.agmadera.mitienda.entities.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {

    Optional<List<ProductoEntity>>  findByNombreContainsIgnoreCaseOrCompatiblesNombreContainsIgnoreCase(String modelo,String nombre);
    Optional<List<ProductoEntity>>  findByNombreContains(String modelo);

}
