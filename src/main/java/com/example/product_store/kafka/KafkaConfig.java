package com.example.product_store.kafka;

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

    // ORDER PASS / FAIL
    @Bean
    public NewTopic orderTopic(){
        return new NewTopic("order-events",3,(short) 1);
    }

    //  PAYMENT PASS / FAIL
    @Bean
    public NewTopic paymentTopic(){
        return new NewTopic("payment-events",3,(short) 1);
    }

    // INVENTORY PASS /FAIL
    @Bean
    public NewTopic inventoryTopic(){
        return new NewTopic("inventory-events",3,(short) 1);
    }


}
