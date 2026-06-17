package com.mkmateus.inventory_api.repository;

import com.mkmateus.inventory_api.entity.Product;
import com.mkmateus.inventory_api.entity.StockEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StockEntryRepository extends JpaRepository<StockEntry, UUID> {

    // historico de movimentacoes
    Page<StockEntry> findAllByProductOrderByCreatedAtDesc(
            Product product, Pageable pageable);

    // filtra por tipo de movimentação (IN ou OUT)
    Page<StockEntry> findAllByProductAndTypeOrderByCreatedAtDesc(
            Product product, StockEntry.Type type, Pageable pageable);

    // soma total de entradas de um produto
    @Query("""
            SELECT COALESCE(SUM(s.quantity), 0) FROM StockEntry s
            WHERE s.product = :product
            AND s.type = 'IN'
            """)
    Integer sumEntriesByProduct(@Param("product") Product product);

    // soma total de saídas de um produto
    @Query("""
            SELECT COALESCE(SUM(s.quantity), 0) FROM StockEntry s
            WHERE s.product = :product
            AND s.type = 'OUT'
            """)
    Integer sumExitsByProduct(@Param("product") Product product);
}