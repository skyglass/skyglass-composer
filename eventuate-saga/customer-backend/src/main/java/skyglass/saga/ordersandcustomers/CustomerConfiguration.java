package skyglass.saga.ordersandcustomers;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.eventuate.tram.commands.consumer.CommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import io.eventuate.tram.sagas.participant.SagaParticipantConfiguration;
import skyglass.saga.ordersandcustomers.domain.CustomerRepository;
import skyglass.saga.ordersandcustomers.service.CustomerCommandHandler;
import skyglass.saga.ordersandcustomers.service.CustomerService;

@Configuration
@Import(SagaParticipantConfiguration.class)
@EnableJpaRepositories
@EnableAutoConfiguration
public class CustomerConfiguration {

	@Bean
	public CustomerService customerService(CustomerRepository customerRepository) {
		return new CustomerService(customerRepository);
	}

	@Bean
	public CustomerCommandHandler customerCommandHandler(CustomerService customerService) {
		return new CustomerCommandHandler(customerService);
	}

	// TODO Exception handler for CustomerCreditLimitExceededException

	@Bean
	public CommandDispatcher consumerCommandDispatcher(CustomerCommandHandler target,
			SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {

		return sagaCommandDispatcherFactory.make("customerCommandDispatcher", target.commandHandlerDefinitions());
	}

}
