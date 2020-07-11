package skyglass.saga.ordersandcustomers.orders.service;

import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import skyglass.saga.ordersandcustomers.orders.common.OrderDetails;
import skyglass.saga.ordersandcustomers.orders.domain.Order;
import skyglass.saga.ordersandcustomers.orders.domain.OrderRepository;
import skyglass.saga.ordersandcustomers.orders.sagas.createorder.CreateOrderSaga;
import skyglass.saga.ordersandcustomers.orders.sagas.createorder.CreateOrderSagaData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class OrderService {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private SagaInstanceFactory sagaInstanceFactory;

  @Autowired
  private CreateOrderSaga createOrderSaga;

  public OrderService(OrderRepository orderRepository, SagaInstanceFactory sagaInstanceFactory, CreateOrderSaga createOrderSaga) {
    this.orderRepository = orderRepository;
    this.sagaInstanceFactory = sagaInstanceFactory;
    this.createOrderSaga = createOrderSaga;
  }

  @Transactional
  public Order createOrder(OrderDetails orderDetails) {
    CreateOrderSagaData data = new CreateOrderSagaData(orderDetails);
    sagaInstanceFactory.create(createOrderSaga, data);
    return orderRepository.findById(data.getOrderId()).get();
  }

}
