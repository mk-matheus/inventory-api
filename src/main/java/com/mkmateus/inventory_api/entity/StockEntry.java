package com.mkmateus.inventory_api.entity;

import com.mkmateus.inventory_api.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockEntry extends BaseEntity {

    public enum Type { IN, OUT }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Type type;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 255)
    private String reason;
}