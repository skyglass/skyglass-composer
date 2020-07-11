
package skyglass.composer.customer;

import org.springframework.data.repository.Repository;

public interface CustomerRepository extends Repository<Customer, String> {

	/**
	 * Returns the {@link Customer} with the given identifier.
	 * 
	 * @param id the id to search for.
	 * @return
	 */
	Customer findByUuid(String uuid);

	/**
	 * Saves the given {@link Customer}.
	 * 
	 * @param customer the {@link Customer} to search for.
	 * @return
	 */
	Customer save(Customer customer);

	/**
	 * Returns the customer with the given {@link EmailAddress}.
	 * 
	 * @param emailAddress the {@link EmailAddress} to search for.
	 * @return
	 */
	Customer findByEmailAddress(EmailAddress emailAddress);
}
