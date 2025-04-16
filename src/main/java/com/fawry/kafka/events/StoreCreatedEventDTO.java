package com.fawry.kafka.events;

import com.fawry.kafka.dto.AddressDetails;
import com.fawry.kafka.dto.PaymentMethod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@RequiredArgsConstructor
@Getter
public class StoreCreatedEventDTO implements Serializable {
    private final Long orderId;
    private final Long userId;
    private final String status;
    private final String customerEmail;
    private final String customerName;
    private final String customerContact;
    private final AddressDetails addressDetails;
    private final BigDecimal paymentAmount;
    private final PaymentMethod paymentMethod;
    private final String merchantEmail;

    public static StoreCreatedEventDTO newInstance(Long orderId,
                                                   Long userId,
                                                   String status,
                                                   String customerEmail,
                                                   String customerName,
                                                   String customerContact,
                                                   AddressDetails addressDetails,
                                                   BigDecimal paymentAmount,
                                                   PaymentMethod paymentMethod,
                                                   String merchantEmail
    ) {
        return new StoreCreatedEventDTO(orderId, userId, status, customerEmail, customerName, customerContact, addressDetails, paymentAmount, paymentMethod,merchantEmail);
    }
}
