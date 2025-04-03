package com.fawry.kafka.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
class PaymentDetails {
    private String number;
    private String cvv;
    private String expiry;
}
