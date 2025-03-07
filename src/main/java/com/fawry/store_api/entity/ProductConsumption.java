package com.fawry.store_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "product_consumptions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consumption_id")
    private Long consumptionId;

    @NotNull(message = "Product ID is mandatory")
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull(message = "Store is mandatory")
    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false, referencedColumnName = "store_id")
    private Store store;

    @NotNull(message = "Product price is mandatory")
    @Column(name = "product_price", nullable = false, precision = 10, scale = 2)
    @Min(value = 0, message = "Product price must be at least 0")
    private BigDecimal productPrice;


    @NotNull(message = "Consumption quantity is mandatory")
    @Column(name = "consumption_quantity", nullable = false)
    private Integer consumptionQuantity;

    @Column(name = "consumption_date", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime consumptionDate;
}
