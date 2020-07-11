package skyglass.composer.security.repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.bean.AEntityBean;
import skyglass.composer.entity.EntityUtil;
import skyglass.composer.exceptions.NotNullableNorEmptyException;
import skyglass.composer.security.domain.ContextType;
import skyglass.composer.security.domain.GlobalRole;
import skyglass.composer.security.domain.RelationType;
import skyglass.composer.security.domain.Role;
import skyglass.composer.security.domain.User;
import skyglass.composer.security.domain.UserContextRole;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class UserContextRoleBean extends AEntityBean<UserContextRole> {

	@Autowired
	private PermissionBean permissionBean;

	/**
	 * Retrieve userObectPermission by user, permission and action.
	 *
	 * @param user
	 * @param contextUuid
	 * @return UserContextRole collection
	 */
	public Collection<UserContextRole> getUserContextRoles(User user, String contextUuid,
			String resourceUuid, RelationType relation, String ownerUuid) {
		if (user == null) {
			return Collections.emptyList();
		}
		TypedQuery<UserContextRole> query = entityBeanUtil.createQuery(
				"SELECT ucr FROM UserContextRole ucr WHERE ucr.user = :user "
						+ (StringUtils.isBlank(contextUuid) ? "AND ucr.context_uuid IS NULL " : "AND ucr.context_uuid = :contextUuid ")
						+ (StringUtils.isBlank(resourceUuid) ? "AND ucr.resource_uuid IS NULL " : "AND ucr.resource_uuid = :resourceUuid ")
						+ ((relation == null) ? "AND ucr.relation IS NULL " : "AND ucr.relation = :relation ")
						+ (StringUtils.isBlank(ownerUuid) ? "AND ucr.owner_uuid IS NULL " : "AND ucr.owner_uuid = :ownerUuid "),
				UserContextRole.class);
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

	public Collection<UserContextRole> getUserContextRoles(String contextUuid, String resourceUuid,
			RelationType relation, String ownerUuid) {
		User user = permissionBean.getUserFromCtx();
		return getUserContextRoles(user, contextUuid, resourceUuid, relation, ownerUuid);
	}

	public Collection<Role> getRoles(User user, String contextUuid) {
		return getRoles(user, contextUuid, null, null, null);
	}

	public Collection<Role> getRoles(User user, String contextUuid, String resourceUuid,
			RelationType relation, String ownerUuid) {
		return getUserContextRoles(user, contextUuid, resourceUuid, relation, ownerUuid)
				.stream().map(uor -> uor.getRole()).collect(Collectors.toSet());
	}

	public void updateRoles(User user, String contextUuid, ContextType contextType, List<Role> rolesToBeUpdated) {
		updateRoles(user, contextUuid, null, contextType, null, null, rolesToBeUpdated);
	}

	/**
	 * Update Roles for the given Resource Context and the given User
	 *
	 * @param user
	 * @param rolesToBeUpdated
	 * @return ResourceType
	 */
	public void updateRoles(User user, String contextUuid, String resourceUuid, ContextType contextType,
			RelationType relation, String ownerUuid, List<Role> rolesToBeUpdated) {
		validateParams(user);
		if (contextType == null) {
			throw new NotNullableNorEmptyException("Context Type");
		}
		if (rolesToBeUpdated == null || rolesToBeUpdated.isEmpty()) {
			return;
		}

		TypedQuery<UserContextRole> query = entityBeanUtil.createQuery(
				"DELETE FROM UserContextRole ucr WHERE ucr.user = :user "
						+ (StringUtils.isBlank(contextUuid) ? "AND ucr.context_uuid IS NULL " : "AND ucr.context_uuid = :contextUuid ")
						+ (StringUtils.isBlank(resourceUuid) ? "AND ucr.resource_uuid IS NULL " : "AND ucr.resource_uuid = :resourceUuid ")
						+ ((relation == null) ? "AND ucr.relation IS NULL " : "AND ucr.relation = :relation ")
						+ (StringUtils.isBlank(ownerUuid) ? "AND ucr.owner_uuid IS NULL " : "AND ucr.owner_uuid = :ownerUuid "),
				UserContextRole.class);
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
		query.executeUpdate();

		createUserContextRoles(user, contextUuid, resourceUuid, contextType, relation, ownerUuid, rolesToBeUpdated);
	}

	private void createUserContextRoles(User user, String contextUuid,
			String resourceUuid, ContextType contextType, RelationType relation, String ownerUuid, List<Role> roles) {
		for (Role role : roles) {
			validateParams(user, role);
			createEntity(user, contextUuid, resourceUuid, contextType, relation, ownerUuid, role);
		}
	}

	public void assignRoleToUser(User user, String ownerUuid, Role role) {
		assignRoleToUserContext(user, null, null, null, null, ownerUuid, role);
	}

	public void assignRoleToUserContext(User user, String contextUuid, ContextType contextType, Role role) {
		assignRoleToUserContext(user, contextUuid, null, contextType, null, null, role);
	}

	public void assignRoleToUserContext(User user, String contextUuid, String resourceUuid, ContextType contextType,
			RelationType relation, String ownerUuid, Role role) {
		validateParams(user, role);
		UserContextRole userContextRole = getUserContextRole(user, contextUuid, resourceUuid, relation, ownerUuid, role);
		if (userContextRole == null) {
			createEntity(user, contextUuid, resourceUuid, contextType, relation, ownerUuid, role);
		}
	}

	public void removeRoleFromUser(User user, Role role) {
		removeRoleFromUserContext(user, null, null, null, null, role);
	}

	public void removeRoleFromUserContext(User user, String contextUuid, Role role) {
		removeRoleFromUserContext(user, contextUuid, null, null, null, role);
	}

	public void removeRoleFromUserOwner(User user, String ownerUuid, Role role) {
		removeRoleFromUserContext(user, null, null, null, ownerUuid, role);
	}

	public void removeRoleFromUserContext(User user, String contextUuid, String resourceUuid,
			RelationType relation, String ownerUuid, Role role) {
		validateParams(user, role);
		UserContextRole userContextRole = getUserContextRole(user, contextUuid, resourceUuid, relation, ownerUuid, role);
		if (userContextRole != null) {
			deleteUserContextRole(userContextRole);
		}
	}

	private void deleteUserContextRole(UserContextRole userContextRole) {
		entityBeanUtil.remove(userContextRole);
	}

	/**
	 * Checks if current user has one of the given roles for the given contextUuid.
	 * 
	 * @param contextUuid
	 * @param userRoles
	 *        {@link GlobalRole}
	 *
	 * @return {@code true} if user contains at least one role from the list or
	 *         if the list is empty (null) {@code false} otherwise.
	 */
	public boolean hasCurrentUserRole(String contextUuid, String resourceUuid,
			RelationType relation, String ownerUuid, Role... roles) {
		if (roles == null || roles.length == 0) {
			return true;
		}

		User user = permissionBean.getUserFromCtx();
		if (user == null) {
			return false;
		}

		Collection<UserContextRole> userContextRoles = getUserContextRoles(user, contextUuid, resourceUuid, relation, ownerUuid);
		if (CollectionUtils.isEmpty(userContextRoles)) {
			return false;
		}

		return CollectionUtils.containsAny(userContextRoles.stream().map(uor -> uor.getRole()).collect(Collectors.toList()), roles);
	}

	public UserContextRole getUserContextRole(User user, String contextUuid, Role role) {
		return getUserContextRole(user, contextUuid, null, null, null, role);
	}

	public UserContextRole getUserContextRole(User user, String contextUuid,
			String resourceUuid, RelationType relation, String ownerUuid, Role role) {
		if (user == null || StringUtils.isBlank(user.getUuid()) || role == null) {
			return null;
		}
		TypedQuery<UserContextRole> query = entityBeanUtil.createQuery(
				"SELECT ucr FROM UserContextRole ucr WHERE ucr.user = :user "
						+ (StringUtils.isBlank(contextUuid) ? "AND ucr.context_uuid IS NULL " : "AND ucr.context_uuid = :contextUuid ")
						+ (StringUtils.isBlank(resourceUuid) ? "AND ucr.resource_uuid IS NULL " : "AND ucr.resource_uuid = :resourceUuid ")
						+ ((relation == null) ? "AND ucr.relation IS NULL " : "AND ucr.relation = :relation ")
						+ (StringUtils.isBlank(ownerUuid) ? "AND ucr.owner_uuid IS NULL " : "AND ucr.owner_uuid = :ownerUuid ")
						+ "AND ucr.role = :role",
				UserContextRole.class);
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
		query.setParameter("role", role);

		return EntityUtil.getSingleResultSafely(query);
	}

	private UserContextRole createEntity(User user, String contextUuid, String resourceUuid,
			ContextType contextType, RelationType relation, String ownerUuid, Role role) {
		UserContextRole userContextRole = new UserContextRole();
		userContextRole.setUser(user);
		userContextRole.setContext_uuid(contextUuid);
		userContextRole.setResource_uuid(resourceUuid);
		userContextRole.setContextType(contextType);
		userContextRole.setRelation(relation);
		userContextRole.setOwner_uuid(ownerUuid);
		userContextRole.setRole(role);
		create(userContextRole);
		return userContextRole;
	}

	private void validateParams(User user, Role role) {
		validateParams(user);
		if (role == null) {
			throw new NotNullableNorEmptyException("Role");
		}
		if (StringUtils.isBlank(role.getUuid())) {
			throw new NotNullableNorEmptyException("Role UUID");
		}
	}

	private void validateParams(User user) {
		if (user == null) {
			throw new NotNullableNorEmptyException("User");
		}
		if (StringUtils.isBlank(user.getUuid())) {
			throw new NotNullableNorEmptyException("User UUID");
		}
	}

}
