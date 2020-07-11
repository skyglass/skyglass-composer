package skyglass.saga.ordersandcustomers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import skyglass.saga.ordersandcustomers.orders.common.OrderDetails;
import skyglass.saga.ordersandcustomers.orders.domain.Order;
import skyglass.saga.ordersandcustomers.orders.domain.OrderRepository;
import skyglass.saga.ordersandcustomers.orders.service.OrderService;
import skyglass.saga.ordersandcustomers.orders.webapi.CreateOrderRequest;
import skyglass.saga.ordersandcustomers.orders.webapi.CreateOrderResponse;
import skyglass.saga.ordersandcustomers.orders.webapi.GetOrderResponse;

@RestController
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-06-26T23:58:22.588+02:00")
@Api(value = "orders", description = "the orders API")
@RequestMapping("/orders")
public class OrderController {

	private OrderService orderService;

	private OrderRepository orderRepository;

	@Autowired
	public OrderController(OrderService orderService, OrderRepository orderRepository) {
		this.orderService = orderService;
		this.orderRepository = orderRepository;
	}

	@ApiOperation(value = "Create a new order", nickname = "createOrder", notes = "", response = CreateOrderResponse.class, tags = { "orders", })
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Created Order", response = CreateOrderResponse.class),
			@ApiResponse(code = 405, message = "Invalid input") })
	@RequestMapping(value = { "/", "" }, produces = { "application/xml", "application/json" }, consumes = { "application/xml", "application/json" }, method = RequestMethod.POST)
	public CreateOrderResponse createOrder(
			@ApiParam(value = "Order object that needs to be created", required = true) @RequestBody CreateOrderRequest createOrderRequest) {
		Order order = orderService.createOrder(new OrderDetails(createOrderRequest.getCustomerId(), createOrderRequest.getOrderTotal()));
		return new CreateOrderResponse(order.getId());
	}

	@ApiOperation(value = "Retrieve existing Order by Id", nickname = "getOrderById", notes = "", response = GetOrderResponse.class, tags = { "orders", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved Order Slot", response = GetOrderResponse.class),
			@ApiResponse(code = 400, message = "Invalid ID supplied"),
			@ApiResponse(code = 404, message = "Order with specified ID not found") })
	@RequestMapping(value = "/{orderId}", produces = { "application/xml" }, method = RequestMethod.GET)
	public ResponseEntity<GetOrderResponse> getOrder(
			@ApiParam(value = "Order id", required = true) @PathVariable Long orderId) {
		return orderRepository
				.findById(orderId)
				.map(o -> new ResponseEntity<>(new GetOrderResponse(o.getId(), o.getState(), o.getRejectionReason()), HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
}
