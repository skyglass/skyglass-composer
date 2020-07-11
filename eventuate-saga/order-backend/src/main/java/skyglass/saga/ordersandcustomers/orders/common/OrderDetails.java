package skyglass.saga.ordersandcustomers.orders.common;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import io.eventuate.examples.tram.ordersandcustomers.commondomain.Money;

@Embeddable
public class OrderDetails {

	private Long customerId;

	@Embedded
	private Money orderTotal;

	public OrderDetails() {
	}

	public OrderDetails(Long customerId, Money orderTotal) {
		this.customerId = customerId;
		this.orderTotal = orderTotal;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public Money getOrderTotal() {
		return orderTotal;
	}
}
