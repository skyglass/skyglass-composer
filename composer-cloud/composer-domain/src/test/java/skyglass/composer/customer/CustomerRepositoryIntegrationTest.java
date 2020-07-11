package skyglass.composer.customer;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.test.AbstractDomainTest;

/**
 * Integration tests for {@link CustomerRepository}.
 * 
 * @author Oliver Gierke
 */
public class CustomerRepositoryIntegrationTest extends AbstractDomainTest {

	@Autowired
	CustomerRepository customerRepository;

	@Test
	public void savesCustomerCorrectly() {

		EmailAddress email = new EmailAddress("alicia4@keys.com");

		Customer alicia = customerRepository.findByEmailAddress(email);

		if (alicia == null) {
			alicia = new Customer("Alicia4", "Keys");
			alicia.setEmailAddress(email);
			alicia.add(new Address("27 Broadway", "New York", "United States"));

			Customer result = customerRepository.save(alicia);
			assertThat(result.getUuid(), is(notNullValue()));
		}

	}

	@Test
	public void readsCustomerByEmail() {

		EmailAddress email = new EmailAddress("alicia5@keys.com");
		Customer result = customerRepository.findByEmailAddress(email);

		if (result == null) {
			result = new Customer("Alicia5", "Keys");
			result.setEmailAddress(email);

			customerRepository.save(result);

			result = customerRepository.findByEmailAddress(email);
		}

		assertThat(result.getUuid(), is(notNullValue()));
	}

	@Test
	public void preventsDuplicateEmail() {

		Customer dave = customerRepository.findByEmailAddress(new EmailAddress("dave@dmband.com"));

		Customer anotherDave = new Customer("Dave5", "Matthews");
		anotherDave.setEmailAddress(dave.getEmailAddress());

		//TODO: test that exception is thrown
		//repository.save(anotherDave);
	}
}
