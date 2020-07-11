
package skyglass.composer.order;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static skyglass.composer.customer.CoreMatchers.named;
import static skyglass.composer.customer.CoreMatchers.with;
import static skyglass.composer.order.OrderMatchers.LineItem;
import static skyglass.composer.order.OrderMatchers.Product;
import static skyglass.composer.order.OrderMatchers.containsOrder;

import java.util.List;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.customer.Customer;
import skyglass.composer.customer.CustomerRepository;
import skyglass.composer.customer.EmailAddress;
import skyglass.composer.customer.Product;
import skyglass.composer.customer.ProductRepository;
import skyglass.composer.test.AbstractDomainTest;

/**
 * Integration tests for {@link OrderRepository}.
 * 
 * @author Oliver Gierke
 */
public class OrderRepositoryIntegrationTest extends AbstractDomainTest {

	@Autowired
	OrderRepository repository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	ProductRepository productRepository;

	@Test
	public void createOrder() {

		Customer dave = customerRepository.findByEmailAddress(new EmailAddress("dave@dmband.com"));
		Product iPad = productRepository.findById("f78c4fae-2a31-11e9-b210-d663bd873d93").get();

		Order order = new Order(dave, dave.getAddresses().iterator().next());
		order.getShippingAddress().setUuid(null);
		order.add(new LineItem(iPad));

		order = repository.save(order);
		assertThat(order.getUuid(), is(notNullValue()));
	}

	@Test
	public void readOrder() {

		Customer dave = customerRepository.findByEmailAddress(new EmailAddress("dave@dmband.com"));
		List<Order> orders = repository.findByCustomer(dave);
		Matcher<Iterable<? super Order>> hasOrderForiPad = containsOrder(with(LineItem(with(Product(named("iPad"))))));

		//assertThat(orders, hasSize(1));
		assertThat(orders, hasOrderForiPad);
	}
}
