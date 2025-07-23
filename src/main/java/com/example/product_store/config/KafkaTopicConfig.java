package com.example.product_store.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

  // KAFKA CONFIG TO DETERMINE THE NUMBER OF PARTITIONS AND REPLICAS
  // Topic : group of events
  // replicas: copy of a topic partition that resides on a different broker within a Kafka cluster
  @Bean
  public NewTopic orderCreateTopic() {
    return TopicBuilder.name("order.events").partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic orderCompletedCreateTopic() {
    return TopicBuilder.name("order-completed.events").partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic inventoryRestockTopic() {
    return TopicBuilder.name("inventory-restock.events").partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic paymentRefundCreateTopic() {
    return TopicBuilder.name("payment-refund.events").partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic notifyAdminTopic() {
    return TopicBuilder.name("notify-admin.events").partitions(3).replicas(1).build();
  }
}
