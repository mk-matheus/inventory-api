package com.mkmateus.inventory_api.repository;

import com.mkmateus.inventory_api.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    // útil pra dropdowns no frontend
    List<Category> findAllByOrderByNameAsc();

    // valida duplicata antes de criar
    boolean existsByNameIgnoreCase(String name);

    Optional<Category> findByNameIgnoreCase(String name);
}