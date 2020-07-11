package skyglass.composer.customer;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static skyglass.composer.customer.CoreMatchers.named;

import java.math.BigDecimal;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import skyglass.composer.test.AbstractDomainTest;

/**
 * Integration tests for {@link ProductRepository}.
 * 
 * @author Oliver Gierke
 */
public class ProductRepositoryIntegrationTest extends AbstractDomainTest {

	@Autowired
	ProductRepository repository;

	@Autowired
	JpaProductRepository jpaRepository;

	@Test
	public void createProduct() {

		Product product = new Product("Camera bag2", new BigDecimal(49.99));
		product = repository.save(product);
	}

	@Test
	public void lookupProductsByDescription() {

		Pageable pageable = PageRequest.of(0, 1, Direction.DESC, "name");
		Page<Product> page = repository.findByDescriptionContaining("Apple", pageable);

		assertThat(page.getContent(), hasSize(1));
		assertThat(page, Matchers.<Product> hasItems(named("iPad")));
		assertThat(page.getTotalElements(), is(2L));
		assertThat(page.isFirst(), is(true));
		assertThat(page.isLast(), is(false));
		assertThat(page.hasNext(), is(true));
	}

	@Test
	public void findsProductsByAttributes() {

		List<Product> products = jpaRepository.findByAttributeAndValue("connector", "plug");

		assertThat(products, Matchers.<Product> hasItems(named("Dock")));
	}
}
