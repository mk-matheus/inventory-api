package com.mkmateus.inventory_api.repository;

import com.mkmateus.inventory_api.entity.Category;
import com.mkmateus.inventory_api.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findAllByActiveTrue(Pageable pageable);

    Page<Product> findAllByActiveTrueAndCategory(
            Category category, Pageable pageable);

    Optional<Product> findByIdAndActiveTrue(UUID id);

    Page<Product> findAllByActiveTrueAndNameContainingIgnoreCase(
            String name, Pageable pageable);

    Page<Product> findAllByActiveTrueAndQuantityLessThanEqual(
            int threshold, Pageable pageable);
}