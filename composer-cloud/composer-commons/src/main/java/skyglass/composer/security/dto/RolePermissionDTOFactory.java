package skyglass.composer.security.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import skyglass.composer.dto.AEntityDTOFactory;
import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.ResourceType;
import skyglass.composer.security.domain.Role;
import skyglass.composer.security.domain.RolePermission;

/**
 * @author skyglass
 *
 */
public class RolePermissionDTOFactory extends AEntityDTOFactory {

	public static RolePermissionDTO createRolePermissionDTO(Map.Entry<ResourceType, OperationType> rolePermissions, Map<ResourceType, OperationType> directRolePermissions) {
		RolePermissionDTO rolePermissionDTO = new RolePermissionDTO();
		rolePermissionDTO.setOperationType(rolePermissions.getValue());
		rolePermissionDTO.setResourceType(rolePermissions.getKey());
		OperationType directOperationType = directRolePermissions.get(rolePermissionDTO.getResourceType());
		if (directOperationType == null) {
			rolePermissionDTO.setInherited(true);
		} else {
			rolePermissionDTO.setOperationType(directOperationType.getRank() > rolePermissions.getValue().getRank() ? directOperationType : rolePermissions.getValue());
		}
		return rolePermissionDTO;
	}

	public static RolePermission createRolePermission(Role role, RolePermissionDTO rolePermissionDTO, OperationProvider operationCache) {
		RolePermission rolePermission = new RolePermission();
		rolePermission.setUuid(rolePermissionDTO.getUuid());
		rolePermission.setResourceType(rolePermissionDTO.getResourceType());
		rolePermission.setRole(role);
		rolePermission.setOperation(operationCache.get(rolePermissionDTO.getOperationType()));
		return rolePermission;
	}

	public static List<RolePermissionDTO> createRolePermissionDTOs(Map<ResourceType, OperationType> rolePermissions, Map<ResourceType, OperationType> inheritedRolePermissions) {
		if (rolePermissions == null) {
			return Collections.emptyList();
		}
		return inheritedRolePermissions.entrySet().stream().map(entry -> createRolePermissionDTO(entry, rolePermissions))
				.collect(Collectors.toList());
	}

	public static List<RolePermission> createRolePermissions(Role role, Collection<RolePermissionDTO> rolePermissionDTOs, OperationProvider operationProvider) {
		if (rolePermissionDTOs == null) {
			return Collections.emptyList();
		}
		Map<ResourceType, RolePermissionDTO> permissions = new HashMap<>();
		for (RolePermissionDTO result : rolePermissionDTOs) {
			OperationType operationType = result.getOperationType();
			ResourceType resourceType = result.getResourceType();
			RolePermissionDTO currentResult = permissions.get(resourceType);
			if (operationType != OperationType.None && (currentResult == null || currentResult.getOperationType().getRank() < operationType.getRank())) {
				permissions.put(resourceType, result);
			}
		}

		return permissions.values().stream().map(dto -> createRolePermission(role, dto, operationProvider))
				.collect(Collectors.toList());
	}

}
