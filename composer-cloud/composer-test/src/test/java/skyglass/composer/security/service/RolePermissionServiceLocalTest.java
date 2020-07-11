package skyglass.composer.security.service;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.local.bean.TestingApi;
import skyglass.composer.local.helper.security.RolePermissionLocalTestHelper;
import skyglass.composer.local.helper.security.UserLocalTestHelper;
import skyglass.composer.local.test.AbstractBaseTest;
import skyglass.composer.security.domain.ContextType;
import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.ResourceType;
import skyglass.composer.security.domain.UserContextPermission;
import skyglass.composer.security.domain.UserContextRole;
import skyglass.composer.security.dto.RoleDTO;
import skyglass.composer.security.service.RolePermissionService;
import skyglass.composer.security.service.RoleService;
import skyglass.composer.security.service.UserService;

/**
 *
 * @author skyglass
 */
//@ActiveProfiles({ AbstractBaseTest.PROFILE_PSQL })
public class RolePermissionServiceLocalTest extends AbstractBaseTest {

	private static final String CONTEXTUUID = "CONTEXTUUID";

	private static final String USER_ID = "TEST_USER";

	@Autowired
	private UserService userService;

	@Autowired
	private RolePermissionService rolePermissionService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private TestingApi testingApi;

	private UserLocalTestHelper userTestHelper;

	private RolePermissionLocalTestHelper rolePermissionTestHelper;

	@Before
	public void init() throws IOException {
		userTestHelper = UserLocalTestHelper.create(userService);
		userTestHelper.createUser(USER_ID);
		rolePermissionTestHelper = RolePermissionLocalTestHelper.create(roleService, rolePermissionService, testingApi);
	}

	@Test
	public void readAccessTest() throws IOException {
		Assert.assertEquals(false, rolePermissionService.canRead(USER_ID, ResourceType.Mv, CONTEXTUUID));
		rolePermissionService.assignPermissionToUserContext(USER_ID, CONTEXTUUID, ContextType.Unit, ResourceType.Mv, OperationType.Read);
		UserContextPermission result = rolePermissionService.getUserContextPermission(USER_ID, CONTEXTUUID, ResourceType.Mv, OperationType.Read);
		Assert.assertNotNull(result);
		Assert.assertEquals(CONTEXTUUID, result.getContext_uuid());
		Assert.assertEquals(ContextType.Unit, result.getContextType());
		Assert.assertEquals(OperationType.Read, result.getOperation().getName());
		Assert.assertEquals(ResourceType.Mv, result.getResourceType());
		Assert.assertEquals(USER_ID, result.getUser().getName());
		Assert.assertEquals(true, rolePermissionService.canRead(USER_ID, ResourceType.Mv, CONTEXTUUID));

		rolePermissionService.removePermissionFromUserContext(USER_ID, CONTEXTUUID, ContextType.Unit, ResourceType.Mv, OperationType.Read);
		Assert.assertEquals(false, rolePermissionService.canRead(USER_ID, ResourceType.Mv, CONTEXTUUID));
	}

	@Test
	public void writeAccessTest() throws IOException {
		Assert.assertEquals(false, rolePermissionService.canWrite(USER_ID, ResourceType.Mv, CONTEXTUUID));
		rolePermissionService.assignPermissionToUserContext(USER_ID, CONTEXTUUID, ContextType.Unit, ResourceType.Mv, OperationType.Write);
		Assert.assertEquals(true, rolePermissionService.canWrite(USER_ID, ResourceType.Mv, CONTEXTUUID));
		rolePermissionService.removePermissionFromUserContext(USER_ID, CONTEXTUUID, ContextType.Unit, ResourceType.Mv, OperationType.Write);
		Assert.assertEquals(false, rolePermissionService.canWrite(USER_ID, ResourceType.Mv, CONTEXTUUID));
	}

	@Test
	public void roleReadAccessTest() throws IOException {
		RoleDTO role = rolePermissionTestHelper.createRole(RolePermissionLocalTestHelper.WORKPIECE_VIEWER_ROLE);
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.Mv, OperationType.Read);

		Assert.assertEquals(false, rolePermissionService.canRead(USER_ID, ResourceType.Mv, CONTEXTUUID));
		rolePermissionService.assignRoleToUserContextByName(USER_ID, CONTEXTUUID, ContextType.Unit, RolePermissionLocalTestHelper.WORKPIECE_VIEWER_ROLE);
		UserContextRole result = rolePermissionService.getUserContextRoleByName(USER_ID, CONTEXTUUID, RolePermissionLocalTestHelper.WORKPIECE_VIEWER_ROLE);
		Assert.assertNotNull(result);
		Assert.assertEquals(RolePermissionLocalTestHelper.WORKPIECE_VIEWER_ROLE, result.getRole().getName());
		Assert.assertEquals(CONTEXTUUID, result.getContext_uuid());
		Assert.assertEquals(ContextType.Unit, result.getContextType());
		Assert.assertEquals(USER_ID, result.getUser().getName());
		Assert.assertEquals(true, rolePermissionService.canRead(USER_ID, ResourceType.Mv, CONTEXTUUID));

		rolePermissionService.removeRoleFromUserContextByName(USER_ID, CONTEXTUUID, ContextType.Unit, RolePermissionLocalTestHelper.WORKPIECE_VIEWER_ROLE);
		Assert.assertEquals(false, rolePermissionService.canRead(USER_ID, ResourceType.Mv, CONTEXTUUID));
	}

	@Test
	public void roleWriteAccessTest() throws IOException {
		RoleDTO role = rolePermissionTestHelper.createRole(RolePermissionLocalTestHelper.WORKPIECE_EDITOR_ROLE);
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.Mv, OperationType.Write);

		Assert.assertEquals(false, rolePermissionService.canWrite(USER_ID, ResourceType.Mv, CONTEXTUUID));
		rolePermissionService.assignRoleToUserContextByName(USER_ID, CONTEXTUUID, ContextType.Unit, RolePermissionLocalTestHelper.WORKPIECE_EDITOR_ROLE);
		Assert.assertEquals(true, rolePermissionService.canWrite(USER_ID, ResourceType.Mv, CONTEXTUUID));

		rolePermissionService.removeRoleFromUserContextByName(USER_ID, CONTEXTUUID, ContextType.Unit, RolePermissionLocalTestHelper.WORKPIECE_EDITOR_ROLE);
		Assert.assertEquals(false, rolePermissionService.canWrite(USER_ID, ResourceType.Mv, CONTEXTUUID));
	}

}
