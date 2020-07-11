package skyglass.composer.user.service;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.local.bean.MockHelper;
import skyglass.composer.local.helper.security.UserLocalTestHelper;
import skyglass.composer.local.test.AbstractBaseTest;
import skyglass.composer.security.api.UserApi;
import skyglass.composer.security.dto.UserDTO;

// @ActiveProfiles({ AbstractBaseTest.PROFILE_PSQL })
public class CreateUserTest extends AbstractBaseTest {

	@Autowired
	private MockHelper mockHelper;

	@Autowired
	private UserApi userService;

	private UserLocalTestHelper userTestHelper;

	@Before
	public void init() {
		userTestHelper = UserLocalTestHelper.create(userService);
	}

	@Test
	public void testCreateUser() throws IOException {
		UserDTO result = userTestHelper.createUser("P42");
		result = userTestHelper.getUser(result.getUsername());
		Assert.assertEquals("P42", result.getUsername());
	}

}
