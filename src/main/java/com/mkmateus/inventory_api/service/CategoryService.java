package com.mkmateus.inventory_api.service;

import com.mkmateus.inventory_api.dto.request.CreateCategoryRequest;
import com.mkmateus.inventory_api.dto.request.UpdateCategoryRequest;
import com.mkmateus.inventory_api.dto.response.CategoryResponse;
import com.mkmateus.inventory_api.entity.Category;
import com.mkmateus.inventory_api.exception.CategoryNotFoundException;
import com.mkmateus.inventory_api.exception.DuplicateResourceException;
import com.mkmateus.inventory_api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse create(CreateCategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException(
                    "Categoria já existe: " + request.name());
        }

        Category category = Category.builder()
                .name(request.name())
                .description(request.description())
                .build();

        return toResponse(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAllByOrderByNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(UUID id) {
        return categoryRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public CategoryResponse update(UUID id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        category.setName(request.name());
        category.setDescription(request.description());

        return toResponse(categoryRepository.save(category));
    }

    public void delete(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        categoryRepository.deleteById(id);
    }

    // método de uso interno — retorna a entidade, não o DTO
    // usado pelo ProductService para associar categoria ao produto
    public Category findEntityById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}