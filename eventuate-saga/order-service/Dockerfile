FROM openjdk:8-jdk-alpine
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /order-service/lib
COPY ${DEPENDENCY}/META-INF /order-service/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /order-service
ENTRYPOINT ["java","-cp","app:order-service/lib/*","skyglass.saga.orders.OrdersServiceMain"]