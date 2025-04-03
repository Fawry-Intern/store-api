package com.fawry.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;


@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private Long storeId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
}

