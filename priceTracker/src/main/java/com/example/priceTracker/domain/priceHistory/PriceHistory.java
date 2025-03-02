package com.example.priceTracker.domain.priceHistory;

import com.example.priceTracker.domain.common.BaseEntity;
import com.example.priceTracker.domain.enums.PriceStatus;
import com.example.priceTracker.domain.product.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicUpdate
@DynamicInsert
public class PriceHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PriceStatus status;

    @Column(length = 255)
    private String region;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

}
