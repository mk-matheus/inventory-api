package com.mkmateus.inventory_api.service;

import com.mkmateus.inventory_api.dto.request.StockEntryRequest;
import com.mkmateus.inventory_api.dto.response.StockEntryResponse;
import com.mkmateus.inventory_api.entity.Product;
import com.mkmateus.inventory_api.entity.StockEntry;
import com.mkmateus.inventory_api.exception.InsufficientStockException;
import com.mkmateus.inventory_api.repository.StockEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StockEntryService {

    private final StockEntryRepository stockEntryRepository;
    private final ProductService productService;

    public StockEntryResponse register(UUID productId, StockEntryRequest request) {
        Product product = productService.findEntityById(productId);

        int currentQuantity = product.getQuantity();
        int newQuantity;

        if (request.type() == StockEntry.Type.IN) {
            newQuantity = currentQuantity + request.quantity();
        } else {
            // regra de negócio: não pode sair mais do que tem
            if (request.quantity() > currentQuantity) {
                throw new InsufficientStockException(
                        product.getName(),
                        currentQuantity,
                        request.quantity()
                );
            }
            newQuantity = currentQuantity - request.quantity();
        }

        // atualiza o estoque do produto
        productService.updateQuantity(product, newQuantity);

        // registra a movimentação
        StockEntry entry = StockEntry.builder()
                .product(product)
                .type(request.type())
                .quantity(request.quantity())
                .reason(request.reason())
                .build();

        return toResponse(stockEntryRepository.save(entry));
    }

    @Transactional(readOnly = true)
    public Page<StockEntryResponse> findByProduct(UUID productId, Pageable pageable) {
        Product product = productService.findEntityById(productId);
        return stockEntryRepository
                .findAllByProductOrderByCreatedAtDesc(product, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<StockEntryResponse> findByProductAndType(
            UUID productId, StockEntry.Type type, Pageable pageable) {

        Product product = productService.findEntityById(productId);
        return stockEntryRepository
                .findAllByProductAndTypeOrderByCreatedAtDesc(product, type, pageable)
                .map(this::toResponse);
    }

    private StockEntryResponse toResponse(StockEntry entry) {
        return new StockEntryResponse(
                entry.getId(),
                entry.getProduct().getId(),
                entry.getProduct().getName(),
                entry.getType(),
                entry.getQuantity(),
                entry.getReason(),
                entry.getCreatedAt()
        );
    }
}