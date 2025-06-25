package com.example.product_store;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class ProductStoreApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProductStoreApplication.class, args);
  }
}
