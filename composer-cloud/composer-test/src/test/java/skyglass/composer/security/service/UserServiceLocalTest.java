package skyglass.composer.security.service;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import skyglass.composer.exceptions.PermissionDeniedException;
import skyglass.composer.local.bean.MockHelper;
import skyglass.composer.local.bean.TestingApi;
import skyglass.composer.local.helper.security.RolePermissionLocalTestHelper;
import skyglass.composer.local.helper.security.UserLocalTestHelper;
import skyglass.composer.local.test.AbstractBaseTest;
import skyglass.composer.security.domain.GlobalRole;
import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.ResourceType;
import skyglass.composer.security.dto.RoleDTO;
import skyglass.composer.security.dto.UserDTO;
import skyglass.composer.service.TestDataConstants;

/**
 *
 * @author skyglass
 */
@ActiveProfiles({ AbstractBaseTest.PROFILE_PSQL })
public class UserServiceLocalTest extends AbstractBaseTest {

	private static final String USER_ID = "TEST_USER";

	@Autowired
	private UserService userService;

	@Autowired
	private RolePermissionService rolePermissionService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private TestingApi testingApi;

	@Autowired
	private MockHelper mockHelper;

	private UserLocalTestHelper userTestHelper;

	private RolePermissionLocalTestHelper rolePermissionTestHelper;

	@Before
	public void init() throws IOException {
		userTestHelper = UserLocalTestHelper.create(userService);
		UserDTO testUser = userTestHelper.createUser(USER_ID);
		userTestHelper.setUserRoles(testUser, GlobalRole.Admin);
		rolePermissionService.assignOperationToUserResourceType(
				testUser.getUsername(), testUser.getUuid(), ResourceType.User, OperationType.Read);
		rolePermissionTestHelper = RolePermissionLocalTestHelper.create(roleService, rolePermissionService, testingApi);
	}

	@Test
	public void userReadAccessDenied() {
		UserDTO testUser = userTestHelper.createUser("user-viewer-test");

		mockHelper.mockUser(USER_ID);

		try {
			testUser = userService.getUserDTOSecure(testUser.getUsername());
			Assert.fail("User read should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}
	}

	@Test
	public void userReadAccessGranted() {
		UserDTO testUser = userTestHelper.createUser("user-viewer-test");
		RoleDTO role = rolePermissionTestHelper.createRole("user-viewer");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.Read);
		rolePermissionService.assignRoleToUserByName(USER_ID, testUser.getOwnerUuid(), "user-viewer");

		mockHelper.mockUser(USER_ID);

		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-viewer-test", testUser.getUsername());

		try {
			testUser = userService.updateUser(testUser);
			Assert.fail("User update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}
	}

	@Test
	public void userUpdateAccessGrantedWithWritePermission() {
		mockHelper.mockUser(USER_ID);

		UserDTO testUser = userService.getUserDTO(USER_ID);
		Assert.assertEquals(USER_ID, testUser.getUsername());

		try {
			testUser = userService.updateUser(testUser);
			Assert.fail("User update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		testUser = userTestHelper.createUser("user-update-test");
		RoleDTO role = rolePermissionTestHelper.createRole("user-updater");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.Write);
		rolePermissionService.assignRoleToUserByName(USER_ID, "user-updater");

		mockHelper.mockUser(USER_ID);

		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-update-test", testUser.getUsername());

		testUser.setUsername("user-updated");
		userService.updateUser(testUser);
		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-updated", testUser.getUsername());

		testUser = userTestHelper.createUser("user-create-test");
		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-create-test", testUser.getUsername());
	}

	@Test
	public void userUpdateAccessGrantedWithUpdatePermission() {
		mockHelper.mockUser(USER_ID);

		UserDTO testUser = userService.getUserDTO(USER_ID);
		Assert.assertEquals(USER_ID, testUser.getUsername());

		try {
			userService.updateUser(testUser);
			Assert.fail("User update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		testUser = userTestHelper.createUser("user-update-test");
		RoleDTO role = rolePermissionTestHelper.createRole("user-updater");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.Read);
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.Update);
		rolePermissionService.assignRoleToUserByName(USER_ID, testUser.getOwnerUuid(), "user-updater");

		mockHelper.mockUser(USER_ID);

		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-update-test", testUser.getUsername());

		testUser.setUsername("user-updated");
		userService.updateUser(testUser);
		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-updated", testUser.getUsername());

		try {
			testUser = userTestHelper.createUser("user-create-test");
			Assert.fail("User create should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}
	}

	@Test
	public void userCreateAccessGrantedWithCreatePermission() {
		mockHelper.mockUser(USER_ID);

		UserDTO testUser = userService.getUserDTO(USER_ID);
		Assert.assertEquals(USER_ID, testUser.getUsername());

		try {
			userTestHelper.createUser("user-create-test");
			Assert.fail("User create should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		RoleDTO role = rolePermissionTestHelper.createRole("user-creator");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.Read);
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.Create);
		rolePermissionService.assignRoleToUserByName(USER_ID, "user-creator");

		mockHelper.mockUser(USER_ID);

		testUser = userTestHelper.createUser("user-create-test");
		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-create-test", testUser.getUsername());

		try {
			testUser = userService.updateUser(testUser);
			Assert.fail("User update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}
	}

	@Test
	public void userCreateAccessGrantedWithWritePermission() {
		mockHelper.mockUser(USER_ID);

		UserDTO testUser = userService.getUserDTO(USER_ID);
		Assert.assertEquals(USER_ID, testUser.getUsername());

		try {
			userTestHelper.createUser("user-create-test");
			Assert.fail("User create should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		RoleDTO role = rolePermissionTestHelper.createRole("user-writer");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.Write);
		rolePermissionService.assignRoleToUserByName(USER_ID, testUser.getOwnerUuid(), "user-writer");

		mockHelper.mockUser(USER_ID);

		testUser = userTestHelper.createUser("user-write-test");
		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-write-test", testUser.getUsername());

		testUser.setUsername("user-updated");
		userService.updateUser(testUser);
		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-updated", testUser.getUsername());
	}

	@Test
	public void userDeleteAccessGrantedWithDeletePermission() {
		mockHelper.mockUser(USER_ID);

		UserDTO testUser = userService.getUserDTO(USER_ID);
		Assert.assertEquals(USER_ID, testUser.getUsername());

		try {
			userService.deleteUser(testUser.getUuid());
			Assert.fail("User delete should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		RoleDTO role = rolePermissionTestHelper.createRole("user-deleter");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.Read);
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.Delete);
		rolePermissionService.assignRoleToUserByName(USER_ID, "user-deleter");

		testUser = userTestHelper.createUser("user-delete-test");
		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-delete-test", testUser.getUsername());

		mockHelper.mockUser(USER_ID);

		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-delete-test", testUser.getUsername());

		try {
			testUser = userService.updateUser(testUser);
			Assert.fail("User update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		testUser = userService.deleteUser(testUser.getUuid());
		Assert.assertEquals("user-delete-test", testUser.getUsername());

		try {
			testUser = userService.getUserDTO(testUser.getUsername());
			if (testUser != null) {
				Assert.fail("User should be deleted");
			}
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}
	}

	@Test
	public void userDeleteAccessGrantedWithWritePermission() {
		mockHelper.mockUser(USER_ID);
		testingApi.executeString("REFRESH MATERIALIZED VIEW USERRESOURCEPERMISSIONVIEW");

		UserDTO testUser = userService.getUserDTO(USER_ID);
		Assert.assertEquals(USER_ID, testUser.getUsername());

		try {
			userService.deleteUser(testUser.getUuid());
			Assert.fail("User delete should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		RoleDTO role = rolePermissionTestHelper.createRole("user-deleter");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.Write);
		rolePermissionService.assignRoleToUserByName(USER_ID, testUser.getOwnerUuid(), "user-deleter");

		testUser = userTestHelper.createUser("user-delete-test");
		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-delete-test", testUser.getUsername());

		mockHelper.mockUser(USER_ID);

		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-delete-test", testUser.getUsername());

		testUser.setUsername("user-updated");
		userService.updateUser(testUser);
		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-updated", testUser.getUsername());

		testUser = userService.deleteUser(testUser.getUuid());
		Assert.assertEquals("user-updated", testUser.getUsername());

		testUser = userService.getUserDTO(testUser.getUsername());
		if (testUser != null) {
			Assert.fail("User should be deleted");
		}
	}

	@Test
	public void userChangeOwnerAccessGrantedWithChangeOwnerPermission() {
		mockHelper.mockUser(USER_ID);

		UserDTO testUser = userService.getUserDTO(USER_ID);
		Assert.assertEquals(USER_ID, testUser.getUsername());

		try {
			userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_2_UUID);
			Assert.fail("User change owner should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		RoleDTO role = rolePermissionTestHelper.createRole("user-owner-changer");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.Read);
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.ChangeOwner);
		rolePermissionService.assignRoleToUserByName(USER_ID, "user-owner-changer");

		testUser = userTestHelper.createUser("user-change-owner");
		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-change-owner", testUser.getUsername());

		mockHelper.mockUser(USER_ID);

		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-change-owner", testUser.getUsername());

		try {
			testUser = userService.updateUser(testUser);
			Assert.fail("User update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_NAME, testUser.getOwnerName());
		testUser = userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_2_UUID);
		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-change-owner", testUser.getUsername());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_NAME, testUser.getOwnerName());
	}

	@Test
	public void userChangeOwnerAccessGrantedWithWritePermission() {
		mockHelper.mockUser(USER_ID);

		UserDTO testUser = userService.getUserDTO(USER_ID);
		Assert.assertEquals(USER_ID, testUser.getUsername());

		try {
			userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_2_UUID);
			Assert.fail("User change owner should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		RoleDTO role = rolePermissionTestHelper.createRole("user-owner-changer");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.Write);
		rolePermissionService.assignRoleToUserByName(USER_ID, testUser.getOwnerUuid(), "user-owner-changer");

		testUser = userTestHelper.createUser("user-change-owner");
		Assert.assertEquals("user-change-owner", testUser.getUsername());

		mockHelper.mockUser(USER_ID);

		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-change-owner", testUser.getUsername());

		testUser = userService.updateUser(testUser);
		Assert.assertEquals("user-change-owner", testUser.getUsername());

		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_NAME, testUser.getOwnerName());
		testUser = userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_2_UUID);
		Assert.assertEquals("user-change-owner", testUser.getUsername());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_NAME, testUser.getOwnerName());
	}

	@Test
	public void userResourceReadOverridesOwnerWrite() {
		mockHelper.mockUser(USER_ID);

		UserDTO testUser = userService.getUserDTO(USER_ID);
		Assert.assertEquals(USER_ID, testUser.getUsername());

		try {
			userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_2_UUID);
			Assert.fail("User change owner should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		RoleDTO role = rolePermissionTestHelper.createRole("user-owner-changer");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.Write);
		rolePermissionService.assignRoleToUserByName(USER_ID, testUser.getOwnerUuid(), "user-owner-changer");

		testUser = userTestHelper.createUser("user-change-owner");
		Assert.assertEquals("user-change-owner", testUser.getUsername());
		rolePermissionService.assignOperationToUserResourceType(USER_ID, testUser.getUuid(), ResourceType.User, OperationType.Read);

		mockHelper.mockUser(USER_ID);

		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-change-owner", testUser.getUsername());

		try {
			testUser = userService.updateUser(testUser);
			Assert.fail("User update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		rolePermissionService.assignOperationToUserResourceType(USER_ID, testUser.getUuid(), ResourceType.User, OperationType.ChangeOwner);

		try {
			testUser = userService.updateUser(testUser);
			Assert.fail("User update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		Assert.assertEquals("user-change-owner", testUser.getUsername());

		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_NAME, testUser.getOwnerName());
		testUser = userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_2_UUID);
		Assert.assertEquals("user-change-owner", testUser.getUsername());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_NAME, testUser.getOwnerName());
	}

	@Test
	public void userResourceReadOverridesGlobalWrite() {
		mockHelper.mockUser(USER_ID);

		UserDTO testUser = userService.getUserDTO(USER_ID);
		Assert.assertEquals(USER_ID, testUser.getUsername());

		try {
			userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_2_UUID);
			Assert.fail("User change owner should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		RoleDTO role = rolePermissionTestHelper.createRole("user-owner-changer");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.Write);
		rolePermissionService.assignRoleToUserByName(USER_ID, "user-owner-changer");

		testUser = userTestHelper.createUser("user-change-owner");
		Assert.assertEquals("user-change-owner", testUser.getUsername());
		rolePermissionService.assignOperationToUserResourceType(USER_ID, testUser.getUuid(), ResourceType.User, OperationType.Read);

		mockHelper.mockUser(USER_ID);

		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-change-owner", testUser.getUsername());

		try {
			testUser = userService.updateUser(testUser);
			Assert.fail("User update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		rolePermissionService.assignOperationToUserResourceType(USER_ID, testUser.getUuid(), ResourceType.User, OperationType.ChangeOwner);

		try {
			testUser = userService.updateUser(testUser);
			Assert.fail("User update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		Assert.assertEquals("user-change-owner", testUser.getUsername());

		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_NAME, testUser.getOwnerName());
		testUser = userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_2_UUID);
		Assert.assertEquals("user-change-owner", testUser.getUsername());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_NAME, testUser.getOwnerName());
	}

	@Test
	public void userResourceWriteOverridesGlobalRead() {
		mockHelper.mockUser(USER_ID);

		UserDTO testUser = userService.getUserDTO(USER_ID);
		Assert.assertEquals(USER_ID, testUser.getUsername());

		try {
			userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_2_UUID);
			Assert.fail("User change owner should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		RoleDTO role = rolePermissionTestHelper.createRole("user-owner-changer");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.Read);
		rolePermissionService.assignRoleToUserByName(USER_ID, "user-owner-changer");

		testUser = userTestHelper.createUser("user-change-owner");
		Assert.assertEquals("user-change-owner", testUser.getUsername());
		rolePermissionService.assignOperationToUserResourceType(USER_ID, testUser.getUuid(), ResourceType.User, OperationType.Write);

		mockHelper.mockUser(USER_ID);

		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-change-owner", testUser.getUsername());

		testUser = userService.updateUser(testUser);

		rolePermissionService.removePermissionFromUserResource(USER_ID, testUser.getUuid(), ResourceType.User, OperationType.Write);
		rolePermissionService.assignOperationToUserResourceType(USER_ID, testUser.getUuid(), ResourceType.User, OperationType.Read);
		rolePermissionService.assignOperationToUserResourceType(USER_ID, testUser.getUuid(), ResourceType.User, OperationType.ChangeOwner);

		try {
			testUser = userService.updateUser(testUser);
			Assert.fail("User update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		Assert.assertEquals("user-change-owner", testUser.getUsername());

		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_NAME, testUser.getOwnerName());
		testUser = userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_2_UUID);
		Assert.assertEquals("user-change-owner", testUser.getUsername());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_NAME, testUser.getOwnerName());
	}

	@Test
	public void userResourceWriteOverridesOwnerRead() {
		mockHelper.mockUser(USER_ID);

		UserDTO testUser = userService.getUserDTO(USER_ID);
		Assert.assertEquals(USER_ID, testUser.getUsername());

		try {
			userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_2_UUID);
			Assert.fail("User change owner should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		RoleDTO role = rolePermissionTestHelper.createRole("user-owner-changer");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.Read);
		rolePermissionService.assignRoleToUserByName(USER_ID, testUser.getOwnerUuid(), "user-owner-changer");

		testUser = userTestHelper.createUser("user-change-owner");
		Assert.assertEquals("user-change-owner", testUser.getUsername());
		rolePermissionService.assignOperationToUserResourceType(USER_ID, testUser.getUuid(), ResourceType.User, OperationType.Write);

		mockHelper.mockUser(USER_ID);

		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-change-owner", testUser.getUsername());

		testUser = userService.updateUser(testUser);

		rolePermissionService.removePermissionFromUserResource(USER_ID, testUser.getUuid(), ResourceType.User, OperationType.Write);
		rolePermissionService.assignOperationToUserResourceType(USER_ID, testUser.getUuid(), ResourceType.User, OperationType.Read);
		rolePermissionService.assignOperationToUserResourceType(USER_ID, testUser.getUuid(), ResourceType.User, OperationType.ChangeOwner);

		try {
			testUser = userService.updateUser(testUser);
			Assert.fail("User update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		Assert.assertEquals("user-change-owner", testUser.getUsername());

		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_NAME, testUser.getOwnerName());
		testUser = userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_2_UUID);
		Assert.assertEquals("user-change-owner", testUser.getUsername());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_NAME, testUser.getOwnerName());
	}

	@Test
	public void userOwnerReadOverridesGlobalWrite() {
		mockHelper.mockUser(USER_ID);

		UserDTO testUser = userService.getUserDTO(USER_ID);
		Assert.assertEquals(USER_ID, testUser.getUsername());

		try {
			userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_2_UUID);
			Assert.fail("User change owner should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		RoleDTO role = rolePermissionTestHelper.createRole("user-owner-changer");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.Write);
		rolePermissionService.assignRoleToUserByName(USER_ID, "user-owner-changer");

		testUser = userTestHelper.createUser("user-change-owner");
		Assert.assertEquals("user-change-owner", testUser.getUsername());
		rolePermissionService.assignOperationToUserOwnerResourceType(USER_ID, testUser.getOwnerUuid(), ResourceType.User, OperationType.Read);

		mockHelper.mockUser(USER_ID);

		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-change-owner", testUser.getUsername());

		try {
			testUser = userService.updateUser(testUser);
			Assert.fail("User update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		rolePermissionService.assignOperationToUserResourceType(USER_ID, testUser.getUuid(), ResourceType.User, OperationType.Read);
		rolePermissionService.assignOperationToUserResourceType(USER_ID, testUser.getUuid(), ResourceType.User, OperationType.ChangeOwner);

		try {
			testUser = userService.updateUser(testUser);
			Assert.fail("User update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		Assert.assertEquals("user-change-owner", testUser.getUsername());

		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_NAME, testUser.getOwnerName());
		testUser = userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_2_UUID);
		Assert.assertEquals("user-change-owner", testUser.getUsername());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_NAME, testUser.getOwnerName());
	}

	@Test
	public void userGlobalReadOverridesWrongOwnerWrite() {
		mockHelper.mockUser(USER_ID);

		UserDTO testUser = userService.getUserDTO(USER_ID);
		Assert.assertEquals(USER_ID, testUser.getUsername());

		try {
			userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_2_UUID);
			Assert.fail("User change owner should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		RoleDTO role = rolePermissionTestHelper.createRole("user-owner-changer");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.Write);
		rolePermissionService.assignRoleToUserByName(USER_ID, TestDataConstants.BUSINESSPARTNER_2_UUID, "user-owner-changer");

		RoleDTO role2 = rolePermissionTestHelper.createRole("user-owner-reader");
		rolePermissionTestHelper.addRolePermission(role2.getUuid(), ResourceType.User, OperationType.Read);
		rolePermissionService.assignRoleToUserByName(USER_ID, "user-owner-reader");

		testUser = userTestHelper.createUser("user-change-owner");
		Assert.assertEquals("user-change-owner", testUser.getUsername());

		mockHelper.mockUser(USER_ID);

		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-change-owner", testUser.getUsername());

		try {
			testUser = userService.updateUser(testUser);
			Assert.fail("User update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		rolePermissionService.assignOperationToUserResourceType(USER_ID, ResourceType.User, OperationType.Read);
		rolePermissionService.assignOperationToUserResourceType(USER_ID, ResourceType.User, OperationType.ChangeOwner);

		try {
			testUser = userService.updateUser(testUser);
			Assert.fail("User update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		Assert.assertEquals("user-change-owner", testUser.getUsername());

		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_NAME, testUser.getOwnerName());
		testUser = userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_2_UUID);
		Assert.assertEquals("user-change-owner", testUser.getUsername());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_NAME, testUser.getOwnerName());
	}

	@Test
	public void userGlobalWriteOverridesWrongOwnerRead() {
		mockHelper.mockUser(USER_ID);

		UserDTO testUser = userService.getUserDTO(USER_ID);
		Assert.assertEquals(USER_ID, testUser.getUsername());

		try {
			userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_2_UUID);
			Assert.fail("User change owner should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		RoleDTO role = rolePermissionTestHelper.createRole("user-owner-reader");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.User, OperationType.Read);
		rolePermissionService.assignRoleToUserByName(USER_ID, TestDataConstants.BUSINESSPARTNER_2_UUID, "user-owner-reader");

		RoleDTO role2 = rolePermissionTestHelper.createRole("user-owner-changer");
		rolePermissionTestHelper.addRolePermission(role2.getUuid(), ResourceType.User, OperationType.Write);
		rolePermissionService.assignRoleToUserByName(USER_ID, "user-owner-changer");

		testUser = userTestHelper.createUser("user-change-owner");
		Assert.assertEquals("user-change-owner", testUser.getUsername());

		mockHelper.mockUser(USER_ID);

		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-change-owner", testUser.getUsername());

		Assert.assertEquals("user-change-owner", testUser.getUsername());

		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_NAME, testUser.getOwnerName());
		testUser = userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_2_UUID);
		Assert.assertEquals("user-change-owner", testUser.getUsername());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_NAME, testUser.getOwnerName());

		try {
			testUser = userService.updateUser(testUser);
			Assert.fail("User update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		rolePermissionService.removeRoleFromUserByName(USER_ID, "user-owner-changer");
		rolePermissionService.assignOperationToUserOwnerResourceType(USER_ID, TestDataConstants.BUSINESSPARTNER_2_UUID, ResourceType.User, OperationType.Read);
		rolePermissionService.assignOperationToUserOwnerResourceType(USER_ID, TestDataConstants.BUSINESSPARTNER_2_UUID, ResourceType.User, OperationType.ChangeOwner);

		try {
			testUser = userService.updateUser(testUser);
			Assert.fail("User update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		Assert.assertEquals("user-change-owner", testUser.getUsername());

		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_2_NAME, testUser.getOwnerName());
		testUser = userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_1_UUID);
		Assert.assertEquals("user-change-owner", testUser.getUsername());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_UUID, testUser.getOwnerUuid());
		Assert.assertEquals(TestDataConstants.BUSINESSPARTNER_1_NAME, testUser.getOwnerName());
	}

}
