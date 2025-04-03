package com.fawry.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic StoreSagaTopic() {
        return TopicBuilder
                .name("store-events")
                .partitions(2)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder
                .name("store-updated-events")
                .partitions(2)
                .replicas(1)
                .build();
    }

}
