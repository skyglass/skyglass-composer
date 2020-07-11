package skyglass.composer.security.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.exceptions.BusinessRuleValidationException;
import skyglass.composer.exceptions.NotNullableNorEmptyException;
import skyglass.composer.security.api.RoleApi;
import skyglass.composer.security.domain.Role;
import skyglass.composer.security.dto.OperationProvider;
import skyglass.composer.security.dto.RoleDTO;
import skyglass.composer.security.dto.RoleDTOFactory;
import skyglass.composer.security.dto.RolePermissionProvider;
import skyglass.composer.security.repository.OperationBean;
import skyglass.composer.security.repository.PermissionBean;
import skyglass.composer.security.repository.RoleBean;
import skyglass.composer.security.repository.RolePermissionBean;

@Service
@Transactional
public class RoleService implements RoleApi {

	@Autowired
	private RoleBean roleBean;

	@Autowired
	private OperationBean operationBean;

	@Autowired
	private RolePermissionBean rolePermissionBean;

	@Autowired
	private PermissionBean permissionBean;

	public List<RoleDTO> findRoles() {
		return findAll();
	}

	public RoleDTO findByUuid(String uuid) {
		permissionBean.checkAdmin();
		Role role = roleBean.findByUuidSecure(uuid);
		return RoleDTOFactory.createRoleDTO(role, getRolePermissionProvider(), getInheritedRolePermissionProvider());
	}

	public List<RoleDTO> findAll() {
		permissionBean.checkAdmin();
		Collection<Role> roles = roleBean.findAll();
		return RoleDTOFactory.createRoleDTOs(roles, getRolePermissionProvider(), getInheritedRolePermissionProvider());
	}

	public List<RoleDTO> findAndInvertParents() {
		return invertOutputDtoChildParents(findAll());
	}

	public List<RoleDTO> findTree() {
		permissionBean.checkAdmin();
		Collection<Role> roles = roleBean.findAll();
		return RoleDTOFactory.createRoleDTOTree(roles, getRolePermissionProvider(), getInheritedRolePermissionProvider());
	}

	public List<RoleDTO> findAllowedParents(String roleUuid) {
		permissionBean.checkAdmin();
		Role role = roleBean.findByUuidSecure(roleUuid);
		Collection<Role> roles = roleBean.getAllowedParents(role);
		return RoleDTOFactory.createRoleDTOs(roles, getEmptyRolePermissionProvider(), getEmptyRolePermissionProvider());
	}

	@Override
	public RoleDTO findByName(String name) {
		permissionBean.checkAdmin();
		return RoleDTOFactory.createRoleDTO(roleBean.findByName(name), getRolePermissionProvider(),
				getInheritedRolePermissionProvider());
	}

	private Map<String, Role> validateRoles(List<RoleDTO> dtos, Map<String, Role> roleUuidCache,
			Map<String, Role> roleNameCache) {
		Map<String, List<String>> parentChildrenMap = new HashMap<>();
		Map<String, RoleDTO> roleDtoCache = new HashMap<>();
		for (RoleDTO dto : dtos) {
			if (StringUtils.isBlank(dto.getName())) {
				throw new NotNullableNorEmptyException("Role Name");
			}

			if (roleNameCache.get(dto.getName()) != null) {
				throw new BusinessRuleValidationException("There is more than one role with the same name in the tree: " + dto.getName()
						+ ". Please, rename the duplicate role and try again");
			}

			Role parent = StringUtils.isNotBlank(dto.getParentUuid()) ? roleUuidCache.get(dto.getParentUuid()) : null;
			if (parent == null && StringUtils.isNotBlank(dto.getParentUuid())) {
				parent = roleBean.findByUuidSecure(dto.getParentUuid());
			}

			Role role = StringUtils.isNotBlank(dto.getUuid()) ? roleBean.findByUuidSecure(dto.getUuid())
					: RoleDTOFactory.createBasicRole(null, dto.getParentUuid(), dto);

			validateConstraints(parent, role);

			if (StringUtils.isNotBlank(dto.getUuid())) {
				roleDtoCache.put(dto.getUuid(), dto);
				if (roleUuidCache.get(dto.getUuid()) != null) {
					throw new BusinessRuleValidationException("Role with uuid: " + dto.getUuid() + " and name: " + roleUuidCache.get(dto.getUuid()).getName()
							+ " has more than one occurence in the tree, which can lead to cyclic parent-child relationship. Please, remove the duplicates and try again");
				} else {
					roleUuidCache.put(dto.getUuid(), role);
				}
			}

			roleNameCache.put(role.getName(), role);

			if (StringUtils.isNotBlank(dto.getUuid())) {
				if (parent != null) {
					parentChildrenMap.computeIfAbsent(parent.getUuid(), key -> new ArrayList<>()).add(dto.getUuid());
				}
			}

		}

		Collection<String> parents = parentChildrenMap.keySet();
		Collection<String> rootParents = getRootParents(parents, roleDtoCache);
		if (CollectionUtils.isEmpty(rootParents) && CollectionUtils.isNotEmpty(parents)) {
			throw new BusinessRuleValidationException(
					"The Role Tree doesn't have root parent, which means either broken tree, or cyclic dependency. "
							+ "Please, fix the tree or remove cyclic dependencies and try again");
		}
		validateTree(rootParents, parentChildrenMap, new HashSet<>(), roleUuidCache);

		return roleUuidCache;
	}

	private Collection<String> getRootParents(Collection<String> parents, Map<String, RoleDTO> roleDtoCache) {
		return parents.stream().filter(r -> {
			RoleDTO dto = roleDtoCache.get(r);
			return dto == null || StringUtils.isBlank(dto.getParentUuid());
		}).collect(Collectors.toList());
	}

	private void validateTree(Collection<String> parents, Map<String, List<String>> parentChildrenMap, Set<String> cache, Map<String, Role> roleCache) {
		for (String parent : parents) {
			if (cache.contains(parent)) {
				throw new BusinessRuleValidationException("Role with uuid: " + parent + " and name: " + roleCache.get(parent).getName()
						+ " has more than one occurence in the tree, which means cyclic parent-child relationship. Please, remove the cyclic parent-child relationship and try again");
			} else {
				cache.add(parent);
			}
			List<String> children = parentChildrenMap.get(parent);
			if (CollectionUtils.isNotEmpty(children)) {
				validateTree(children, parentChildrenMap, cache, roleCache);
			}
		}
	}

	public List<RoleDTO> createOrUpdateRoles(List<RoleDTO> dtos) {
		permissionBean.checkAdmin();
		Map<String, Role> cache = validateRoles(dtos, new HashMap<>(), new HashMap<>());
		return dtos.stream().map(dto -> createOrUpdateRole(dto.getUuid(), dto, cache)).collect(Collectors.toList());
	}

	public List<RoleDTO> createOrUpdateRolesAndInvertParents(List<RoleDTO> dtos) {
		return invertOutputDtoChildParents(createOrUpdateRoles(invertInputDtoChildParents(dtos)));
	}

	private RoleDTO createOrUpdateRole(String roleUuid, RoleDTO dto, Map<String, Role> cache) {
		if (StringUtils.isNotBlank(dto.getUuid())) {
			return updateRole(dto.getUuid(), dto, cache);
		} else {
			return createRole(dto);
		}
	}

	@Override
	public RoleDTO createRole(RoleDTO dto) {
		Role parent = StringUtils.isNotBlank(dto.getParentUuid()) ? roleBean.findByUuidSecure(dto.getParentUuid()) : null;
		Role role = roleBean.create(RoleDTOFactory.createRole(dto, getOperationProvider(), getEmptyRolePermissionProvider()));
		validateConstraints(parent, role);
		return RoleDTOFactory.createRoleDTO(role, getRolePermissionProvider(), getInheritedRolePermissionProvider());
	}

	private RoleDTO updateRole(String roleUuid, RoleDTO dto, Map<String, Role> cache) {
		Role parent = StringUtils.isNotBlank(dto.getParentUuid()) ? cache.get(dto.getParentUuid()) : null;
		if (parent == null) {
			parent = StringUtils.isNotBlank(dto.getParentUuid()) ? roleBean.findByUuidSecure(dto.getParentUuid()) : null;
		}
		Role role = cache.get(roleUuid);
		if (role == null) {
			role = roleBean.findByUuidSecure(roleUuid);
		}
		role = RoleDTOFactory.createRole(role.getUuid(), parent != null ? parent.getUuid() : null, dto, getOperationProvider());
		role = rolePermissionBean.setRolePermissions(role, role.getPermissions());
		validateConstraints(parent, role);
		return RoleDTOFactory.createRoleDTO(role, getRolePermissionProvider(), getInheritedRolePermissionProvider());
	}

	public RoleDTO deleteRole(String uuid) {
		permissionBean.checkAdmin();
		Role role = roleBean.findByUuidSecure(uuid);
		role = roleBean.delete(uuid);
		return RoleDTOFactory.createRoleDTO(role, getEmptyRolePermissionProvider(), getEmptyRolePermissionProvider());
	}

	private OperationProvider getOperationProvider() {
		return new OperationProvider(op -> operationBean.findByName(op));
	}

	private RolePermissionProvider getRolePermissionProvider() {
		return new RolePermissionProvider();
	}

	private RolePermissionProvider getInheritedRolePermissionProvider() {
		return new RolePermissionProvider(r -> rolePermissionBean.loadRolePermissions(r));
	}

	public static RolePermissionProvider getEmptyRolePermissionProvider() {
		return new RolePermissionProvider(r -> Collections.emptyMap());
	}

	private void validateConstraints(Role parent, Role child) {
		if (StringUtils.isBlank(child.getUuid())) {
			roleBean.checkNameDuplicate(child);
		}
	}

	/**
	 * inverts child-parent relationships in the input DTOs (sent from UI)
	 * For example, if:
	 * 1. dto1 has no children
	 * 2. dto1.parent = dto2
	 * 3. dto2.parent = null,
	 * 4. dto2 has one child: dto1
	 * then in the adapted result:
	 * 1. dto2 has no children
	 * 2. dto2.parent = dto1
	 * 3. dto1.parent = null,
	 * 4. dto1 has one child: dto2
	 * 
	 */
	private List<RoleDTO> invertInputDtoChildParents(List<RoleDTO> dtos) {
		Map<String, RoleDTO> roleUuidMap = new HashMap<>();
		for (RoleDTO dto : dtos) {
			if (StringUtils.isBlank(dto.getUuid())) {
				Role createdRole = createBasicRole(dto);
				dto.setUuid(createdRole.getUuid());
			}
			roleUuidMap.put(dto.getUuid(), dto);
		}

		return invertOutputDtoChildParents(dtos, roleUuidMap);
	}

	/**
	 * inverts child-parent relationships in the output DTOs (sent to UI)
	 */
	private List<RoleDTO> invertOutputDtoChildParents(List<RoleDTO> dtos) {
		Map<String, RoleDTO> roleUuidMap = new HashMap<>();
		for (RoleDTO dto : dtos) {
			roleUuidMap.put(dto.getUuid(), dto);
		}
		return invertOutputDtoChildParents(dtos, roleUuidMap);
	}

	private List<RoleDTO> invertOutputDtoChildParents(List<RoleDTO> dtos, Map<String, RoleDTO> roleUuidMap) {
		Map<String, String> childParentMap = new HashMap<>();
		for (RoleDTO dto : dtos) {
			if (StringUtils.isNotBlank(dto.getParentUuid())) {
				childParentMap.put(dto.getParentUuid(), dto.getUuid());
				dto.setParentUuid(null);
			}
		}

		for (String childUuid : childParentMap.keySet()) {
			RoleDTO result = roleUuidMap.get(childUuid);
			if (result != null) {
				String parentUuid = childParentMap.get(childUuid);
				if (StringUtils.isNotBlank(parentUuid)) {
					result.setParentUuid(parentUuid);
				}
			}
		}

		return dtos;
	}

	private Role createBasicRole(RoleDTO dto) {
		Role role = roleBean.create(RoleDTOFactory.createBasicRole(null, null, dto));
		return role;
	}

}
