package skyglass.saga.ordersandcustomers;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.eventuate.tram.jdbckafka.TramJdbcKafkaConfiguration;

@Configuration
@Import({ OrdersAndCustomersIntegrationCommonIntegrationTestConfiguration.class, TramJdbcKafkaConfiguration.class })
public class OrdersAndCustomersIntegrationTestConfiguration {

}
