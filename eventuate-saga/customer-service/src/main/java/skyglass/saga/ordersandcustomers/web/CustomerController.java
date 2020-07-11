package skyglass.saga.ordersandcustomers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import skyglass.saga.ordersandcustomers.customers.webapi.CreateCustomerRequest;
import skyglass.saga.ordersandcustomers.customers.webapi.CreateCustomerResponse;
import skyglass.saga.ordersandcustomers.domain.Customer;
import skyglass.saga.ordersandcustomers.service.CustomerService;

@RestController
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-06-26T23:58:22.588+02:00")
@Api(value = "customers", description = "the customers API")
@RequestMapping("/customers")
public class CustomerController {

	private CustomerService customerService;

	@Autowired
	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@ApiOperation(value = "Add a new customer", nickname = "createCustomer", notes = "", response = CreateCustomerResponse.class, tags = { "customers", })
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Created Customer", response = CreateCustomerResponse.class),
			@ApiResponse(code = 405, message = "Invalid input") })
	@RequestMapping(value = { "/", "" }, produces = { "application/xml", "application/json" }, consumes = { "application/xml", "application/json" }, method = RequestMethod.POST)
	public CreateCustomerResponse createCustomer(
			@ApiParam(value = "Customer object that needs to be added", required = true) @RequestBody CreateCustomerRequest createCustomerRequest) {
		Customer customer = customerService.createCustomer(createCustomerRequest.getName(), createCustomerRequest.getCreditLimit());
		return new CreateCustomerResponse(customer.getId());
	}
}
