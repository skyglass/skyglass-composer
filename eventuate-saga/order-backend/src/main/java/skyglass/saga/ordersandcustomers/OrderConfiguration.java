package skyglass.saga.ordersandcustomers;

import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import io.eventuate.tram.sagas.orchestration.SagaOrchestratorConfiguration;
import skyglass.saga.ordersandcustomers.orders.domain.OrderRepository;
import skyglass.saga.ordersandcustomers.orders.sagas.createorder.CreateOrderSaga;
import skyglass.saga.ordersandcustomers.orders.service.OrderService;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
@EnableAutoConfiguration
@Import(SagaOrchestratorConfiguration.class)
public class OrderConfiguration {

  @Bean
  public OrderService orderService(OrderRepository orderRepository, SagaInstanceFactory sagaInstanceFactory, CreateOrderSaga createOrderSaga) {
    return new OrderService(orderRepository, sagaInstanceFactory, createOrderSaga);
  }

  @Bean
  public CreateOrderSaga createOrderSaga(OrderRepository orderRepository) {
    return new CreateOrderSaga(orderRepository);
  }

}
