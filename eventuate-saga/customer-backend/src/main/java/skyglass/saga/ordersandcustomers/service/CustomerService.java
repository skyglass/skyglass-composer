package skyglass.saga.ordersandcustomers.service;

import io.eventuate.examples.tram.ordersandcustomers.commondomain.Money;
import skyglass.saga.ordersandcustomers.domain.Customer;
import skyglass.saga.ordersandcustomers.domain.CustomerCreditLimitExceededException;
import skyglass.saga.ordersandcustomers.domain.CustomerNotFoundException;
import skyglass.saga.ordersandcustomers.domain.CustomerRepository;

public class CustomerService {

	private CustomerRepository customerRepository;

	public CustomerService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	public Customer createCustomer(String name, Money creditLimit) {
		Customer customer = new Customer(name, creditLimit);
		return customerRepository.save(customer);
	}

	public void reserveCredit(long customerId, long orderId, Money orderTotal) throws CustomerCreditLimitExceededException {
		Customer customer = customerRepository.findById(customerId).orElseThrow(CustomerNotFoundException::new);
		customer.reserveCredit(orderId, orderTotal);
	}
}
