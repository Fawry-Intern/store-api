package com.fawry.store_api.entity;

import com.fawry.store_api.enums.ReservationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

import static jakarta.persistence.EnumType.STRING;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "inventory_reservation ")
public class InventoryReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "Product ID is mandatory")
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull(message = "Order ID is mandatory")
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @NotNull(message = "Reserved quantity is mandatory")
    @Min(value = 0, message = "Reserved quantity must be at least 0")
    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity;

    @NotNull(message = "Status is mandatory")
    @Column(name = "status", nullable = false, length = 10)
    @Enumerated(STRING)
    private ReservationStatus status;

    @Column(name = "reserve_inventory_last_updated", nullable = false)
    @UpdateTimestamp
    private Instant reserveInventoryLastUpdated;
}

