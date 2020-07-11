/*
 * Copyright 2012 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package skyglass.composer.customer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.test.AbstractDomainTest;

/**
 * Integration test for the manual implementation ({@link JpaCustomerRepository}) of the {@link CustomerRepository}
 * interface.
 * 
 * @author Oliver Gierke
 */
public class JpaCustomerRepositoryIntegrationTest extends AbstractDomainTest {

	@Autowired
	JpaCustomerRepository jpaCustomerRepository;

	@Test
	public void insertsNewCustomerCorrectly() {

		Customer customer = new Customer("Alicia", "Keys");
		customer = jpaCustomerRepository.save(customer);

		assertThat(customer.getUuid(), is(notNullValue()));
	}

	@Test
	public void updatesCustomerCorrectly() {

		Customer dave = jpaCustomerRepository.findByEmailAddress(new EmailAddress("dave@dmband.com"));
		assertThat(dave, is(notNullValue()));

		dave.setLastname("Miller");
		dave = jpaCustomerRepository.save(dave);

		Customer reference = jpaCustomerRepository.findByEmailAddress(dave.getEmailAddress());
		assertThat(reference.getLastname(), is(dave.getLastname()));
	}
}
