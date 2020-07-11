package skyglass.composer.business.service;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.business.domain.BusinessOwner;
import skyglass.composer.business.service.BusinessOwnerRepository;
import skyglass.composer.exceptions.BusinessRuleValidationException;
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
import skyglass.composer.security.service.RolePermissionService;
import skyglass.composer.security.service.RoleService;
import skyglass.composer.security.service.UserService;
import skyglass.composer.service.TestDataConstants;

/**
 *
 * @author skyglass
 */
//@ActiveProfiles({ AbstractBaseTest.PROFILE_PSQL })
public class BusinessOwnerServiceLocalTest extends AbstractBaseTest {

	private static final String USER_ID = "TEST_USER";

	@Autowired
	private UserService userService;

	@Autowired
	private BusinessOwnerRepository businessOwnerRepository;

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
		rolePermissionService.assignOperationToUserResourceType(
				mockHelper.getDefaultUser().getUsername(), ResourceType.BusinessOwner, OperationType.Write);
		rolePermissionTestHelper = RolePermissionLocalTestHelper.create(roleService, rolePermissionService, testingApi);
	}

	@Test
	public void businessOwnerReadAccessDenied() {
		BusinessOwner testBusinessOwner = businessOwnerRepository.createBusinessOwner("bo-viewer-test");

		mockHelper.mockUser(USER_ID);

		try {
			testBusinessOwner = businessOwnerRepository.findByUuidSecure(testBusinessOwner.getUuid());
			Assert.fail("BusinessOwner read should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}
	}

	@Test
	public void businessOwnerReadAccessGranted() {
		BusinessOwner testBusinessOwner = businessOwnerRepository.createBusinessOwner("bo-viewer-test");
		RoleDTO role = rolePermissionTestHelper.createRole("bo-viewer");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.BusinessOwner, OperationType.Read);
		rolePermissionService.assignRoleToUserByName(USER_ID, testBusinessOwner.getUuid(), "bo-viewer");

		mockHelper.mockUser(USER_ID);

		testBusinessOwner = businessOwnerRepository.findByUuidSecure(testBusinessOwner.getUuid());
		Assert.assertEquals("bo-viewer-test", testBusinessOwner.getName());

		try {
			testBusinessOwner = businessOwnerRepository.changeBusinessOwnerName(testBusinessOwner.getUuid(), "bo-viewer-test2");
			Assert.fail("BusinessOwner update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}
	}

	@Test
	public void businessOwnerCreateAccessGranted() {
		mockHelper.mockUser(USER_ID);

		try {
			businessOwnerRepository.createBusinessOwner("bo-create-test");
			Assert.fail("BusinessOwner create should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		RoleDTO role = rolePermissionTestHelper.createRole("bo-creator");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.BusinessOwner, OperationType.Read);
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.BusinessOwner, OperationType.Create);
		rolePermissionService.assignRoleToUserByName(USER_ID, "bo-creator");

		mockHelper.mockUser(USER_ID);

		BusinessOwner testBusinessOwner = businessOwnerRepository.createBusinessOwner("bo-create-test");
		testBusinessOwner = businessOwnerRepository.findByUuidSecure(testBusinessOwner.getUuid());
		Assert.assertEquals("bo-create-test", testBusinessOwner.getName());

		try {
			testBusinessOwner = businessOwnerRepository.changeBusinessOwnerName(testBusinessOwner.getUuid(), "bo-create-test2");
			Assert.fail("BusinessOwner update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}
	}

	@Test
	public void businessOwnerUpdateAccessGranted() {
		mockHelper.mockUser(USER_ID);

		try {
			businessOwnerRepository.createBusinessOwner("bo-create-test");
			Assert.fail("BusinessOwner create should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		BusinessOwner testBusinessOwner = businessOwnerRepository.createBusinessOwner("bo-create-test");
		testBusinessOwner = businessOwnerRepository.findByUuidSecure(testBusinessOwner.getUuid());
		Assert.assertEquals("bo-create-test", testBusinessOwner.getName());

		RoleDTO role = rolePermissionTestHelper.createRole("bo-updater");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.BusinessOwner, OperationType.Write);
		rolePermissionService.assignRoleToUserByName(USER_ID, "bo-updater");

		mockHelper.mockUser(USER_ID);

		testBusinessOwner = businessOwnerRepository.findByUuidSecure(testBusinessOwner.getUuid());
		testBusinessOwner = businessOwnerRepository.changeBusinessOwnerName(testBusinessOwner.getUuid(), "bo-create-test2");
		testBusinessOwner = businessOwnerRepository.findByUuidSecure(testBusinessOwner.getUuid());
		Assert.assertEquals("bo-create-test2", testBusinessOwner.getName());

		testBusinessOwner = businessOwnerRepository.createBusinessOwner("bo-create-test3");
		testBusinessOwner = businessOwnerRepository.findByUuidSecure(testBusinessOwner.getUuid());
		Assert.assertEquals("bo-create-test3", testBusinessOwner.getName());
	}

	@Test
	public void businessOwnerDeleteAccessGranted() {

		BusinessOwner testBusinessOwner = businessOwnerRepository.createBusinessOwner("bo-delete-test");
		testBusinessOwner = businessOwnerRepository.findByUuidSecure(testBusinessOwner.getUuid());
		Assert.assertEquals("bo-delete-test", testBusinessOwner.getName());

		UserDTO testUser = userTestHelper.createUser("user-delete-test");
		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-delete-test", testUser.getUsername());

		rolePermissionService.assignOperationToUserOwnerResourceType(
				mockHelper.getDefaultUser().getUsername(), testBusinessOwner.getUuid(), ResourceType.User, OperationType.Write);

		testUser = userService.changeOwner(testUser.getUuid(), testBusinessOwner.getUuid());
		testUser = userService.getUserDTO(testUser.getUsername());
		Assert.assertEquals("user-delete-test", testUser.getUsername());
		Assert.assertEquals(testBusinessOwner.getUuid(), testUser.getOwnerUuid());
		Assert.assertEquals(testBusinessOwner.getName(), testUser.getOwnerName());

		mockHelper.mockUser(USER_ID);

		try {
			businessOwnerRepository.deleteBusinessOwner(testBusinessOwner.getUuid());
			Assert.fail("BusinessOwner delete should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		RoleDTO role = rolePermissionTestHelper.createRole("bo-deleter");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.BusinessOwner, OperationType.Read);
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.BusinessOwner, OperationType.Delete);
		rolePermissionService.assignRoleToUserByName(USER_ID, "bo-deleter");

		mockHelper.mockUser(USER_ID);

		try {
			businessOwnerRepository.changeBusinessOwnerName(testBusinessOwner.getUuid(), "test");
			Assert.fail("BusinessOwner update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		try {
			businessOwnerRepository.deleteBusinessOwner(testBusinessOwner.getUuid());
			Assert.fail("BusinessOwner delete should not be allowed");
		} catch (BusinessRuleValidationException e) {
			Assert.assertEquals(400, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		userService.changeOwner(testUser.getUuid(), TestDataConstants.BUSINESSPARTNER_2_UUID);

		mockHelper.mockUser(USER_ID);

		businessOwnerRepository.deleteBusinessOwner(testBusinessOwner.getUuid());

		testBusinessOwner = businessOwnerRepository.findByUuid(testBusinessOwner.getUuid());
		Assert.assertNull(testBusinessOwner);

	}

	@Test
	public void businessOwnerResourceReadOverridesOwnerWrite() {
		mockHelper.mockUser(USER_ID);

		try {
			businessOwnerRepository.createBusinessOwner("bo-create-test");
			Assert.fail("BusinessOwner create should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		BusinessOwner testBusinessOwner = businessOwnerRepository.createBusinessOwner("bo-create-test");
		testBusinessOwner = businessOwnerRepository.findByUuidSecure(testBusinessOwner.getUuid());
		Assert.assertEquals("bo-create-test", testBusinessOwner.getName());

		RoleDTO role = rolePermissionTestHelper.createRole("user-owner-changer");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.BusinessOwner, OperationType.Write);
		rolePermissionService.assignRoleToUserByName(USER_ID, testBusinessOwner.getUuid(), "user-owner-changer");

		rolePermissionService.assignRoleToUserByName(USER_ID, testBusinessOwner.getUuid(), "user-owner-changer");

		rolePermissionService.assignOperationToUserResourceType(USER_ID, testBusinessOwner.getUuid(),
				ResourceType.BusinessOwner, OperationType.Read);

		mockHelper.mockUser(USER_ID);

		try {
			businessOwnerRepository.changeBusinessOwnerName(testBusinessOwner.getUuid(), "test");
			Assert.fail("BusinessOwner update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		rolePermissionService.assignOperationToUserResourceType(USER_ID, testBusinessOwner.getUuid(),
				ResourceType.BusinessOwner, OperationType.Update);

		mockHelper.mockUser(USER_ID);

		testBusinessOwner = businessOwnerRepository.changeBusinessOwnerName(testBusinessOwner.getUuid(), "bo-create-test2");
		testBusinessOwner = businessOwnerRepository.findByUuidSecure(testBusinessOwner.getUuid());
		Assert.assertEquals("bo-create-test2", testBusinessOwner.getName());
	}

	@Test
	public void businessOwnerOwnerWriteOverridesGlobalRead() {
		mockHelper.mockUser(USER_ID);

		try {
			businessOwnerRepository.createBusinessOwner("bo-create-test");
			Assert.fail("BusinessOwner create should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		BusinessOwner testBusinessOwner = businessOwnerRepository.createBusinessOwner("bo-create-test");
		testBusinessOwner = businessOwnerRepository.findByUuidSecure(testBusinessOwner.getUuid());
		Assert.assertEquals("bo-create-test", testBusinessOwner.getName());

		RoleDTO role = rolePermissionTestHelper.createRole("user-owner-reader");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.BusinessOwner, OperationType.Read);
		rolePermissionService.assignRoleToUserByName(USER_ID, "user-owner-reader");

		mockHelper.mockUser(USER_ID);

		try {
			businessOwnerRepository.changeBusinessOwnerName(testBusinessOwner.getUuid(), "test");
			Assert.fail("BusinessOwner update should not be allowed");
		} catch (PermissionDeniedException e) {
			Assert.assertEquals(403, e.getRawStatusCode());
		}

		mockHelper.mockDefaultUser();

		role = rolePermissionTestHelper.createRole("user-owner-changer");
		rolePermissionTestHelper.addRolePermission(role.getUuid(), ResourceType.BusinessOwner, OperationType.Write);
		rolePermissionService.assignRoleToUserByName(USER_ID, testBusinessOwner.getUuid(), "user-owner-changer");

		mockHelper.mockUser(USER_ID);

		testBusinessOwner = businessOwnerRepository.changeBusinessOwnerName(testBusinessOwner.getUuid(), "bo-create-test2");
		testBusinessOwner = businessOwnerRepository.findByUuidSecure(testBusinessOwner.getUuid());
		Assert.assertEquals("bo-create-test2", testBusinessOwner.getName());
	}

}
