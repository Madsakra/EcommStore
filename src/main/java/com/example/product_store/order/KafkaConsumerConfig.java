package com.example.product_store.order;

import com.example.product_store.order.dto.KafkaOrderGroup;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;
@Configuration
public class KafkaConsumerConfig {
    @Bean
    public ConsumerFactory<String, KafkaOrderGroup> kafkaOrderGroupConsumerFactory() {
        JsonDeserializer<KafkaOrderGroup> deserializer = new JsonDeserializer<>(KafkaOrderGroup.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                        ConsumerConfig.GROUP_ID_CONFIG, "admin-group",
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class
                ),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaOrderGroup> kafkaOrderGroupListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaOrderGroup> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaOrderGroupConsumerFactory());
        return factory;
    }
}
