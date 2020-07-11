package skyglass.saga.ordersandcustomers;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import io.eventuate.examples.tram.ordersandcustomers.commondomain.Money;
import skyglass.saga.ordersandcustomers.domain.Customer;
import skyglass.saga.ordersandcustomers.orders.common.OrderDetails;
import skyglass.saga.ordersandcustomers.orders.domain.Order;
import skyglass.saga.ordersandcustomers.orders.domain.OrderRepository;
import skyglass.saga.ordersandcustomers.orders.domain.OrderState;
import skyglass.saga.ordersandcustomers.orders.service.OrderService;
import skyglass.saga.ordersandcustomers.service.CustomerService;

public abstract class AbstractOrdersAndCustomersIntegrationTest {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Test
	public void shouldApproveOrder() throws InterruptedException {
		Money creditLimit = new Money("15.00");
		Customer customer = customerService.createCustomer("Fred", creditLimit);
		Order order = orderService.createOrder(new OrderDetails(customer.getId(), new Money("12.34")));

		assertOrderState(order.getId(), OrderState.APPROVED);
	}

	@Test
	public void shouldRejectOrder() throws InterruptedException {
		Money creditLimit = new Money("15.00");
		Customer customer = customerService.createCustomer("Fred", creditLimit);
		Order order = orderService.createOrder(new OrderDetails(customer.getId(), new Money("123.40")));

		assertOrderState(order.getId(), OrderState.REJECTED);
	}

	private void assertOrderState(Long id, OrderState expectedState) throws InterruptedException {
		Order order = null;
		for (int i = 0; i < 30; i++) {
			order = transactionTemplate
					.execute(s -> orderRepository.findById(id))
					.orElseThrow(() -> new IllegalArgumentException(String.format("Order with id %s is not found", id)));
			if (order.getState() == expectedState)
				break;
			TimeUnit.MILLISECONDS.sleep(400);
		}

		assertEquals(expectedState, order.getState());
	}
}
