package com.agmadera.mitienda.repository;

import com.agmadera.mitienda.entities.ProductoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {

    Optional<List<ProductoEntity>>  findByNombreContainsIgnoreCaseOrCompatiblesNombreContainsIgnoreCase(String modelo,String nombre);
    Optional<List<ProductoEntity>>  findByNombreContains(String modelo);

    @Query("SELECT p FROM ProductoEntity p WHERE LOWER(p.nombre) LIKE LOWER(concat('%', :nombre,'%'))")
    Page<ProductoEntity> findByNombreContainingIgnoreCase(@Param("nombre") String nombre, Pageable pageable);

    @Query("SELECT DISTINCT p FROM ProductoEntity p LEFT JOIN p.compatibles c " +
            "WHERE LOWER(p.nombre) LIKE LOWER(concat('%', :nombre,'%')) OR " +
            "LOWER(c.nombre) LIKE LOWER(concat('%', :nombre,'%'))")
    Page<ProductoEntity> findByNombreOrCompatibles(@Param("nombre") String nombre, Pageable pageable);

    @EntityGraph(attributePaths = {"historialStockEntity"})
    Page<ProductoEntity> findAll(Pageable pageable);

}
