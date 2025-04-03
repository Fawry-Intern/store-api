package com.fawry.kafka.producers;

import com.fawry.kafka.events.OrderCanceledEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreCancellationPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCanceledEvent(OrderCanceledEventDTO canceledEvent) {
        Message<OrderCanceledEventDTO> message =
                MessageBuilder
                        .withPayload(canceledEvent)
                        .setHeader(TOPIC, "store-events")
                        .build();
        kafkaTemplate.send(message);
        log.info("Store cancellation process successfully {}", canceledEvent);
    }
}
