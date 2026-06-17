package com.mkmateus.inventory_api.repository;

import com.mkmateus.inventory_api.entity.Category;
import com.mkmateus.inventory_api.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    // busca paginada de produtos ativos
    Page<Product> findAllByActiveTrue(Pageable pageable);

    // busca por categoria com paginação
    Page<Product> findAllByActiveTrueAndCategory(
            Category category, Pageable pageable);

    // evita retornar produto deletado
    Optional<Product> findByIdAndActiveTrue(UUID id);

    // busca produto que tenha o termo
    @Query("""
            SELECT p FROM Product p
            WHERE p.active = true
            AND LOWER(p.name) LIKE LOWER(CONCAT('%', :term, '%'))
            """)
    Page<Product> searchByName(@Param("term") String term, Pageable pageable);

    // verifica estoque baixo — útil pra alertas
    @Query("""
            SELECT p FROM Product p
            WHERE p.active = true
            AND p.quantity <= :threshold
            """)
    Page<Product> findLowStock(@Param("threshold") int threshold, Pageable pageable);
}