# 📦 EcommStore – First Release

## Description
EcommStore is a modular Spring Boot-based e-commerce backend focusing on reliable Kafka-based order processing. It uses the outbox pattern, Debezium CDC, and transactional message delivery to guarantee at-least-once event delivery. This release lays the groundwork for scalable, decoupled order workflows.

## 🔧 Highlights & Techniques
- **Transactional Outbox pattern**: encapsulates business logic and event persistence in a single `@Transactional` boundary.
- **Debezium + MySQL binlog**: streams outbox table changes to Kafka—no polling needed.
- **Kafka ordering guarantees**: leverages single-partition topics and key-based partitioning. See MDN’s [Partitioning](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Map) (used conceptually).
- **@CacheEvict** in `CreateCategoryService` for cache invalidation of `GET_ALL_CATEGORIES`.
- **Custom serializers** for Kafka messages ensuring consistent schemas across services.
- **Graceful shutdown hooks** in the Kafka consumer to allow clean offset commits.

## 🧩 Not-Obvious Libraries & Tech
- **Debezium Connector for MySQL**
- **Spring Kafka** and **Kafka Streams**
- **Spring Cache** (backed by Redis or Caffeine)
- **JPA** for mapping database entities to Java objects
- **Liquibase** for managing database schema changes
- **JUnit** for lightweight unit testing

## 🔗 External Dependencies
- [Debezium MySQL Connector](https://debezium.io/)
- [Spring Kafka](https://spring.io/projects/spring-kafka)
- [Redis](https://redis.io/) or [Caffeine](https://github.com/ben-manes/caffeine)

For CDC order flow:
To start debezium listener, register the json:
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @register-mysql.json

To delete debezium listener
curl -X DELETE http://localhost:8083/connectors/store-connect


To use on localhost, application properties will need to change:

1. spring.datasource.url=jdbc:mysql://localhost:3307/product_store
2. spring.data.redis.host=localhost
3. spring.kafka.bootstrap-servers=localhost:29092

To use on docker:
1. spring.datasource.url=jdbc:mysql://mysql:3306/product_store
2. spring.data.redis.host=redis
3. spring.kafka.bootstrap-servers=kafka:9092