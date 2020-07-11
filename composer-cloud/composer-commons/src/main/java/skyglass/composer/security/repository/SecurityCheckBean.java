package skyglass.composer.security.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.bean.EntityBeanUtil;
import skyglass.composer.domain.CrudAction;
import skyglass.composer.domain.IIdentifiable;
import skyglass.composer.entity.EntityUtil;
import skyglass.composer.exceptions.PermissionDeniedException;
import skyglass.composer.security.api.PermissionApi;
import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.ResourceType;
import skyglass.composer.security.domain.User;
import skyglass.composer.security.domain.UserContextPermission;
import skyglass.composer.security.domain.UserContextRole;
import skyglass.composer.security.dto.SecurityCacheRegistry;
import skyglass.composer.utils.query.NativeQueryUtil;

/**
 * Security check for different resource types.
 *
 * @author skyglass
 *
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class SecurityCheckBean {

	@Autowired
	private PermissionApi permissionApi;

	@Autowired
	protected EntityBeanUtil entityBeanUtil;

	public boolean canReadForAnyContext(String ownerUuid, ResourceType resourceType) {
		return canReadForAnyContext(null, ownerUuid, resourceType);
	}

	public boolean canReadForAnyContext(String userId, String ownerUuid, ResourceType resourceType) {
		return canReadForAnyContext(permissionApi.getUser(userId), ownerUuid, resourceType, null);
	}

	public boolean canReadForAnyContext(String ownerUuid, ResourceType resourceType, Collection<String> contextUuids) {
		return canReadForAnyContext(null, ownerUuid, resourceType, contextUuids);
	}

	public boolean canReadForAnyContext(User user, String ownerUuid, ResourceType resourceType, Collection<String> contextUuids) {
		return hasPermissionForAnyContext(user, ownerUuid, resourceType, OperationType.Read, contextUuids);
	}

	public boolean canReadForContext(String ownerUuid, ResourceType resourceType, String contextUuid) {
		return canReadForContext(null, ownerUuid, resourceType, contextUuid);
	}

	public boolean canReadForContext(User user, String ownerUuid, ResourceType resourceType, String contextUuid) {
		return hasPermissionForContext(user, ownerUuid, resourceType, OperationType.Read, contextUuid);
	}

	public boolean canWriteForAnyContext(String ownerUuid, ResourceType resourceType) {
		return canWriteForAnyContext(null, ownerUuid, resourceType);
	}

	public boolean canWriteForAnyContext(String userId, String ownerUuid, ResourceType resourceType) {
		return canWriteForAnyContext(permissionApi.getUser(userId), ownerUuid, resourceType, null);
	}

	public boolean checkWriteForAnyContext(String ownerUuid, ResourceType resourceType, CrudAction action, Class<? extends IIdentifiable> resourceClass) {
		return checkWriteForAnyContext(ownerUuid, resourceType, action, resourceClass, null);
	}

	public boolean checkWriteForAnyContext(String ownerUuid, ResourceType resourceType, CrudAction action, Class<? extends IIdentifiable> resourceClass, Collection<String> contextUuids) {
		if (!canWriteForAnyContext(ownerUuid, resourceType, contextUuids)) {
			throw new PermissionDeniedException(resourceClass, action);
		}
		return true;
	}

	public void checkPermissionForResource(String ownerUuid, ResourceType resourceType, OperationType operation, String resourceUuid, Class<? extends IIdentifiable> resourceClass) {
		checkPermissionForResource(ownerUuid, resourceType, operation, resourceUuid, resourceUuid, CrudAction.CREATE, resourceClass, resourceClass);
	}

	public void checkPermissionForResource(ResourceType resourceType, OperationType operation, String resourceUuid, Class<? extends IIdentifiable> resourceClass) {
		checkPermissionForResource(null, resourceType, operation, resourceUuid, resourceUuid, CrudAction.CREATE, resourceClass, resourceClass);
	}

	private void checkPermissionForResource(String ownerUuid, ResourceType resourceType, OperationType operation,
			String resourceUuid, String entityUuid, CrudAction action, Class<? extends IIdentifiable> resourceClass,
			Class<? extends IIdentifiable> entityClass) {
		if (StringUtils.isNotBlank(resourceUuid)) {
			if (!hasPermissionForResource(resourceType, operation, resourceUuid)) {
				if (StringUtils.isNotBlank(entityUuid)) {
					throw new PermissionDeniedException(entityClass, entityUuid);
				} else {
					throw new PermissionDeniedException(resourceClass, resourceUuid);
				}
			}
		} else {
			if (!hasPermissionForAnyContext(ownerUuid, resourceType, operation, null)) {
				throw new PermissionDeniedException(resourceClass, action);
			}
		}
	}

	public boolean checkReadForAnyContext(String ownerUuid, ResourceType resourceType, Class<? extends IIdentifiable> resourceClass) {
		return checkReadForAnyContext(ownerUuid, resourceType, resourceClass, null);
	}

	public boolean checkReadForAnyContext(String ownerUuid, ResourceType resourceType, Class<? extends IIdentifiable> resourceClass, Collection<String> contextUuids) {
		if (!canReadForAnyContext(ownerUuid, resourceType, contextUuids)) {
			throw new PermissionDeniedException(resourceClass, CrudAction.READ);
		}
		return true;
	}

	public boolean canWriteForAnyContext(String ownerUuid, ResourceType resourceType, Collection<String> contextUuids) {
		return canWriteForAnyContext(null, ownerUuid, resourceType, contextUuids);
	}

	public boolean canWriteForAnyContext(User user, String ownerUuid, ResourceType resourceType, Collection<String> contextUuids) {
		return hasPermissionForAnyContext(user, ownerUuid, resourceType, OperationType.Write, contextUuids);
	}

	public boolean canWriteForContext(String ownerUuid, ResourceType resourceType, String contextUuid) {
		return canWriteForContext(null, ownerUuid, resourceType, contextUuid);
	}

	public boolean canWriteForContext(User user, String ownerUuid, ResourceType resourceType, String contextUuid) {
		return hasPermissionForContext(user, ownerUuid, resourceType, OperationType.Write, contextUuid);
	}

	@NotNull
	private boolean hasPermissionForAnyContext(String ownerUuid, ResourceType resourceType, OperationType operationType) {
		return hasPermissionForAnyContext(null, ownerUuid, resourceType, operationType);
	}

	@NotNull
	private boolean hasPermissionForAnyContext(String ownerUuid, ResourceType resourceType, OperationType operationType, Collection<String> contextUuids) {
		return hasPermissionForAnyContext(null, ownerUuid, resourceType, operationType, contextUuids);
	}

	@NotNull
	private boolean hasPermissionForAnyContext(User user, String ownerUuid, ResourceType resourceType, OperationType operationType) {
		return hasPermissionForAnyContext(user, ownerUuid, resourceType, operationType, null);
	}

	@NotNull
	private boolean hasPermissionForContext(User user, String ownerUuid, ResourceType resourceType, OperationType operationType, String contextUuid) {
		return hasPermissionForAnyContext(user, ownerUuid, resourceType, operationType, StringUtils.isBlank(contextUuid) ? null : Collections.singletonList(contextUuid));
	}

	@NotNull
	private boolean hasPermissionForAnyContext(User user, String ownerUuid, ResourceType resourceType, OperationType operationType, Collection<String> contextUuids) {
		if (resourceType == null || operationType == null) {
			return false;
		}
		if (user == null) {
			user = permissionApi.getUser(null);
		}
		if (CollectionUtils.isEmpty(contextUuids)) {
			//TODO: if contextUuids is not empty, then we need to figure out another cache, including all these uuids
			//Currently, this method is not used with non empty contextUuids anywhere, so it shouldn't be a performance issue
			Optional<Boolean> optionalResult = SecurityCacheRegistry.checkAnyContext(user.getName(), resourceType, operationType);
			if (optionalResult.isPresent()) {
				return optionalResult.get();
			}
		}
		String queryStr = "SELECT DISTINCT ucpv.OPERATION_NAME FROM USERCONTEXTPERMISSIONVIEW ucpv "
				+ "WHERE ucpv.USER_UUID = ?userUuid "
				+ (StringUtils.isBlank(ownerUuid) ? "" : "AND (ucpv.OWNER_UUID IS NULL OR ucpv.OWNER_UUID = ?ownerUuid) ")
				+ (CollectionUtils.isEmpty(contextUuids) ? "" : "AND (ucpv.CONTEXT_UUID IS NULL OR ucpv.CONTEXT_UUID IN " + NativeQueryUtil.getInString(contextUuids) + ") ")
				+ " AND ucpv.RESOURCETYPE = ?resourceType";
		Query nativeQuery = entityBeanUtil.createNativeQuery(queryStr);
		nativeQuery.setParameter("userUuid", user.getUuid());
		nativeQuery.setParameter("resourceType", resourceType.toString());
		if (StringUtils.isNotBlank(ownerUuid)) {
			nativeQuery.setParameter("ownerUuid", ownerUuid);
		}

		@SuppressWarnings({ "unchecked" })
		List<String> results = EntityUtil.getListResultSavely(nativeQuery);

		boolean result = hasOperation(operationType, results);
		SecurityCacheRegistry.registerAnyContext(user.getName(), resourceType, operationType, result);
		return result;
	}

	@NotNull
	private boolean hasGlobalPermission(User user, ResourceType resourceType, OperationType operationType) {
		if (resourceType == null || operationType == null) {
			return false;
		}
		if (user == null) {
			user = permissionApi.getUser(null);
		}
		Optional<Boolean> optionalResult = SecurityCacheRegistry.checkAnyContext(user.getName(), resourceType, operationType);
		if (optionalResult.isPresent()) {
			return optionalResult.get();
		}
		String queryStr = "SELECT DISTINCT ucpv.OPERATION_NAME FROM USERCONTEXTPERMISSIONVIEW ucpv "
				+ "WHERE ucpv.USER_UUID = ?userUuid "
				+ "AND ucpv.CONTEXT_UUID IS NULL AND ucpv.RESOURCETYPE = ?resourceType";
		Query nativeQuery = entityBeanUtil.createNativeQuery(queryStr);
		nativeQuery.setParameter("userUuid", user.getUuid());
		nativeQuery.setParameter("resourceType", resourceType.toString());

		@SuppressWarnings({ "unchecked" })
		List<String> results = EntityUtil.getListResultSavely(nativeQuery);

		boolean result = hasOperation(operationType, results);
		SecurityCacheRegistry.registerAnyContext(user.getName(), resourceType, operationType, result);
		return result;
	}

	public boolean canReadForResource(ResourceType resourceType, String resourceUuid) {
		return canReadForResource(null, resourceType, resourceUuid);
	}

	public boolean canReadForResource(String userId, ResourceType resourceType, String resourceUuid) {
		return hasPermissionForResource(userId, resourceType, OperationType.Read, resourceUuid);
	}

	public void checkActionForNullableResource(String ownerUuid, ResourceType resourceType, String resourceUuid, Class<? extends IIdentifiable> resourceClass, CrudAction action) {
		if (StringUtils.isNotBlank(resourceUuid)) {
			checkActionForResource(resourceType, resourceUuid, resourceUuid, resourceClass, resourceClass, action);
		} else {
			if (action == CrudAction.READ) {
				return;
			}
			checkWriteForAnyContext(ownerUuid, resourceType, action, resourceClass);
		}
	}

	public void checkActionForResource(ResourceType resourceType, String resourceUuid, Class<? extends IIdentifiable> resourceClass, CrudAction action) {
		checkActionForResource(resourceType, resourceUuid, resourceUuid, resourceClass, resourceClass, action);
	}

	public void checkActionForResource(ResourceType resourceType, String resourceUuid, String entityUuid, Class<? extends IIdentifiable> resourceClass, Class<? extends IIdentifiable> entityClass,
			CrudAction action) {
		if (action == CrudAction.READ) {
			return;
		}
		checkWriteForResource(resourceType, resourceUuid, entityUuid, resourceClass, entityClass);
	}

	public void checkWriteForResource(ResourceType resourceType, String resourceUuid, Class<? extends IIdentifiable> resourceClass) {
		checkWriteForResource(resourceType, resourceUuid, resourceUuid, resourceClass, resourceClass);
	}

	public void checkWriteForResource(ResourceType resourceType, String resourceUuid, String entityUuid, Class<? extends IIdentifiable> resourceClass, Class<? extends IIdentifiable> entityClass) {
		if (!canWriteForResource(resourceType, resourceUuid)) {
			if (StringUtils.isNotBlank(entityUuid)) {
				throw new PermissionDeniedException(entityClass, entityUuid);
			} else {
				throw new PermissionDeniedException(resourceClass, resourceUuid);
			}
		}
	}

	public void checkWriteForAllResources(ResourceType resourceType, Class<? extends IIdentifiable> resourceClass, Collection<String> resourceUuids) {
		for (String resourceUuid : resourceUuids) {
			checkWriteForResource(resourceType, resourceUuid, resourceClass);
		}
	}

	public void checkWriteForAllResourceTypes(String resourceUuid, Class<? extends IIdentifiable> resourceClass, ResourceType... resourceTypes) {
		for (ResourceType resourceType : resourceTypes) {
			if (!canWriteForResource(resourceType, resourceUuid)) {
				throw new PermissionDeniedException(resourceClass, resourceUuid);
			}
		}
	}

	public void checkWriteForAnyResourceType(String ownerUuid, CrudAction action, Class<? extends IIdentifiable> resourceClass, ResourceType... resourceTypes) {
		for (ResourceType resourceType : resourceTypes) {
			try {
				checkWriteForAnyContext(ownerUuid, resourceType, action, resourceClass);
				return;
			} catch (PermissionDeniedException e) {
				//continue;
			}
		}
		throw new PermissionDeniedException(resourceClass, action);
	}

	public boolean canWriteForResource(ResourceType resourceType, String resourceUuid) {
		return canWriteForResource(null, resourceType, resourceUuid);
	}

	public boolean canWriteForResource(String userId, ResourceType resourceType, String resourceUuid) {
		return hasPermissionForResource(userId, resourceType, OperationType.Write, resourceUuid);
	}

	public boolean canWriteForAllResources(ResourceType resourceType, Collection<String> resourceUuids) {
		return canWriteForAllResources(null, resourceType, resourceUuids);
	}

	public boolean canWriteForAllResources(String userId, ResourceType resourceType, Collection<String> resourceUuids) {
		if (CollectionUtils.isEmpty(resourceUuids)) {
			return true;
		}
		boolean result = false;
		for (String resourceUuid : resourceUuids) {
			result = result && hasPermissionForResource(userId, resourceType, OperationType.Write, resourceUuid);
		}
		return result;
	}

	private boolean hasPermissionForResource(ResourceType resourceType, OperationType operationType, String resourceUuid) {
		User user = permissionApi.getUser(null);
		return hasPermissionForResource(user, resourceType, operationType, resourceUuid);
	}

	private boolean hasPermissionForResource(String userId, ResourceType resourceType, OperationType operationType, String resourceUuid) {
		User user = permissionApi.getUser(userId);
		Optional<Boolean> optionalResult = SecurityCacheRegistry.checkResource(userId, resourceType, operationType, resourceUuid);
		if (optionalResult.isPresent()) {
			return optionalResult.get();
		}
		boolean result = hasPermissionForResource(user, resourceType, operationType, resourceUuid);
		SecurityCacheRegistry.registerResource(userId, resourceType, operationType, resourceUuid, result);
		return result;
	}

	@NotNull
	private boolean hasPermissionForResource(User user, ResourceType resourceType, OperationType operationType, String resourceUuid) {
		if (resourceType == null || StringUtils.isBlank(resourceUuid)) {
			return false;
		}
		if (user == null) {
			user = permissionApi.getUser(null);
		}

		String queryStr = "SELECT DISTINCT urcpv.OPERATION_NAME FROM USERRESOURCECONTEXTPERMISSIONVIEW urcpv "
				+ "WHERE urcpv.USER_UUID = ?userUuid "
				+ "AND (urcpv.RESOURCE_UUID IS NULL OR urcpv.RESOURCE_UUID = ?resourceUuid) "
				+ "AND urcpv.RESOURCETYPE = ?resourceType";
		Query nativeQuery = entityBeanUtil.createNativeQuery(queryStr);
		nativeQuery.setParameter("userUuid", user.getUuid());
		nativeQuery.setParameter("resourceUuid", resourceUuid);
		nativeQuery.setParameter("resourceType", resourceType.toString());

		@SuppressWarnings({ "unchecked" })
		List<String> results = EntityUtil.getListResultSavely(nativeQuery);

		return hasOperation(operationType, results);
	}

	private boolean hasOperation(OperationType operation, List<String> results) {
		if (CollectionUtils.isEmpty(results)) {
			return false;
		}

		// return operation with the highest rank (if there are more than one)
		for (String operationString : results) {
			OperationType operationType = OperationType.valueOf(operationString);
			if (operationType == operation) {
				return true;
			}
		}

		return false;
	}

	private OperationType resolveOperation(List<OperationType> results) {
		if (CollectionUtils.isEmpty(results)) {
			return OperationType.None;
		}

		OperationType result = null;

		// return operation with the highest rank (if there are more than one)
		for (OperationType operationType : results) {
			if (result == null || result.getRank() < operationType.getRank()) {
				result = operationType;
			}
		}

		return result;
	}

	/**
	 * Loads all permissions for this user.
	 *
	 * @param user
	 *        User to load permissions for.
	 * @return Map with all authorization objects defined and the permissions
	 *         for the given user.
	 */
	@NotNull
	public Map<ResourceType, OperationType> loadUserPermissions(String userId) {
		User user = permissionApi.getUser(userId);
		// create result structure
		Map<ResourceType, OperationType> result = new HashMap<>();
		Map<ResourceType, List<OperationType>> resourceTypeMap = new HashMap<>();

		// get all permissions for this user
		String queryStr = "SELECT DISTINCT ucpv.operation_name, ucpv.resourceType FROM USERCONTEXTPERMISSIONVIEW ucpv "
				+ "WHERE ucpv.USER_UUID = ?userUuid ";
		Query nativeQuery = entityBeanUtil.createNativeQuery(queryStr);
		nativeQuery.setParameter("userUuid", user.getUuid());

		@SuppressWarnings({ "unchecked" })
		List<Object[]> resourceTypes = EntityUtil.getListResultSavely(nativeQuery);

		for (Object[] resourceType : resourceTypes) {
			String operationString = NativeQueryUtil.getStringValueSafely(resourceType[0]);
			String resourceTypeString = NativeQueryUtil.getStringValueSafely(resourceType[1]);
			if (StringUtils.isNotBlank(operationString) && StringUtils.isNotBlank(resourceTypeString)) {
				OperationType operationType = OperationType.valueOf(operationString);
				ResourceType resourceTypeEnum = ResourceType.valueOf(resourceTypeString);
				resourceTypeMap.computeIfAbsent(resourceTypeEnum, k -> new ArrayList<>()).add(operationType);
			}
		}

		// build map for all objects that have been found in the database
		for (Map.Entry<ResourceType, List<OperationType>> entry : resourceTypeMap.entrySet()) {
			result.put(entry.getKey(), resolveOperation(entry.getValue()));
		}

		// add all additional objects that have no permission object in the db
		// yet -> no access
		for (ResourceType resourceType : ResourceType.values()) {
			if (!result.containsKey(resourceType)) {
				result.put(resourceType, OperationType.None);
			}
		}

		return result;
	}

	/**
	 * Warning!
	 * 
	 * Only for internal purposes!
	 * Make sure that the user is authorized to perform this operation!
	 * Never expose this method to the outside APIs!
	 * 
	 * Warning!
	 */
	public void deleteResourceAssociations(String resourceUuid) {
		String queryStr = "DELETE FROM UserContextPermission ucp WHERE ucp.resource_uuid = :resourceUuid";
		TypedQuery<UserContextPermission> query = entityBeanUtil.createQuery(queryStr, UserContextPermission.class);
		query.setParameter("resourceUuid", resourceUuid);
		query.executeUpdate();

		String queryStr2 = "DELETE FROM UserContextRole ucr WHERE ucr.resource_uuid = :resourceUuid";
		TypedQuery<UserContextRole> query2 = entityBeanUtil.createQuery(queryStr2, UserContextRole.class);
		query2.setParameter("resourceUuid", resourceUuid);
		query2.executeUpdate();
	}

	/**
	 * Warning!
	 * 
	 * Only for internal purposes!
	 * Make sure that the user is authorized to perform this operation!
	 * Never expose this method to the outside APIs!
	 * 
	 * Warning!
	 */
	public void deleteContextAssociations(String contextUuid) {
		String queryStr = "DELETE FROM UserContextPermission ucp WHERE ucp.context_uuid = :contextUuid";
		TypedQuery<UserContextPermission> query = entityBeanUtil.createQuery(queryStr, UserContextPermission.class);
		query.setParameter("contextUuid", contextUuid);
		query.executeUpdate();

		String queryStr2 = "DELETE FROM UserContextRole ucr WHERE ucr.context_uuid = :contextUuid";
		TypedQuery<UserContextRole> query2 = entityBeanUtil.createQuery(queryStr2, UserContextRole.class);
		query2.setParameter("contextUuid", contextUuid);
		query2.executeUpdate();
	}

}
