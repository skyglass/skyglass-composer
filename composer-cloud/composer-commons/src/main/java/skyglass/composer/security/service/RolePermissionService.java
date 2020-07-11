package skyglass.composer.security.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.exceptions.NotAccessibleException;
import skyglass.composer.exceptions.NotNullableNorEmptyException;
import skyglass.composer.security.api.RolePermissionApi;
import skyglass.composer.security.domain.ContextType;
import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.ResourceType;
import skyglass.composer.security.domain.Role;
import skyglass.composer.security.domain.User;
import skyglass.composer.security.domain.UserContextPermission;
import skyglass.composer.security.domain.UserContextRole;
import skyglass.composer.security.dto.RoleDTO;
import skyglass.composer.security.dto.RoleDTOFactory;
import skyglass.composer.security.repository.PermissionBean;
import skyglass.composer.security.repository.RoleBean;
import skyglass.composer.security.repository.RolePermissionBean;
import skyglass.composer.security.repository.SecurityCheckBean;
import skyglass.composer.security.repository.UserContextPermissionBean;
import skyglass.composer.security.repository.UserContextRoleBean;

/**
 *
 * @author skyglass
 */
@Service
@Transactional
public class RolePermissionService implements RolePermissionApi {

	@Autowired
	private UserContextPermissionBean userContextPermissionBean;

	@Autowired
	private SecurityCheckBean securityCheckBean;

	@Autowired
	private UserContextRoleBean userContextRoleBean;

	@Autowired
	private RoleBean roleBean;

	@Autowired
	private PermissionBean permissionBean;

	@Autowired
	private RolePermissionBean rolePermissionBean;

	@NotNull
	@Override
	public Map<ResourceType, OperationType> addRolePermissions(String roleUuid, Map<ResourceType, OperationType> permissionsToBeAddedOrUpdated) {
		permissionBean.checkAdmin();
		Role role = getRole(roleUuid);
		rolePermissionBean.addRolePermissions(role, permissionsToBeAddedOrUpdated);
		return rolePermissionBean.loadRolePermissions(role);
	}

	@NotNull
	@Override
	public Map<ResourceType, OperationType> setRolePermissions(String roleUuid, Map<ResourceType, OperationType> permissionsToBeAddedOrUpdated) {
		permissionBean.checkAdmin();
		Role role = getRole(roleUuid);
		rolePermissionBean.setRolePermissionsFromMap(role, permissionsToBeAddedOrUpdated);
		return rolePermissionBean.loadRolePermissions(role);
	}

	@NotNull
	@Override
	public Map<ResourceType, OperationType> removeRolePermissions(String roleUuid, Map<ResourceType, OperationType> permissionsToBeRemoved) {
		permissionBean.checkAdmin();
		Role role = getRole(roleUuid);
		rolePermissionBean.removeRolePermissions(role, permissionsToBeRemoved);
		return rolePermissionBean.loadRolePermissions(role);
	}

	private Role getRole(String roleUuid) {
		permissionBean.checkAdmin();
		if (StringUtils.isBlank(roleUuid)) {
			throw new NotNullableNorEmptyException("Role UUID");
		}

		Role role = roleBean.findByUuid(roleUuid);
		if (role == null) {
			throw new NotAccessibleException(Role.class, roleUuid);
		}
		return role;
	}

	@NotNull
	public Map<ResourceType, OperationType> setUserGlobalPermissions(String userId, Map<ResourceType, OperationType> permissions)
			throws NotNullableNorEmptyException, NotAccessibleException {

		return setUserContextPermissions(userId, null, null, permissions);
	}

	@NotNull
	public Map<ResourceType, OperationType> setUserContextPermissions(String userId, String contextUuid, ContextType contextType, Map<ResourceType, OperationType> permissions)
			throws NotNullableNorEmptyException, NotAccessibleException {

		if (StringUtils.isBlank(userId)) {
			throw new NotNullableNorEmptyException("User ID");
		}

		User user = getUserAndCheckAdmin(userId);

		// create or update global permissions
		userContextPermissionBean.updatePermissions(user, contextUuid, contextType, permissions);
		return userContextPermissionBean.loadPermissions(user, contextUuid);
	}

	@NotNull
	public List<RoleDTO> setUserContextRoles(String userId, String contextUuid,
			ContextType contextType, List<String> rolesToBeAddedOrUpdated) {
		if (StringUtils.isBlank(userId)) {
			throw new NotNullableNorEmptyException("User ID");
		}

		if (contextType == null) {
			throw new NotNullableNorEmptyException("Context Type");
		}

		// create or update object roles
		userContextRoleBean.updateRoles(getUserAndCheckAdmin(userId), contextUuid, contextType, roleBean.findByUuids(rolesToBeAddedOrUpdated));
		return loadUserContextRoles(contextUuid, userId);
	}

	public void assignRoleToUserContext(String userId, String contextUuid, ContextType contextType, String roleUuid) {
		Role role = roleBean.findByUuid(roleUuid);
		userContextRoleBean.assignRoleToUserContext(getUserAndCheckAdmin(userId), contextUuid, contextType, role);
	}

	public void assignRoleToUserByName(String userId, String roleName) {
		assignRoleToUserContextByName(userId, null, null, roleName);
	}

	public void assignRoleToUserByName(String userId, String ownerUuid, String roleName) {
		Role role = roleBean.findByName(roleName);
		userContextRoleBean.assignRoleToUser(getUserAndCheckAdmin(userId), ownerUuid, role);
	}

	public void assignRoleToUserContextByName(String userId, String contextUuid, ContextType contextType, String roleName) {
		Role role = roleBean.findByName(roleName);
		userContextRoleBean.assignRoleToUserContext(getUserAndCheckAdmin(userId), contextUuid, contextType, role);
	}

	public void assignOperationToUserResourceType(String userId, ResourceType resourceType, OperationType operationType) {
		userContextPermissionBean.assignOperationToUserResourceType(getUser(userId), resourceType, operationType);
	}

	public void assignOperationToUserResourceType(String userId, String resourceUuid, ResourceType resourceType, OperationType operationType) {
		userContextPermissionBean.assignOperationToUserResourceType(getUser(userId), resourceUuid, resourceType, operationType);
	}

	public void assignOperationToUserOwnerResourceType(String userId, String ownerUuid, ResourceType resourceType, OperationType operationType) {
		userContextPermissionBean.assignOperationToUserOwnerResourceType(getUser(userId), ownerUuid, resourceType, operationType);
	}

	public void assignOperationToUserContextResourceType(String userId, String contextUuid, ContextType contextType,
			ResourceType resourceType, OperationType operationType) {
		userContextPermissionBean.assignOperationToUserContextResourceType(getUser(userId), contextUuid, contextType, resourceType, operationType);
	}

	public List<RoleDTO> loadUserContextRoles(String contextUuid, String userId) {
		return RoleDTOFactory.createRoleDTOs(userContextRoleBean.getRoles(getUserAndCheckAdmin(userId), contextUuid),
				RoleService.getEmptyRolePermissionProvider(),
				RoleService.getEmptyRolePermissionProvider());
	}

	public List<RoleDTO> loadCurrentUserContextRoles(String contextUuid) {
		return loadUserContextRoles(contextUuid, null);
	}

	public Map<ResourceType, OperationType> loadUserContextPermissions(String contextUuid, String userId) {
		return userContextPermissionBean.loadPermissions(getUserAndCheckAdmin(userId), contextUuid);
	}

	public Map<ResourceType, OperationType> loadCurrentUserContextPermissions(String contextUuid) {
		return loadUserContextPermissions(contextUuid, null);
	}

	@NotNull
	public Map<ResourceType, OperationType> getDefaultPermissions() {
		Map<ResourceType, OperationType> permissions = new HashMap<>();

		for (ResourceType resourceType : ResourceType.values()) {
			permissions.put(resourceType, OperationType.None);
		}

		return permissions;
	}

	public boolean canRead(String userId, ResourceType resourceType, String contextUuid) {
		return securityCheckBean.canReadForContext(getUser(userId), null, resourceType, contextUuid);
	}

	public boolean canWrite(String userId, ResourceType resourceType, String contextUuid) {
		return securityCheckBean.canWriteForContext(getUser(userId), null, resourceType, contextUuid);
	}

	public void assignPermissionToUserContext(String userId, String contextUuid, ContextType contextType, ResourceType resourceType, OperationType operationType) {
		userContextPermissionBean.assignOperationToUserContextResourceType(getUserAndCheckAdmin(userId), contextUuid, contextType, resourceType, operationType);
	}

	public void removePermissionFromUserContext(String userId, String contextUuid, ContextType contextType, ResourceType resourceType, OperationType operationType) {
		userContextPermissionBean.removeOperationFromUserContextResourceType(getUserAndCheckAdmin(userId), contextUuid, contextType, resourceType, operationType);
	}

	public void removePermissionFromUserResource(String userId, String resourceUuid, ResourceType resourceType, OperationType operationType) {
		userContextPermissionBean.removeOperationFromUserResourceResourceType(getUserAndCheckAdmin(userId), resourceUuid, resourceType, operationType);
	}

	public void removeRoleFromUserContext(String userId, String contextUuid, ContextType contextType, String roleUuid) {
		Role role = roleBean.findByUuid(roleUuid);
		userContextRoleBean.removeRoleFromUserContext(getUserAndCheckAdmin(userId), contextUuid, role);
	}

	public void removeRoleFromUserContextByName(String userId, String contextUuid, ContextType contextType, String roleName) {
		Role role = roleBean.findByName(roleName);
		userContextRoleBean.removeRoleFromUserContext(getUserAndCheckAdmin(userId), contextUuid, role);
	}

	public void removeRoleFromUserOwnerByName(String userId, String ownerUuid, String roleName) {
		Role role = roleBean.findByName(roleName);
		userContextRoleBean.removeRoleFromUserOwner(getUserAndCheckAdmin(userId), ownerUuid, role);
	}

	public void removeRoleFromUserByName(String userId, String roleName) {
		Role role = roleBean.findByName(roleName);
		userContextRoleBean.removeRoleFromUser(getUserAndCheckAdmin(userId), role);
	}

	public UserContextPermission getUserContextPermission(String userId, String contextUuid, ResourceType resourceType, OperationType operationType) {
		return userContextPermissionBean.getUserContextPermission(getUserAndCheckAdmin(userId), contextUuid, resourceType, operationType);
	}

	public UserContextRole getUserContextRole(String userId, String contextUuid, String roleUuid) {
		Role role = roleBean.findByUuid(roleUuid);
		return userContextRoleBean.getUserContextRole(getUserAndCheckAdmin(userId), contextUuid, role);
	}

	public UserContextRole getUserContextRoleByName(String userId, String contextUuid, String roleName) {
		Role role = roleBean.findByName(roleName);
		return userContextRoleBean.getUserContextRole(getUserAndCheckAdmin(userId), contextUuid, role);
	}

	private User getUser(String userId) {
		return permissionBean.getUser(userId);
	}

	private User getUserAndCheckAdmin(String userId) {
		User user = getUser(userId);
		permissionBean.checkAdmin();
		return user;
	}

}
