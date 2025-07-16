
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