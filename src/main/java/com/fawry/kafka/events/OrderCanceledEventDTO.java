package com.fawry.kafka.events;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class OrderCanceledEventDTO implements Serializable {

    private Long orderId;
    private String reason;
    private String customerEmail;



    public static OrderCanceledEventDTO newInstance(Long orderId, String reason, String customerEmail) {
        return new OrderCanceledEventDTO(orderId, reason, customerEmail);
    }

}
