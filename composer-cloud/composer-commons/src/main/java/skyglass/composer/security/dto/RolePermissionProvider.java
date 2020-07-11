package skyglass.composer.security.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.ResourceType;
import skyglass.composer.security.domain.Role;
import skyglass.composer.security.domain.RolePermission;

public class RolePermissionProvider {

	private Map<Role, Map<ResourceType, OperationType>> map = new HashMap<>();

	private Function<Role, Map<ResourceType, OperationType>> provider;

	public RolePermissionProvider() {
		this(null);
	}

	public RolePermissionProvider(Function<Role, Map<ResourceType, OperationType>> provider) {
		this.provider = provider;
	}

	public Map<ResourceType, OperationType> get(Role role) {
		Map<ResourceType, OperationType> permissions = map.get(role);
		if (permissions == null) {
			permissions = this.provider != null ? provider.apply(role) : getRolePermissions(role);
			map.put(role, permissions);
		}

		return permissions;
	}

	private Map<ResourceType, OperationType> getRolePermissions(Role role) {
		Map<ResourceType, OperationType> permissions = new HashMap<>();
		for (RolePermission result : role.getPermissions()) {
			OperationType operationType = result.getOperation().getName();
			ResourceType resourceType = result.getResourceType();
			OperationType currentOperationType = permissions.get(resourceType);
			if (currentOperationType == null || currentOperationType.getRank() < operationType.getRank()) {
				permissions.put(resourceType, operationType);
			}
		}

		// add all additional resource type that have no permission in the db
		// None -> no access
		for (ResourceType resourceType : ResourceType.values()) {
			if (!permissions.containsKey(resourceType)) {
				permissions.put(resourceType, OperationType.None);
			}
		}

		return permissions;
	}

}
