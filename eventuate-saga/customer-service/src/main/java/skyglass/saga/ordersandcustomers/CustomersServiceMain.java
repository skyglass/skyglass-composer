package skyglass.saga.ordersandcustomers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.eventuate.tram.commands.producer.TramCommandProducerConfiguration;
import io.eventuate.tram.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.jdbckafka.TramJdbcKafkaConfiguration;
import io.eventuate.tram.messaging.common.ChannelMapping;
import io.eventuate.tram.messaging.common.DefaultChannelMapping;
import io.eventuate.tram.sagas.orchestration.SagaOrchestratorConfiguration;
import skyglass.saga.ordersandcustomers.web.CustomerWebConfiguration;

@SpringBootApplication
@Configuration
@Import({ CustomerConfiguration.class,
		CustomerWebConfiguration.class,
		TramEventsPublisherConfiguration.class,
		TramCommandProducerConfiguration.class,
		SagaOrchestratorConfiguration.class,
		TramJdbcKafkaConfiguration.class })
@ComponentScan
public class CustomersServiceMain {

	@Bean
	public ChannelMapping channelMapping() {
		return DefaultChannelMapping.builder().build();
	}

	public static void main(String[] args) {
		SpringApplication.run(CustomersServiceMain.class, args);
	}
}
