package skyglass.composer.security.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.bean.AEntityBean;
import skyglass.composer.entity.EntityUtil;
import skyglass.composer.security.domain.Operation;
import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.ResourceType;
import skyglass.composer.security.domain.Role;
import skyglass.composer.security.domain.RolePermission;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class RolePermissionBean extends AEntityBean<RolePermission> {

	@Autowired
	private OperationBean operationBean;

	@Autowired
	private RoleBean roleBean;

	/**
	 * Loads all resource types for this role.
	 *
	 * @param role
	 *        Role to load resource types for.
	 * @return Map with all resource types and correspondent operations, defined for the given role.
	 */
	@NotNull
	public Map<ResourceType, OperationType> loadRolePermissions(Role role) {

		if (role != null) {
			// get all permissions for this role
			String queryStr = "SELECT DISTINCT rpv.RESOURCETYPE, op.NAME FROM ROLEPERMISSIONVIEW rpv "
					+ "JOIN OPERATION op ON op.UUID = rpv.OPERATION_UUID "
					+ "JOIN ROLE r ON r.UUID = rpv.ROLE_UUID AND rpv.ROLE_UUID = ?roleUuid";
			Query nativeQuery = entityBeanUtil.createNativeQuery(queryStr);
			nativeQuery.setParameter("roleUuid", role.getUuid());

			@SuppressWarnings("unchecked")
			List<Object[]> results = EntityUtil.getListResultSavely(nativeQuery);
			return getPermissionMap(results);
		}

		return Collections.emptyMap();

	}

	/**
	 * Update Resource Type Operation for Role
	 * 
	 * @param role
	 * @param resourceTypesToBeUpdated
	 */
	public Collection<RolePermission> addRolePermissions(Role role, Map<ResourceType, OperationType> resourceTypesToBeUpdated) {
		List<RolePermission> result = createRolePermissions(role, resourceTypesToBeUpdated);
		for (RolePermission rolePermission : result) {
			RolePermission toUpdate = findRolePermission(role, rolePermission);
			if (toUpdate != null) {
				//prevent duplicates
				entityBeanUtil.remove(toUpdate);
			}
		}
		role.setPermissions(result);
		roleBean.update(role);
		return result;
	}

	public Collection<RolePermission> removeRolePermissions(Role role, Map<ResourceType, OperationType> resourceTypesToBeRemoved) {
		List<RolePermission> result = createRolePermissions(role, resourceTypesToBeRemoved);
		for (RolePermission rolePermission : result) {
			RolePermission toDelete = findRolePermission(role, rolePermission);
			if (toDelete != null) {
				role.getPermissions().remove(toDelete);
			}
			entityBeanUtil.remove(toDelete);
		}
		roleBean.update(role);
		return result;
	}

	public Role setRolePermissionsFromMap(Role role, Map<ResourceType, OperationType> resourceTypesToBeUpdated) {
		List<RolePermission> result = createRolePermissions(role, resourceTypesToBeUpdated);
		return setRolePermissions(role, result);
	}

	public Role setRolePermissions(Role role, List<RolePermission> permissions) {
		String queryStr = "DELETE FROM RolePermission rp WHERE rp.role = :role";
		TypedQuery<RolePermission> query = entityBeanUtil.createQuery(queryStr, RolePermission.class);
		query.setParameter("role", role);
		query.executeUpdate();
		role.setPermissions(permissions);
		return roleBean.update(role);
	}

	private List<RolePermission> createRolePermissions(Role role, Map<ResourceType, OperationType> resourceTypesToBeUpdated) {
		if (role == null || resourceTypesToBeUpdated == null) {
			return Collections.emptyList();
		}
		Map<OperationType, Operation> operationMap = new HashMap<>();
		List<RolePermission> result = new ArrayList<>();
		for (Map.Entry<ResourceType, OperationType> entry : resourceTypesToBeUpdated.entrySet()) {
			if (entry.getValue() == OperationType.None) {
				continue;
			}
			RolePermission rolePermission = new RolePermission();
			rolePermission.setRole(role);
			rolePermission.setResourceType(entry.getKey());
			rolePermission.setOperation(operationBean.getOperation(entry.getValue(), operationMap));
			result.add(rolePermission);
		}

		return result;
	}

	private RolePermission findRolePermission(Role role, RolePermission example) {
		for (RolePermission rolePermission : role.getPermissions()) {
			if (rolePermission.getResourceType() == example.getResourceType()
					&& rolePermission.getOperation() == example.getOperation()) {
				return rolePermission;
			}
		}
		return null;
	}

	public Map<ResourceType, OperationType> getPermissionMap(List<Object[]> results) {

		if (results == null) {
			results = Collections.emptyList();
		}

		// create result structure
		Map<ResourceType, OperationType> authMap = new HashMap<>();

		// build map for all objects that have been found in the database
		for (Object[] result : results) {
			String resourceTypeString = (String) result[0];
			String operationString = (String) result[1];
			OperationType operationType = OperationType.valueOf(operationString);
			ResourceType resourceType = ResourceType.valueOf(resourceTypeString);
			OperationType currentOperationType = authMap.get(resourceType);
			if (currentOperationType == null || currentOperationType.getRank() < operationType.getRank()) {
				authMap.put(resourceType, operationType);
			}
		}

		// add all additional resource type that have no permission in the db
		// None -> no access
		for (ResourceType resourceType : ResourceType.values()) {
			if (!authMap.containsKey(resourceType)) {
				authMap.put(resourceType, OperationType.None);
			}
		}

		return authMap;

	}
}
