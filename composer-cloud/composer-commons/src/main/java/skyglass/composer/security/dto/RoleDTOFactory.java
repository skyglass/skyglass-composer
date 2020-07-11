package skyglass.composer.security.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import skyglass.composer.dto.AEntityDTOFactory;
import skyglass.composer.security.domain.Role;

/**
 * @author skyglass
 *
 */
public class RoleDTOFactory extends AEntityDTOFactory {

	public static RoleDTO createRoleDTO(Role role, RolePermissionProvider rolePermissionProvider, RolePermissionProvider inheritedRolePermissionProvider) {
		RoleDTO roleDTO = new RoleDTO();
		roleDTO.setUuid(role.getUuid());
		roleDTO.setName(role.getName());
		if (role.getParent() != null) {
			roleDTO.setParentUuid(role.getParent().getUuid());
		}
		roleDTO.setPermissions(RolePermissionDTOFactory.createRolePermissionDTOs(rolePermissionProvider.get(role), inheritedRolePermissionProvider.get(role)));
		return roleDTO;
	}

	public static Role createRole(RoleDTO roleDTO, OperationProvider operationProvider, RolePermissionProvider inheritedRolePermissionProvider) {
		Role role = createBasicRole(roleDTO.getUuid(), roleDTO.getParentUuid(), roleDTO);
		updateRole(role, roleDTO, operationProvider);
		return role;
	}

	public static Role createBasicRole(String uuid, String parentUuid, RoleDTO roleDTO) {
		Role role = new Role();
		role.setUuid(uuid);
		updateBasicRole(roleDTO, parentUuid, role);
		return role;
	}

	public static Role createRole(String uuid, String parentUuid, RoleDTO roleDTO, OperationProvider operationProvider) {
		Role role = createBasicRole(uuid, parentUuid, roleDTO);
		updateRole(role, roleDTO, operationProvider);
		return role;
	}

	private static Role updateBasicRole(RoleDTO roleDTO, String parentUuid, Role role) {
		if (StringUtils.isNotBlank(parentUuid)) {
			Role parent = new Role();
			parent.setUuid(parentUuid);
			role.setParent(parent);
		}
		role.setName(roleDTO.getName());
		return role;
	}

	private static void updateRole(Role role, RoleDTO roleDTO, OperationProvider operationProvider) {
		role.setPermissions(RolePermissionDTOFactory.createRolePermissions(role, roleDTO.getPermissions(), operationProvider));
	}

	public static List<RoleDTO> createRoleDTOs(Collection<Role> roles, RolePermissionProvider rolePermissionProvider,
			RolePermissionProvider inheritedRolePermissionProvider) {
		if (roles == null) {
			return Collections.emptyList();
		}
		List<RoleDTO> result = roles.stream().map(entity -> createRoleDTO(entity, rolePermissionProvider, inheritedRolePermissionProvider))
				.collect(Collectors.toList());
		return result;
	}

	public static List<RoleDTO> createRoleDTOTree(Collection<Role> roles, RolePermissionProvider rolePermissionProvider,
			RolePermissionProvider inheritedRolePermissionProvider) {
		return constructTree(null, createRoleDTOs(roles, rolePermissionProvider, inheritedRolePermissionProvider), null);
	}

	public static List<RoleDTO> constructTree(RoleDTO parent, List<RoleDTO> dtos, String parentUuid) {
		List<RoleDTO> result = new ArrayList<>();
		for (RoleDTO dto : dtos) {
			if (Objects.equals(dto.getParentUuid(), parentUuid)) {
				constructTree(dto, dtos, dto.getUuid());
				result.add(dto);
			}
		}
		if (parent != null) {
			parent.setChildren(result);
		}
		return result;
	}

}
