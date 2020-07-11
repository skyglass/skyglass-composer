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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.entity.EntityUtil;

/**
 * Plain JPA based implementation of {@link CustomerRepository}.
 * 
 * @author Oliver Gierke
 */
@Repository
@Transactional
class JpaCustomerRepository implements CustomerRepository {

	@PersistenceContext
	private EntityManager em;

	/*
	 * (non-Javadoc)
	 * @see com.oreilly.springdata.jpa.core.CustomerRepository#findOne(java.lang.Long)
	 */
	@Override
	public Customer findByUuid(String uuid) {
		return em.find(Customer.class, uuid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.oreilly.springdata.jpa.core.CustomerRepository#save(com.oreilly.springdata.jpa.core.Customer)
	 */
	@Override
	public Customer save(Customer customer) {
		if (customer.getUuid() == null) {
			em.persist(customer);
			return customer;
		} else {
			return em.merge(customer);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.oreilly.springdata.jpa.core.CustomerRepository#findByEmailAddress(com.oreilly.springdata.jpa.core.EmailAddress)
	 */
	@Override
	public Customer findByEmailAddress(EmailAddress emailAddress) {

		TypedQuery<Customer> query = em.createQuery("select c from Customer c where c.emailAddress = :email",
				Customer.class);
		query.setParameter("email", emailAddress);

		return EntityUtil.getSingleResultSafely(query);
	}
}
