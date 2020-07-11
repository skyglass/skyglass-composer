FROM openjdk:8-jdk-alpine
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /customer-service/lib
COPY ${DEPENDENCY}/META-INF /customer-service/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /customer-service
ENTRYPOINT ["java","-cp","app:customer-service/lib/*","skyglass.saga.customers.CustomersServiceMain"]