package skyglass.composer.security.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.exceptions.BusinessRuleValidationException;
import skyglass.composer.local.bean.TestingApi;
import skyglass.composer.local.helper.security.RolePermissionLocalTestHelper;
import skyglass.composer.local.helper.security.UserLocalTestHelper;
import skyglass.composer.local.test.AbstractBaseTest;
import skyglass.composer.security.domain.GlobalRole;
import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.ResourceType;
import skyglass.composer.security.dto.RoleDTO;
import skyglass.composer.security.dto.RolePermissionDTO;
import skyglass.composer.security.dto.UserDTO;
import skyglass.composer.security.service.RolePermissionService;
import skyglass.composer.security.service.RoleService;
import skyglass.composer.security.service.SecurityCheckService;
import skyglass.composer.security.service.UserService;

/**
 *
 * @author skyglass
 */
//@ActiveProfiles({ AbstractBaseTest.PROFILE_PSQL })
public class RoleServiceLocalTest extends AbstractBaseTest {

	private static final String USER_ID = "TEST_USER";

	private static final String USER_ID2 = "TEST_USER2";

	@Autowired
	private UserService userService;

	@Autowired
	private RolePermissionService rolePermissionService;

	@Autowired
	private TestingApi testingApi;

	@Autowired
	private SecurityCheckService securityCheckService;

	@Autowired
	private RoleService roleService;

	private UserLocalTestHelper userTestHelper;

	private RolePermissionLocalTestHelper rolePermissionTestHelper;

	private UserDTO testUser1;

	private UserDTO testUser2;

	RoleDTO pf1ParentBp1Role;

	RoleDTO pf1ParentBp2Role;

	RoleDTO pf1AccessRole;

	RoleDTO pf1DeniedRole;

	RoleDTO pf1ChildRole;

	RoleDTO pf1GrandChildRole;

	RoleDTO pf1ChildDeniedRole;

	RoleDTO pf1ChildBp1Role;

	RoleDTO pf1ChildBp2Role;

	RoleDTO pf1GrandChildBp2Role;

	@Before
	public void init() throws IOException {
		userTestHelper = UserLocalTestHelper.create(userService);

		testUser1 = userTestHelper.createUser(USER_ID);
		testUser2 = userTestHelper.createUser(USER_ID2);
		userTestHelper.setUserRoles(testUser1, GlobalRole.Admin);
		userTestHelper.setUserRoles(testUser2, GlobalRole.Admin);
		rolePermissionTestHelper = RolePermissionLocalTestHelper.create(roleService, rolePermissionService, testingApi);

		RoleDTO testRole1 = rolePermissionTestHelper.createRoleDto("test1", null, null);
		List<RoleDTO> parentRoles = new ArrayList<>();
		parentRoles.add(testRole1);
		parentRoles = roleService.createOrUpdateRolesAndInvertParents(parentRoles);
		parentRoles = roleService.findAndInvertParents().stream().filter(r -> !r.isGlobal()).collect(Collectors.toList());
		Assert.assertEquals(1, parentRoles.size());
		for (RoleDTO roleDto : parentRoles) {
			if (roleDto.getName().equals("test1")) {
				testRole1 = roleDto;
			}
		}

		RoleDTO testRole2 = rolePermissionTestHelper.createRoleDto("test2", testRole1.getUuid(), null);
		parentRoles.add(testRole2);
		parentRoles = roleService.createOrUpdateRolesAndInvertParents(parentRoles);
		parentRoles = roleService.findAndInvertParents().stream().filter(r -> !r.isGlobal()).collect(Collectors.toList());
		Assert.assertEquals(2, parentRoles.size());
		for (RoleDTO roleDto : parentRoles) {
			if (roleDto.getName().equals("test2")) {
				testRole2 = roleDto;
			}
		}

		RoleDTO testRole3 = rolePermissionTestHelper.createRoleDto("test3", testRole2.getUuid(), null);
		parentRoles.add(testRole3);
		parentRoles = roleService.createOrUpdateRolesAndInvertParents(parentRoles);
		parentRoles = roleService.findAndInvertParents().stream().filter(r -> !r.isGlobal()).collect(Collectors.toList());
		Assert.assertEquals(3, parentRoles.size());
		for (RoleDTO roleDto : parentRoles) {
			if (roleDto.getName().equals("test3")) {
				testRole3 = roleDto;
			}
		}

		List<RoleDTO> updatedRoles = new ArrayList<>();
		for (RoleDTO parentRole : parentRoles) {
			if (parentRole.getUuid().equals(testRole1.getUuid())) {
				testRole1 = parentRole;
			}
			if (parentRole.getUuid().equals(testRole2.getUuid())) {
				testRole2 = parentRole;
			}
			if (parentRole.getUuid().equals(testRole3.getUuid())) {
				testRole3 = parentRole;
			}
			updatedRoles.add(parentRole);
		}

		Assert.assertEquals(null, testRole1.getParentUuid());
		Assert.assertEquals(testRole1.getUuid(), testRole2.getParentUuid());
		Assert.assertEquals(testRole2.getUuid(), testRole3.getParentUuid());
		Assert.assertEquals(0, testRole1.getPermissions().stream().filter(p -> p.getOperationType() != OperationType.None).collect(Collectors.toList()).size());
		Assert.assertEquals(0, testRole2.getPermissions().stream().filter(p -> p.getOperationType() != OperationType.None).collect(Collectors.toList()).size());
		Assert.assertEquals(0, testRole3.getPermissions().stream().filter(p -> p.getOperationType() != OperationType.None).collect(Collectors.toList()).size());

		testRole1.setParentUuid(testRole3.getUuid());

		try {
			parentRoles = roleService.createOrUpdateRoles(parentRoles);
			Assert.fail("Setting cyclic parent-child relationship should fail");
		} catch (BusinessRuleValidationException e) {
			//ignore
		}
		try {
			parentRoles = roleService.createOrUpdateRolesAndInvertParents(parentRoles);
			Assert.fail("Setting cyclic parent-child relationship should fail");
		} catch (BusinessRuleValidationException e) {
			//ignore
		}
		testRole1.setParentUuid(null);
		List<RolePermissionDTO> permissions1 = new ArrayList<>();
		RolePermissionDTO permission1 = new RolePermissionDTO();
		permission1.setOperationType(OperationType.Read);
		permission1.setResourceType(ResourceType.BusinessOwner);
		permissions1.add(permission1);
		testRole1.setPermissions(permissions1);

		testRole2.setParentUuid(testRole1.getUuid());

		List<RolePermissionDTO> permissions2 = new ArrayList<>();
		RolePermissionDTO permission11 = new RolePermissionDTO();
		permission11.setOperationType(OperationType.Write);
		permission11.setResourceType(ResourceType.BusinessOwner);
		permissions2.add(permission11);

		RolePermissionDTO permission12 = new RolePermissionDTO();
		permission12.setOperationType(OperationType.Read);
		permission12.setResourceType(ResourceType.BusinessUnit);
		permissions2.add(permission12);

		testRole2.setPermissions(permissions2);

		testRole3.setParentUuid(testRole2.getUuid());
		RoleDTO testRole4 = rolePermissionTestHelper.createRoleDto("test4", testRole3.getUuid(), null);
		RoleDTO testRole5 = rolePermissionTestHelper.createRoleDto("test5", null, null);
		testRole4.setPermissions(permissions1);
		parentRoles.add(testRole4);
		parentRoles.add(testRole5);
		parentRoles = roleService.createOrUpdateRolesAndInvertParents(parentRoles);
		testingApi.resetRoleHierarchy(testRole4, testRole3, testRole2, testRole1);

		parentRoles = roleService.findAll().stream().filter(r -> !r.isGlobal()).collect(Collectors.toList());
		Assert.assertEquals(5, parentRoles.size());
		for (RoleDTO parentRole : parentRoles) {
			if (parentRole.getUuid().equals(testRole1.getUuid())) {
				testRole1 = parentRole;
			}
			if (parentRole.getUuid().equals(testRole2.getUuid())) {
				testRole2 = parentRole;
			}
			if (parentRole.getUuid().equals(testRole3.getUuid())) {
				testRole3 = parentRole;
			}
			if (parentRole.getName().equals(testRole4.getName())) {
				testRole4 = parentRole;
			}
			if (parentRole.getName().equals(testRole5.getName())) {
				testRole5 = parentRole;
			}
		}
		Assert.assertEquals(testRole1.getParentUuid(), testRole2.getUuid());
		Assert.assertEquals(testRole2.getParentUuid(), testRole3.getUuid());
		Assert.assertEquals(testRole3.getParentUuid(), testRole4.getUuid());
		Assert.assertEquals(testRole4.getParentUuid(), null);
		Assert.assertEquals(testRole5.getParentUuid(), null);

		List<RolePermissionDTO> filteredRole1 = testRole1.getPermissions().stream().filter(p -> p.getOperationType() != OperationType.None).collect(Collectors.toList());
		Assert.assertEquals(1, filteredRole1.size());
		Assert.assertEquals(OperationType.Read, filteredRole1.get(0).getOperationType());
		Assert.assertEquals(ResourceType.BusinessOwner, filteredRole1.get(0).getResourceType());

		List<RolePermissionDTO> filteredRole2 = testRole2.getPermissions().stream().filter(p -> p.getOperationType() != OperationType.None).collect(Collectors.toList());
		Assert.assertEquals(2, filteredRole2.size());
		for (RolePermissionDTO rolePermission : filteredRole2) {
			if (rolePermission.getResourceType() == ResourceType.BusinessOwner) {
				Assert.assertEquals(OperationType.Write, rolePermission.getOperationType());
			}
			if (rolePermission.getResourceType() == ResourceType.BusinessUnit) {
				Assert.assertEquals(OperationType.Read, rolePermission.getOperationType());
			}
		}

		List<RolePermissionDTO> filteredRole3 = testRole3.getPermissions().stream().filter(p -> p.getOperationType() != OperationType.None).collect(Collectors.toList());
		Assert.assertEquals(2, filteredRole3.size());
		for (RolePermissionDTO rolePermission : filteredRole3) {
			if (rolePermission.getResourceType() == ResourceType.BusinessOwner) {
				Assert.assertEquals(OperationType.Write, rolePermission.getOperationType());
			}
			if (rolePermission.getResourceType() == ResourceType.BusinessUnit) {
				Assert.assertEquals(OperationType.Read, rolePermission.getOperationType());
			}
		}

		List<RolePermissionDTO> filteredRole4 = testRole4.getPermissions().stream().filter(p -> p.getOperationType() != OperationType.None).collect(Collectors.toList());
		Assert.assertEquals(2, filteredRole4.size());
		for (RolePermissionDTO rolePermission : filteredRole4) {
			if (rolePermission.getResourceType() == ResourceType.BusinessOwner) {
				Assert.assertEquals(OperationType.Write, rolePermission.getOperationType());
			}
			if (rolePermission.getResourceType() == ResourceType.BusinessUnit) {
				Assert.assertEquals(OperationType.Read, rolePermission.getOperationType());
			}
		}

		parentRoles = roleService.findAndInvertParents().stream().filter(r -> !r.isGlobal()).collect(Collectors.toList());
		Assert.assertEquals(5, parentRoles.size());
		for (RoleDTO parentRole : parentRoles) {
			if (parentRole.getUuid().equals(testRole1.getUuid())) {
				testRole1 = parentRole;
			}
			if (parentRole.getUuid().equals(testRole2.getUuid())) {
				testRole2 = parentRole;
			}
			if (parentRole.getUuid().equals(testRole3.getUuid())) {
				testRole3 = parentRole;
			}
			if (parentRole.getName().equals(testRole4.getName())) {
				testRole4 = parentRole;
			}
			if (parentRole.getName().equals(testRole5.getName())) {
				testRole5 = parentRole;
			}
		}
		Assert.assertEquals(testRole1.getParentUuid(), null);
		Assert.assertEquals(testRole2.getParentUuid(), testRole1.getUuid());
		Assert.assertEquals(testRole3.getParentUuid(), testRole2.getUuid());
		Assert.assertEquals(testRole4.getParentUuid(), testRole3.getUuid());
		Assert.assertEquals(testRole5.getParentUuid(), null);

		roleService.deleteRole(testRole1.getUuid());
		roleService.deleteRole(testRole2.getUuid());
		roleService.deleteRole(testRole3.getUuid());
		roleService.deleteRole(testRole4.getUuid());
		roleService.deleteRole(testRole5.getUuid());

		parentRoles = roleService.findAll().stream().filter(r -> !r.isGlobal()).collect(Collectors.toList());
		Assert.assertEquals(0, parentRoles.size());

		pf1ParentBp1Role = rolePermissionTestHelper.createRole("PF1-Parent-BP1", null, null);

		pf1ParentBp2Role = rolePermissionTestHelper.createRole("PF1-Parent-BP2", null, null);
		List<RolePermissionDTO> permissions = new ArrayList<>();
		permissions = new ArrayList<>();
		permission1 = new RolePermissionDTO();
		permission1.setOperationType(OperationType.Write);
		permission1.setResourceType(ResourceType.BusinessOwner);
		permissions.add(permission1);
		pf1ParentBp2Role.setPermissions(permissions);
		pf1ParentBp2Role = roleService.createOrUpdateRoles(Collections.singletonList(pf1ParentBp2Role)).get(0);

		pf1AccessRole = rolePermissionTestHelper.createRole("PF1-Access", null, null);
		pf1DeniedRole = rolePermissionTestHelper.createRole("PF1-Denied", null, null);
		pf1ChildRole = rolePermissionTestHelper.createRole("PF1-Child-Access", pf1AccessRole.getUuid(), null);
		pf1GrandChildRole = rolePermissionTestHelper.createRole("PF1-Grand-Child-Access", pf1ChildRole.getUuid(), pf1AccessRole.getUuid(), null);
		pf1ChildDeniedRole = rolePermissionTestHelper.createRole("PF1-Child-Denied", pf1DeniedRole.getUuid(), null);

		pf1ChildBp1Role = rolePermissionTestHelper.createRole("PF1-Child-BP1", pf1ParentBp1Role.getUuid(), null);

		pf1ChildBp2Role = new RoleDTO();
		pf1ChildBp2Role.setName("PF1-Child-BP2");
		pf1ChildBp2Role.setGlobal(false);
		pf1ChildBp2Role.setParentUuid(pf1ParentBp2Role.getUuid());
		permissions = new ArrayList<>();
		permission1 = new RolePermissionDTO();
		permission1.setOperationType(OperationType.Read);
		permission1.setResourceType(ResourceType.BusinessOwner);
		permissions.add(permission1);
		pf1ChildBp2Role.setPermissions(permissions);
		pf1ChildBp2Role = roleService.createOrUpdateRoles(Collections.singletonList(pf1ChildBp2Role)).get(0);
		testingApi.addChildRoleHierarchyRecord(pf1ChildBp2Role.getUuid(), pf1ChildBp2Role.getParentUuid(), pf1ChildBp2Role.getName());
		List<RolePermissionDTO> filtered = pf1ChildBp2Role.getPermissions().stream().filter(p -> p.getOperationType() != OperationType.None).collect(Collectors.toList());
		Assert.assertEquals(1, filtered.size());
		Assert.assertEquals(OperationType.Read, filtered.get(0).getOperationType());
		Assert.assertEquals(ResourceType.BusinessOwner, filtered.get(0).getResourceType());
		Assert.assertEquals(pf1ParentBp2Role.getUuid(), pf1ChildBp2Role.getParentUuid());

		List<RoleDTO> roles = roleService.findAll();
		boolean found = false;
		boolean parentFound = false;
		for (RoleDTO role : roles) {
			if (pf1ChildBp2Role.getUuid().equals(role.getUuid())) {
				Assert.assertEquals(pf1ParentBp2Role.getUuid(), role.getParentUuid());
				filtered = role.getPermissions().stream().filter(p -> p.getOperationType() != OperationType.None).collect(Collectors.toList());
				Assert.assertEquals(1, filtered.size());
				Assert.assertEquals(OperationType.Read, filtered.get(0).getOperationType());
				Assert.assertEquals(ResourceType.BusinessOwner, filtered.get(0).getResourceType());
				found = true;
			}
			if (pf1ParentBp2Role.getUuid().equals(role.getUuid())) {
				parentFound = true;
				filtered = role.getPermissions().stream().filter(p -> p.getOperationType() != OperationType.None).collect(Collectors.toList());
				Assert.assertEquals(1, filtered.size());
				Assert.assertEquals(OperationType.Write, filtered.get(0).getOperationType());
				Assert.assertEquals(ResourceType.BusinessOwner, filtered.get(0).getResourceType());
			}
		}
		if (!found) {
			Assert.fail("Role was not found");
		}
		if (!parentFound) {
			Assert.fail("Parent Role was not found");
		}

		pf1ParentBp2Role.setParentUuid(pf1ChildBp2Role.getUuid());
		try {
			pf1ParentBp2Role = roleService.createOrUpdateRoles(Arrays.asList(pf1ChildBp2Role, pf1ChildBp2Role)).get(0);
			Assert.fail("Setting cyclic parent-child relationship should fail");
		} catch (BusinessRuleValidationException e) {
			//ignore
		}

		parentRoles = roleService.findAll();
		Assert.assertEquals(9, parentRoles.size());
		parentRoles = roleService.createOrUpdateRoles(parentRoles);
		parentRoles = roleService.findAll();
		Assert.assertEquals(9, parentRoles.size());

		pf1ChildBp2Role = roleService.findByUuid(pf1ChildBp2Role.getUuid());
		filtered = pf1ChildBp2Role.getPermissions().stream().filter(p -> p.getOperationType() != OperationType.None).collect(Collectors.toList());
		Assert.assertEquals(1, filtered.size());
		Assert.assertEquals(OperationType.Read, filtered.get(0).getOperationType());
		Assert.assertEquals(ResourceType.BusinessOwner, filtered.get(0).getResourceType());

		pf1ChildBp2Role = roleService.createOrUpdateRoles(Collections.singletonList(pf1ChildBp2Role)).get(0);
		permissions = new ArrayList<>();
		permission1 = new RolePermissionDTO();
		permission1.setOperationType(OperationType.Write);
		permission1.setResourceType(ResourceType.BusinessUnit);
		permissions.add(permission1);
		RolePermissionDTO permission2 = new RolePermissionDTO();
		permission2.setOperationType(OperationType.Read);
		permission2.setResourceType(ResourceType.BusinessUnit);
		permissions.add(permission2);
		pf1ChildBp2Role.setPermissions(permissions);
		pf1ChildBp2Role = roleService.createOrUpdateRoles(Collections.singletonList(pf1ChildBp2Role)).get(0);
		pf1ChildBp2Role = roleService.findByUuid(pf1ChildBp2Role.getUuid());

		filtered = pf1ChildBp2Role.getPermissions().stream().filter(p -> p.getOperationType() != OperationType.None).collect(Collectors.toList());
		Assert.assertEquals(1, filtered.size());
		Assert.assertEquals(OperationType.Write, filtered.get(0).getOperationType());
		Assert.assertEquals(ResourceType.BusinessUnit, filtered.get(0).getResourceType());
		pf1ChildBp2Role.setPermissions(new ArrayList<>());

		pf1ChildBp2Role = roleService.createOrUpdateRoles(Collections.singletonList(pf1ChildBp2Role)).get(0);
		filtered = pf1ChildBp2Role.getPermissions().stream().filter(p -> p.getOperationType() != OperationType.None).collect(Collectors.toList());
		Assert.assertEquals(0, filtered.size());

		pf1ChildBp2Role = roleService.findByUuid(pf1ChildBp2Role.getUuid());
		filtered = pf1ChildBp2Role.getPermissions().stream().filter(p -> p.getOperationType() != OperationType.None).collect(Collectors.toList());
		Assert.assertEquals(0, filtered.size());

		pf1GrandChildBp2Role = rolePermissionTestHelper.createRole("PF1-Grand-Child-BP2", pf1ChildBp2Role.getUuid(), pf1ParentBp2Role.getUuid(), null);
		pf1GrandChildBp2Role = roleService.createOrUpdateRoles(Collections.singletonList(pf1GrandChildBp2Role)).get(0);
	}

	@Test
	//Scenario 8: Role with other business partner should not be visible to BP1
	//When: Roles for business partner BP1 are loaded
	//Then: “PF1-Child-BP2” should not be in the list
	//And: “PF1-Access” should be in the list
	//And: “PF1-Denied” should be in the list
	public void pf1_scenario8_roles() {
		List<RoleDTO> roleDTOs = roleService.findTree();
		Assert.assertEquals(true, roleDTOs.contains(pf1AccessRole));
		Assert.assertEquals(true, roleDTOs.contains(pf1DeniedRole));
		Assert.assertEquals(true, roleDTOs.contains(pf1ParentBp1Role));
		for (RoleDTO roleDTO : roleDTOs) {
			if (roleDTO.equals(pf1AccessRole)) {
				Assert.assertEquals(1, roleDTO.getChildren().size());
				Assert.assertEquals(true, roleDTO.getChildren().contains(pf1ChildRole));
				Assert.assertEquals(false, roleDTO.getChildren().contains(pf1ChildBp2Role));
			}
			if (roleDTO.equals(pf1DeniedRole)) {
				Assert.assertEquals(1, roleDTO.getChildren().size());
				Assert.assertEquals(true, roleDTO.getChildren().contains(pf1ChildDeniedRole));
			}
			if (roleDTO.equals(pf1ParentBp1Role)) {
				Assert.assertEquals(1, roleDTO.getChildren().size());
				Assert.assertEquals(true, roleDTO.getChildren().contains(pf1ChildBp1Role));
			}
		}

		roleDTOs = roleService.findTree();
		Assert.assertEquals(4, roleDTOs.size());
		Assert.assertEquals(true, roleDTOs.contains(pf1AccessRole));
		Assert.assertEquals(true, roleDTOs.contains(pf1ParentBp2Role));
		Assert.assertEquals(true, roleDTOs.contains(pf1DeniedRole));
		for (RoleDTO roleDTO : roleDTOs) {
			if (roleDTO.equals(pf1AccessRole)) {
				Assert.assertEquals(1, roleDTO.getChildren().size());
				Assert.assertEquals(true, roleDTO.getChildren().contains(pf1ChildRole));
				for (RoleDTO child : roleDTO.getChildren()) {
					if (child.equals(pf1ChildRole)) {
						Assert.assertEquals(1, child.getChildren().size());
						Assert.assertEquals(true, child.getChildren().contains(pf1GrandChildRole));
					}
				}
			}
			if (roleDTO.equals(pf1ParentBp2Role)) {
				Assert.assertEquals(1, roleDTO.getChildren().size());
				Assert.assertEquals(true, roleDTO.getChildren().contains(pf1ChildBp2Role));
				for (RoleDTO child : roleDTO.getChildren()) {
					if (child.equals(pf1ChildBp2Role)) {
						Assert.assertEquals(1, child.getChildren().size());
						Assert.assertEquals(true, child.getChildren().contains(pf1GrandChildBp2Role));
					}
				}
			}
			if (roleDTO.equals(pf1DeniedRole)) {
				Assert.assertEquals(1, roleDTO.getChildren().size());
				Assert.assertEquals(true, roleDTO.getChildren().contains(pf1ChildDeniedRole));
			}
		}
	}

	@Test
	//Scenario 13: PF1 access with insufficient role and with sufficient license should be denied
	//When: U1 is assigned “PF1-Denied” Role with any context
	//When: U1 tries to access Paid Feature PF1
	//Then: Access should be denied
	public void permissions_scenario13() {

		RoleDTO testRole = rolePermissionTestHelper.createRole("test-role", null, null);
		List<RolePermissionDTO> permissions = new ArrayList<>();
		permissions = new ArrayList<>();
		RolePermissionDTO permission1 = new RolePermissionDTO();
		permission1.setOperationType(OperationType.Write);
		permission1.setResourceType(ResourceType.BusinessOwner);
		permissions.add(permission1);
		testRole.setPermissions(permissions);
		testRole = roleService.createOrUpdateRoles(Collections.singletonList(testRole)).get(0);

		rolePermissionService.assignRoleToUserByName(testUser1.getUsername(), "test-role");
		Map<ResourceType, OperationType> permissionMap = securityCheckService.loadUserPermissions(testUser1.getUsername());
		if (permissionMap.get(ResourceType.BusinessOwner) != OperationType.Write) {
			Assert.fail("Access To Business Owner Should be WRITE");
		}

		List<ResourceType> filtered = permissionMap.entrySet().stream().filter(e -> e.getValue() != OperationType.None).map(e -> e.getKey()).collect(Collectors.toList());
		Assert.assertEquals(1, filtered.size());
		for (ResourceType permission : filtered) {
			if (permission == ResourceType.BusinessOwner) {
				Assert.assertEquals(OperationType.Write, permissionMap.get(ResourceType.BusinessOwner));
			} else {
				Assert.fail("Unknown Permission");
			}
		}

		RolePermissionDTO permission2 = new RolePermissionDTO();
		permission2.setOperationType(OperationType.Read);
		permission2.setResourceType(ResourceType.Role);
		permissions.add(permission2);
		testRole.setPermissions(permissions);
		testRole = roleService.createOrUpdateRoles(Collections.singletonList(testRole)).get(0);

		permissionMap = securityCheckService.loadUserPermissions(testUser1.getUsername());

		filtered = permissionMap.entrySet().stream().filter(e -> e.getValue() != OperationType.None).map(e -> e.getKey()).collect(Collectors.toList());
		Assert.assertEquals(2, filtered.size());
		for (ResourceType permission : filtered) {
			if (permission == ResourceType.BusinessOwner) {
				Assert.assertEquals(OperationType.Write, permissionMap.get(ResourceType.BusinessOwner));
			} else if (permission == ResourceType.Role) {
				Assert.assertEquals(OperationType.Read, permissionMap.get(ResourceType.Role));
			} else {
				Assert.fail("Unknown Permission");
			}
		}

		permissions = new ArrayList<>();

		permission1 = new RolePermissionDTO();
		permission1.setOperationType(OperationType.Read);
		permission1.setResourceType(ResourceType.Role);
		permissions.add(permission1);

		permission2 = new RolePermissionDTO();
		permission2.setOperationType(OperationType.Write);
		permission2.setResourceType(ResourceType.Role);
		permissions.add(permission2);

		testRole.setPermissions(permissions);

		testRole = roleService.createOrUpdateRoles(Collections.singletonList(testRole)).get(0);

		permissionMap = securityCheckService.loadUserPermissions(testUser1.getUsername());

		filtered = permissionMap.entrySet().stream().filter(e -> e.getValue() != OperationType.None).map(e -> e.getKey()).collect(Collectors.toList());
		Assert.assertEquals(1, filtered.size());
		for (ResourceType permission : filtered) {
			if (permission == ResourceType.Role) {
				Assert.assertEquals(OperationType.Write, permissionMap.get(ResourceType.Role));
			} else {
				Assert.fail("Unknown Permission");
			}
		}

		permissions = new ArrayList<>();

		permission1 = new RolePermissionDTO();
		permission1.setOperationType(OperationType.Read);
		permission1.setResourceType(ResourceType.Role);
		permissions.add(permission1);

		testRole.setPermissions(permissions);

		testRole = roleService.createOrUpdateRoles(Collections.singletonList(testRole)).get(0);

		// set global READ permission for user1 on User
		Map<ResourceType, OperationType> globalPermissions = new HashMap<>();
		globalPermissions.put(ResourceType.User, OperationType.Write);
		rolePermissionService.setUserGlobalPermissions(testUser1.getUsername(), globalPermissions);

		permissionMap = securityCheckService.loadUserPermissions(testUser1.getUsername());

		filtered = permissionMap.entrySet().stream().filter(e -> e.getValue() != OperationType.None).map(e -> e.getKey()).collect(Collectors.toList());
		Assert.assertEquals(2, filtered.size());
		for (ResourceType permission : filtered) {
			if (permission == ResourceType.User) {
				Assert.assertEquals(OperationType.Write, permissionMap.get(ResourceType.User));
			} else if (permission == ResourceType.Role) {
				Assert.assertEquals(OperationType.Read, permissionMap.get(ResourceType.Role));
			} else {
				Assert.fail("Unknown Permission");
			}
		}

		permissions = new ArrayList<>();

		testRole.setPermissions(permissions);

		testRole = roleService.createOrUpdateRoles(Collections.singletonList(testRole)).get(0);

		permissionMap = securityCheckService.loadUserPermissions(testUser1.getUsername());

		filtered = permissionMap.entrySet().stream().filter(e -> e.getValue() != OperationType.None).map(e -> e.getKey()).collect(Collectors.toList());
		Assert.assertEquals(1, filtered.size());
		for (ResourceType permission : filtered) {
			if (permission == ResourceType.User) {
				Assert.assertEquals(OperationType.Write, permissionMap.get(ResourceType.User));
			} else {
				Assert.fail("Unknown Permission");
			}
		}

		permissions = new ArrayList<>();

		permission1 = new RolePermissionDTO();
		permission1.setOperationType(OperationType.Read);
		permission1.setResourceType(ResourceType.Role);
		permissions.add(permission1);

		testRole.setPermissions(permissions);

		testRole = roleService.createOrUpdateRoles(Collections.singletonList(testRole)).get(0);

		// set global NONE permission for user1 on User
		globalPermissions = new HashMap<>();
		globalPermissions.put(ResourceType.User, OperationType.None);
		rolePermissionService.setUserGlobalPermissions(testUser1.getUsername(), globalPermissions);

		permissionMap = securityCheckService.loadUserPermissions(testUser1.getUsername());

		filtered = permissionMap.entrySet().stream().filter(e -> e.getValue() != OperationType.None).map(e -> e.getKey()).collect(Collectors.toList());
		Assert.assertEquals(2, filtered.size());
		for (ResourceType permission : filtered) {
			if (permission == ResourceType.User) {
				Assert.assertEquals(OperationType.Write, permissionMap.get(ResourceType.User));
			} else if (permission == ResourceType.Role) {
				Assert.assertEquals(OperationType.Read, permissionMap.get(ResourceType.Role));
			} else {
				Assert.fail("Unknown Permission");
			}
		}

		// remove role with permissions from user
		rolePermissionService.removeRoleFromUserByName(testUser1.getUsername(), "test-role");

		permissionMap = securityCheckService.loadUserPermissions(testUser1.getUsername());

		filtered = permissionMap.entrySet().stream().filter(e -> e.getValue() != OperationType.None).map(e -> e.getKey()).collect(Collectors.toList());
		Assert.assertEquals(1, filtered.size());

		// set global WRITE permission for user1 on User
		globalPermissions = new HashMap<>();
		globalPermissions.put(ResourceType.User, OperationType.Write);
		rolePermissionService.setUserGlobalPermissions(testUser1.getUsername(), globalPermissions);

		permissionMap = securityCheckService.loadUserPermissions(testUser1.getUsername());

		filtered = permissionMap.entrySet().stream().filter(e -> e.getValue() != OperationType.None).map(e -> e.getKey()).collect(Collectors.toList());
		Assert.assertEquals(1, filtered.size());
		for (ResourceType permission : filtered) {
			if (permission == ResourceType.User) {
				Assert.assertEquals(OperationType.Write, permissionMap.get(ResourceType.User));
			} else {
				Assert.fail("Unknown Permission");
			}
		}

	}

}
