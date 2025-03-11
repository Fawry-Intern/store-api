package com.fawry.store_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long id;

    @NotNull(message = "Product ID is mandatory")
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull(message = "Store is mandatory")
    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @NotNull(message = "Stock available quantity is mandatory")
    @Column(name = "stock_available_quantity", nullable = false)
    @Min(value = 0, message = "Stock available quantity must be at least 0")
    private Integer stockAvailableQuantity;

    @NotNull(message = "Stock last updated is mandatory")
    @Column(name = "stock_last_updated", nullable = false)
    @UpdateTimestamp
    private Instant stockLastUpdated;
}
