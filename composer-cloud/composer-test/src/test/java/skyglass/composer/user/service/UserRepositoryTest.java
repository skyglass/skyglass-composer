package skyglass.composer.user.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.local.test.AbstractBaseTest;
import skyglass.composer.security.domain.User;
import skyglass.composer.security.repository.UserRepository;

// @ActiveProfiles({ AbstractBaseTest.PROFILE_PSQL })
public class UserRepositoryTest extends AbstractBaseTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	public void findUserByName() throws Exception {
		User user = userRepository.findByName("USER1");
		assertTrue(user != null);
		assertEquals("USER1", user.getName());
		assertEquals("02655648-7238-48e5-a36e-45025559b219", user.getUuid());
	}

}
