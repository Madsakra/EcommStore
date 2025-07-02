package com.example.product_store.order;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {


    // FOR STARTING ORDER EVENT
    @Bean
    public NewTopic orderCommands(){
        return new NewTopic("order-commands",3,(short) 1);
    }

    // START PAYMENT
    @Bean
    public NewTopic paymentCommandTopic(){
        return new NewTopic("payment-commands",3,(short) 1);
    }

    // START INVENTORY EVENTS
    @Bean
    public NewTopic inventoryCommandTopic(){
        return new NewTopic("inventory-commands",3,(short) 1);
    }

    // ORDER COMPLETION
    @Bean
    public NewTopic orderTopic(){
        return new NewTopic("order-events",3,(short) 1);
    }

    //  PAYMENT COMPLETION
    @Bean
    public NewTopic paymentTopic(){
        return new NewTopic("payment-events",3,(short) 1);
    }

    // INVENTORY COMPLETION
    @Bean
    public NewTopic inventoryTopic(){
        return new NewTopic("inventory-events",3,(short) 1);
    }

    // Payment Failure to Process
    @Bean
    public NewTopic paymentFailedTopic(){
        return new NewTopic("payment-failed",3,(short) 1);
    }

    // Inventory Failure to process
    @Bean
    public NewTopic inventoryFailedTopic(){
        return new NewTopic("inventory-failed",3,(short) 1);
    }



}
