package skyglass.composer.user.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.local.bean.MockHelper;
import skyglass.composer.local.test.AbstractBaseTest;
import skyglass.composer.security.domain.User;
import skyglass.composer.security.repository.PermissionBean;

// @ActiveProfiles({ AbstractBaseTest.PROFILE_PSQL })
public class PermissionBeanTest extends AbstractBaseTest {

	@Autowired
	private MockHelper mockHelper;

	@Autowired
	private PermissionBean permissionBean;

	@Test
	public void getCurrentUser() throws Exception {
		mockHelper.logout();
		User user = permissionBean.getUserFromCtx();
		Assert.assertTrue(user == null);
	}

}
