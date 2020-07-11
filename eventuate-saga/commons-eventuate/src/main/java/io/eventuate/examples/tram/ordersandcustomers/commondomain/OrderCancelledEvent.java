package io.eventuate.examples.tram.ordersandcustomers.commondomain;

public class OrderCancelledEvent implements OrderEvent {

	private OrderDetails orderDetails;

	public OrderCancelledEvent() {
	}

	public OrderCancelledEvent(OrderDetails orderDetails) {
		this.orderDetails = orderDetails;
	}

	public OrderDetails getOrderDetails() {
		return orderDetails;
	}
}
