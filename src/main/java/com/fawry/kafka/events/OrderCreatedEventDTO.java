package com.fawry.kafka.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fawry.kafka.dto.AddressDetails;
import com.fawry.kafka.dto.OrderItemDTO;
import com.fawry.kafka.dto.PaymentMethod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;



@Getter
@ToString
public class OrderCreatedEventDTO implements Serializable {
    private final Long orderId;
    private final Long userId;
    private final String sagaEventType;
    private final String status;
    private final String customerEmail;
    private final String customerName;
    private final String customerContact;
    private final AddressDetails addressDetails;
    private final BigDecimal paymentAmount;
    private final List<OrderItemDTO> orderItems;
    private final PaymentMethod paymentMethod;


    @JsonCreator
    public OrderCreatedEventDTO(@JsonProperty("orderId") Long orderId,
                                @JsonProperty("userId") Long userId,
                                @JsonProperty("sagaEventType") String sagaEventType,
                                @JsonProperty("status") String status,
                                @JsonProperty("customerEmail") String customerEmail,
                                @JsonProperty("customerName") String customerName,
                                @JsonProperty("customerContact") String customerContact,
                                @JsonProperty("addressDetails") AddressDetails addressDetails,
                                @JsonProperty("paymentAmount") BigDecimal paymentAmount,
                                @JsonProperty("orderItems") List<OrderItemDTO> orderItems,
                                @JsonProperty("paymentMethod") PaymentMethod paymentMethod) {
        this.orderId = orderId;
        this.userId = userId;
        this.sagaEventType = sagaEventType;
        this.status = status;
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.customerContact = customerContact;
        this.addressDetails = addressDetails;
        this.paymentAmount = paymentAmount;
        this.orderItems = orderItems;
        this.paymentMethod = paymentMethod;
    }

    public static OrderCreatedEventDTO newInstance(Long orderId,
                                                   Long userId,
                                                   String sagaEventType,
                                                   String status,
                                                   String customerEmail,
                                                   String customerName,
                                                   String customerContact,
                                                   AddressDetails addressDetails,
                                                   BigDecimal paymentAmount,
                                                   List<OrderItemDTO> orderItems,
                                                   PaymentMethod paymentMethod) {
        return new OrderCreatedEventDTO(orderId, userId, sagaEventType, status, customerEmail, customerName, customerContact, addressDetails, paymentAmount, orderItems, paymentMethod);
    }

}
