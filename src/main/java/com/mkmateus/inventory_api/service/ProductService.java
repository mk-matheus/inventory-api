package com.mkmateus.inventory_api.service;

import com.mkmateus.inventory_api.dto.request.CreateProductRequest;
import com.mkmateus.inventory_api.dto.request.UpdateProductRequest;
import com.mkmateus.inventory_api.dto.response.CategoryResponse;
import com.mkmateus.inventory_api.dto.response.ProductResponse;
import com.mkmateus.inventory_api.entity.Category;
import com.mkmateus.inventory_api.entity.Product;
import com.mkmateus.inventory_api.exception.ProductNotFoundException;
import com.mkmateus.inventory_api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductResponse create(CreateProductRequest request) {
        Category category = null;
        if (request.categoryId() != null) {
            // CategoryService já lança exceção se não encontrar
            category = categoryService.findEntityById(request.categoryId());
        }

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .quantity(request.quantity())
                .category(category)
                .active(true)
                .build();

        return toResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAll(Pageable pageable) {
        return productRepository.findAllByActiveTrue(pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String term, Pageable pageable) {
        return productRepository
                .findAllByActiveTrueAndNameContainingIgnoreCase(term, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findByCategory(UUID categoryId, Pageable pageable) {
        Category category = categoryService.findEntityById(categoryId);
        return productRepository.findAllByActiveTrueAndCategory(category, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findLowStock(int threshold, Pageable pageable) {
        return productRepository
                .findAllByActiveTrueAndQuantityLessThanEqual(threshold, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(UUID id) {
        return productRepository.findByIdAndActiveTrue(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public ProductResponse update(UUID id, UpdateProductRequest request) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        Category category = null;
        if (request.categoryId() != null) {
            category = categoryService.findEntityById(request.categoryId());
        }

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(category);

        return toResponse(productRepository.save(product));
    }

    // soft delete — mantém histórico, só desativa o produto
    public void deactivate(UUID id) {
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setActive(false);
        productRepository.save(product);
    }

    // chamado pelo StockEntryService — não expõe DTO, trabalha com entidade
    public void updateQuantity(Product product, int newQuantity) {
        product.setQuantity(newQuantity);
        productRepository.save(product);
    }

    // retorna entidade — uso interno entre services
    public Product findEntityById(UUID id) {
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private ProductResponse toResponse(Product product) {
        CategoryResponse categoryResponse = null;

        if (product.getCategory() != null) {
            categoryResponse = new CategoryResponse(
                    product.getCategory().getId(),
                    product.getCategory().getName(),
                    product.getCategory().getDescription()
            );
        }

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                categoryResponse,
                product.isActive(),
                product.getCreatedAt()
        );
    }
}