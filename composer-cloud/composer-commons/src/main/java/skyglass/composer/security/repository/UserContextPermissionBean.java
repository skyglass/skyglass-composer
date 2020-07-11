package skyglass.composer.security.repository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.bean.AEntityBean;
import skyglass.composer.entity.EntityUtil;
import skyglass.composer.security.api.PermissionApi;
import skyglass.composer.security.domain.ContextType;
import skyglass.composer.security.domain.Operation;
import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.RelationType;
import skyglass.composer.security.domain.ResourceType;
import skyglass.composer.security.domain.User;
import skyglass.composer.security.domain.UserContextPermission;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class UserContextPermissionBean extends AEntityBean<UserContextPermission> {

	@Autowired
	private PermissionApi permissionBean;

	@Autowired
	private RolePermissionBean rolePermissionBean;

	@Autowired
	private OperationBean operationBean;

	public Map<ResourceType, OperationType> loadPermissions(User user, String contextUuid) {
		return loadPermissions(user, contextUuid, null, null, null);
	}

	/**
	 * Loads all resource types and operations for this user.
	 *
	 * @param user
	 *        User to load resource types and operations for.
	 * @return Map with all the resource types and operations
	 *         for the given user.
	 */
	@NotNull
	public Map<ResourceType, OperationType> loadPermissions(User user, String contextUuid,
			String resourceUuid, RelationType relation, String ownerUuid) {

		if (user != null && StringUtils.isNotBlank(contextUuid)) {
			// get all permissions for this user
			String queryStr = "SELECT DISTINCT ucpv.RESOURCETYPE, op.NAME FROM USERCONTEXTPERMISSIONVIEW ucpv "
					+ "JOIN OPERATION op ON op.UUID = ucpv.OPERATION_UUID AND ucpv.USER_UUID = ?userUuid "
					+ (StringUtils.isBlank(contextUuid) ? "AND ucpv.context_uuid IS NULL " : "AND ucpv.context_uuid = ?contextUuid ")
					+ (StringUtils.isBlank(resourceUuid) ? "AND ucpvv.resource_uuid IS NULL " : "AND ucpvv.resource_uuid = ?resourceUuid ")
					+ ((relation == null) ? "AND ucpv.relation IS NULL " : "AND ucpv.relation = ?relation ")
					+ (StringUtils.isBlank(ownerUuid) ? "AND ucpv.owner_uuid IS NULL " : "AND ucpv.owner_uuid = ?ownerUuid ");
			Query nativeQuery = entityBeanUtil.createNativeQuery(queryStr);
			nativeQuery.setParameter("userUuid", user.getUuid());
			if (StringUtils.isNotBlank(contextUuid)) {
				nativeQuery.setParameter("contextUuid", contextUuid);
			}
			if (StringUtils.isNotBlank(resourceUuid)) {
				nativeQuery.setParameter("resourceUuid", resourceUuid);
			}
			if (relation != null) {
				nativeQuery.setParameter("relation", relation);
			}
			if (StringUtils.isNotBlank(ownerUuid)) {
				nativeQuery.setParameter("ownerUuid", ownerUuid);
			}

			@SuppressWarnings("unchecked")
			List<Object[]> results = EntityUtil.getListResultSavely(nativeQuery);
			return rolePermissionBean.getPermissionMap(results);
		}

		return Collections.emptyMap();
	}

	public Map<ResourceType, OperationType> loadPermissions(String contextUuid, String resourceUuid,
			RelationType relation, String ownerUuid) {
		User user = permissionBean.getUserFromCtx();
		return loadPermissions(user, contextUuid, resourceUuid, relation, ownerUuid);
	}

	public void updatePermissions(User user, String contextUuid, ContextType contextType, Map<ResourceType, OperationType> resourceTypesToBeUpdated) {
		updatePermissions(user, contextUuid, null, contextType, null, null, resourceTypesToBeUpdated);
	}

	public void updatePermissions(User user, String contextUuid, String resourceUuid, ContextType contextType,
			RelationType relation, String ownerUuid, Map<ResourceType, OperationType> resourceTypesToBeUpdated) {
		if (user == null || resourceTypesToBeUpdated == null || resourceTypesToBeUpdated.isEmpty()) {
			return;
		}

		Collection<UserContextPermission> userContextPermissions = getUserContextPermissions(user, contextUuid,
				resourceUuid, relation, ownerUuid);
		for (UserContextPermission userContextPermission : userContextPermissions) {
			deleteUserContextPermission(userContextPermission);
		}

		Map<OperationType, Operation> operationMap = new HashMap<>();
		for (Map.Entry<ResourceType, OperationType> entry : resourceTypesToBeUpdated.entrySet()) {
			createUserContextPermission(user, contextUuid, resourceUuid, contextType, relation, ownerUuid, entry, operationMap);
		}
	}

	public void postCreate(String resourceUuid, ResourceType resourceType) {
		assignOperationToUserContextResourceType(permissionBean.getUserFromCtx(), null, resourceUuid,
				null, null, null, resourceType, OperationType.Write);
	}

	public void assignOperationToUserResourceType(User user, ResourceType resourceType, OperationType operationType) {
		assignOperationToUserContextResourceType(user, null, null, null, null, null, resourceType, operationType);
	}

	public void assignOperationToUserResourceType(User user, String resourceUuid, ResourceType resourceType, OperationType operationType) {
		assignOperationToUserContextResourceType(user, null, resourceUuid, null, null, null, resourceType, operationType);
	}

	public void assignOperationToUserOwnerResourceType(User user, String ownerUuid, ResourceType resourceType, OperationType operationType) {
		assignOperationToUserContextResourceType(user, null, null, null, null, ownerUuid, resourceType, operationType);
	}

	public void assignOperationToUserContextResourceType(User user, String contextUuid, ContextType contextType,
			ResourceType resourceType, OperationType operationType) {
		assignOperationToUserContextResourceType(user, contextUuid, null, contextType, null, null, resourceType, operationType);
	}

	public void assignOperationToUserContextResourceType(User user, String contextUuid, String resourceUuid, ContextType contextType,
			RelationType relation, String ownerUuid, ResourceType resourceType, OperationType operationType) {
		if (user == null || StringUtils.isBlank(user.getUuid()) || resourceType == null || operationType == null) {
			return;
		}
		UserContextPermission userContextPermission = getUserContextPermission(user, contextUuid, resourceUuid, relation,
				ownerUuid, resourceType, operationType);
		if (userContextPermission == null) {
			Operation operation = operationBean.findByName(operationType);
			createEntity(user, contextUuid, resourceUuid, contextType, relation, ownerUuid, resourceType, operation);
		}
	}

	public void removeOperationFromUserContextResourceType(User user, String contextUuid, ContextType contextType,
			ResourceType resourceType, OperationType operationType) {
		removeOperationFromUserContextResourceType(user, contextUuid, null, contextType, null, null, resourceType, operationType);
	}

	public void removeOperationFromUserResourceResourceType(User user, String resourceUuid, ResourceType resourceType, OperationType operationType) {
		removeOperationFromUserContextResourceType(user, null, resourceUuid, null, null, null, resourceType, operationType);
	}

	public void removeOperationFromUserContextResourceType(User user, String contextUuid, String resourceUuid, ContextType contextType,
			RelationType relation, String ownerUuid, ResourceType resourceType, OperationType operationType) {
		if (user == null || StringUtils.isBlank(user.getUuid()) || resourceType == null || operationType == null) {
			return;
		}
		UserContextPermission userContextPermission = getUserContextPermission(user, contextUuid, resourceUuid, relation,
				ownerUuid, resourceType, operationType);
		if (userContextPermission != null) {
			deleteUserContextPermission(userContextPermission);
		}
	}

	private void createUserContextPermission(User user, String contextUuid, String resourceUuid, ContextType contextType,
			RelationType relation, String ownerUuid, Map.Entry<ResourceType, OperationType> entry,
			Map<OperationType, Operation> operationMap) {
		if (user == null || entry == null) {
			return;
		}
		if (entry.getValue() == OperationType.None) {
			return;
		}
		createEntity(user, contextUuid, resourceUuid, contextType, relation, ownerUuid, entry.getKey(),
				operationBean.getOperation(entry.getValue(), operationMap));
	}

	private void deleteUserContextPermission(UserContextPermission userContextPermission) {
		entityBeanUtil.remove(userContextPermission);
	}

	private Collection<UserContextPermission> getUserContextPermissions(User user, String contextUuid,
			String resourceUuid, RelationType relation, String ownerUuid) {
		if (user == null || StringUtils.isBlank(contextUuid)) {
			return Collections.emptyList();
		}
		TypedQuery<UserContextPermission> query = entityBeanUtil.createQuery(
				"SELECT ucp FROM UserContextPermission ucp WHERE ucp.user = :user "
						+ (StringUtils.isBlank(contextUuid) ? "AND ucp.context_uuid IS NULL " : "AND ucp.context_uuid = :contextUuid ")
						+ (StringUtils.isBlank(resourceUuid) ? "AND ucp.resource_uuid IS NULL " : "AND ucp.resource_uuid = :resourceUuid ")
						+ ((relation == null) ? "AND ucp.relation IS NULL " : "AND ucp.relation = :relation ")
						+ (StringUtils.isBlank(ownerUuid) ? "AND ucp.owner_uuid IS NULL " : "AND ucp.owner_uuid = :ownerUuid "),
				UserContextPermission.class);
		query.setParameter("user", user);
		if (StringUtils.isNotBlank(contextUuid)) {
			query.setParameter("contextUuid", contextUuid);
		}
		if (StringUtils.isNotBlank(resourceUuid)) {
			query.setParameter("resourceUuid", resourceUuid);
		}
		if (relation != null) {
			query.setParameter("relation", relation);
		}
		if (StringUtils.isNotBlank(ownerUuid)) {
			query.setParameter("ownerUuid", ownerUuid);
		}
		return EntityUtil.getListResultSafely(query);
	}

	public UserContextPermission getUserContextPermission(User user, String contextUuid,
			ResourceType resourceType, OperationType operationType) {
		return getUserContextPermission(user, contextUuid, null, null, null, resourceType, operationType);
	}

	public UserContextPermission getUserContextPermission(User user, String contextUuid, String resourceUuid,
			RelationType relation, String ownerUuid, ResourceType resourceType, OperationType operationType) {
		if (user == null || StringUtils.isBlank(user.getUuid()) || resourceType == null || operationType == null) {
			return null;
		}
		Operation operation = operationBean.findByName(operationType);
		String queryStr = "SELECT ucp FROM UserContextPermission ucp WHERE ucp.user = :user "
				+ (StringUtils.isBlank(contextUuid) ? "AND ucp.context_uuid IS NULL " : "AND ucp.context_uuid = :contextUuid ")
				+ (StringUtils.isBlank(resourceUuid) ? "AND ucp.resource_uuid IS NULL " : "AND ucp.resource_uuid = :resourceUuid ")
				+ ((relation == null) ? "AND ucp.relation IS NULL " : "AND ucp.relation = :relation ")
				+ (StringUtils.isBlank(ownerUuid) ? "AND ucp.owner_uuid IS NULL " : "AND ucp.owner_uuid = :ownerUuid ")
				+ "AND ucp.resourceType = :resourceType AND ucp.operation = :operation";
		TypedQuery<UserContextPermission> query = entityBeanUtil.createQuery(queryStr, UserContextPermission.class);
		query.setParameter("user", user);
		if (StringUtils.isNotBlank(contextUuid)) {
			query.setParameter("contextUuid", contextUuid);
		}
		if (StringUtils.isNotBlank(resourceUuid)) {
			query.setParameter("resourceUuid", resourceUuid);
		}
		if (relation != null) {
			query.setParameter("relation", relation);
		}
		if (StringUtils.isNotBlank(ownerUuid)) {
			query.setParameter("ownerUuid", ownerUuid);
		}
		query.setParameter("resourceType", resourceType);
		query.setParameter("operation", operation);

		return EntityUtil.getSingleResultSafely(query);
	}

	private UserContextPermission createEntity(User user, String contextUuid, String resourceUuid, ContextType contextType,
			RelationType relation, String ownerUuid, ResourceType resourceType, Operation operation) {
		UserContextPermission userContextPermission = new UserContextPermission();
		userContextPermission.setResourceType(resourceType);
		userContextPermission.setOperation(operation);
		userContextPermission.setUser(user);
		userContextPermission.setContext_uuid(contextUuid);
		userContextPermission.setResource_uuid(resourceUuid);
		userContextPermission.setOwner_uuid(ownerUuid);
		userContextPermission.setContextType(contextType);
		userContextPermission.setRelation(relation);
		create(userContextPermission);
		return userContextPermission;
	}

}
