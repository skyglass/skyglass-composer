package skyglass.composer.security.api;

import java.util.Map;

import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.ResourceType;

public interface RolePermissionApi {

	Map<ResourceType, OperationType> addRolePermissions(String roleUuid, Map<ResourceType, OperationType> permissionsToBeAddedOrUpdated);

	Map<ResourceType, OperationType> setRolePermissions(String roleUuid, Map<ResourceType, OperationType> permissionsToBeAddedOrUpdated);

	Map<ResourceType, OperationType> removeRolePermissions(String roleUuid, Map<ResourceType, OperationType> permissionsToBeAddedOrUpdated);

}
