package com.fawry.kafka.producers;

import com.fawry.kafka.events.OrderCreatedEventDTO;
import com.fawry.kafka.events.StoreCreatedEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.kafka.support.KafkaHeaders.PARTITION;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreUpdatedPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishStoreUpdatedEvent(StoreCreatedEventDTO createdEvent) {
        log.info("Publish order event created to store to reserve the orderItems {}: ", createdEvent);

        int orderHash = hash(createdEvent.getOrderId());
        Message<StoreCreatedEventDTO> message =
                MessageBuilder
                        .withPayload(createdEvent)
                        .setHeader(TOPIC, "store-updated-events")
                        .setHeader(PARTITION, randPartitions(orderHash))
                        .build();
        kafkaTemplate.send(message);
    }

    private int hash(long orderId) {
        return Objects.hash(orderId);
    }
    private int randPartitions(int orderHash) {
        return orderHash % 2;
    }
}
