package skyglass.composer.security.repository;

import java.util.Collections;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.bean.AEntityBean;
import skyglass.composer.domain.CrudAction;
import skyglass.composer.entity.EntityUtil;
import skyglass.composer.exceptions.BusinessRuleValidationException;
import skyglass.composer.exceptions.NotAccessibleException;
import skyglass.composer.exceptions.NotNullableNorEmptyException;
import skyglass.composer.security.domain.ResourceType;
import skyglass.composer.security.domain.Role;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class RoleBean extends AEntityBean<Role> {

	@Autowired
	private SecurityCheckBean securityCheckBean;

	public List<Role> findByUuids(List<String> uuids) {
		if (CollectionUtils.isEmpty(uuids)) {
			return Collections.emptyList();
		}
		TypedQuery<Role> query = entityBeanUtil.createQuery(
				"SELECT r FROM Role r WHERE r.uuid IN :uuids",
				Role.class);
		query.setParameter("uuids", uuids);
		List<Role> roles = EntityUtil.getListResultSafely(query);
		if (CollectionUtils.isEmpty(roles)) {
			throw new NotAccessibleException(Role.class, uuids.get(0));
		}

		return roles;
	}

	public List<Role> findAll() {
		TypedQuery<Role> query = entityBeanUtil.createQuery(
				"SELECT r FROM Role r", Role.class);
		List<Role> roles = EntityUtil.getListResultSafely(query);
		return roles;
	}

	public Role findByName(String name) {
		if (StringUtils.isBlank(name)) {
			throw new NotNullableNorEmptyException("Role Name");
		}

		TypedQuery<Role> query = entityBeanUtil.createQuery(
				"SELECT r FROM Role r WHERE r.name = :name",
				Role.class);
		query.setParameter("name", name);

		Role role = EntityUtil.getSingleResultSafely(query);
		if (role == null) {
			throw new NotAccessibleException(Role.class, name);
		}

		return role;
	}

	@NotNull
	@Override
	protected Role preCreate(@NotNull Role entity)
			throws IllegalArgumentException, IllegalStateException, NotAccessibleException {
		checkSecurity(CrudAction.CREATE);
		return super.preCreate(entity);
	}

	@NotNull
	@Override
	protected Role preUpdate(@NotNull Role entity)
			throws IllegalArgumentException, IllegalStateException, NotAccessibleException {
		checkSecurity(CrudAction.UPDATE);
		return super.preUpdate(entity);
	}

	@NotNull
	@Override
	protected Role preDelete(@NotNull Role entity)
			throws IllegalArgumentException, IllegalStateException, NotAccessibleException {
		checkSecurity(CrudAction.DELETE);
		if (getUserContextRoleCount(entity.getUuid()) > 0) {
			throw new BusinessRuleValidationException("Role cannot be deleted, because it is already asigned to some user. Please, unassign all users from this role and try again");
		}
		if (getChildrenCount(entity.getUuid()) > 0) {
			throw new BusinessRuleValidationException("Role cannot be deleted, because it has children. Please, delete all its children and try again");
		}
		deleteRolePermissions(entity.getUuid());
		return super.preDelete(entity);
	}

	private void deleteRolePermissions(String roleUuid) {
		String queryStr = "DELETE FROM RolePermission rp WHERE rp.ROLE_UUID = ?roleUuid";
		Query query = entityBeanUtil.createNativeQuery(queryStr);
		query.setParameter("roleUuid", roleUuid);
		query.executeUpdate();
	}

	private long getUserContextRoleCount(String roleUuid) {
		String queryStr = "SELECT COUNT(ucr) FROM UserContextRole ucr"
				+ " WHERE ucr.role.uuid = :roleUuid";
		TypedQuery<Long> query = entityBeanUtil.createQuery(queryStr, Long.class);
		query.setParameter("roleUuid", roleUuid);
		Long result = EntityUtil.getSingleResultSafely(query);
		if (result == null) {
			return 0L;
		}
		return result;
	}

	private long getChildrenCount(String roleUuid) {
		String queryStr = "SELECT COUNT(r) FROM Role r"
				+ " WHERE r.parent.uuid = :roleUuid";
		TypedQuery<Long> query = entityBeanUtil.createQuery(queryStr, Long.class);
		query.setParameter("roleUuid", roleUuid);
		Long result = EntityUtil.getSingleResultSafely(query);
		if (result == null) {
			return 0L;
		}
		return result;
	}

	public List<Role> getAllowedParents(Role role) {
		String queryStr = "SELECT DISTINCT r FROM Role r"
				+ " LEFT JOIN Role_HierarchyView rhv ON r.uuid = rhv.child_uuid AND rhv.parent_uuid = :roleUuid WHERE rhv.child_uuid IS NULL";
		TypedQuery<Role> query = entityBeanUtil.createQuery(queryStr, Role.class);
		query.setParameter("roleUuid", role.getUuid());

		return EntityUtil.getListResultSafely(query);
	}

	public boolean isParentAllowed(String childUuid, String parentUuid) {
		if (parentUuid.equals(childUuid)) {
			return false;
		}
		String queryStr = "SELECT rhv.child_uuid FROM Role_HierarchyView rhv ON rhv.parent_uuid = :childUuid AND rhv.child_uuid = :parentUuid";
		Query query = entityBeanUtil.createNativeQuery(queryStr);
		query.setParameter("childUuid", childUuid);
		query.setParameter("parentUuid", parentUuid);
		query.setMaxResults(1);
		@SuppressWarnings("unchecked")
		List<Object[]> results = EntityUtil.getListResultSavely(query);
		return results.size() == 0;
	}

	public void checkNameDuplicate(Role role) {
		TypedQuery<Role> query = entityBeanUtil.createQuery(
				"SELECT r FROM Role r WHERE r.name = :name AND r.uuid != :roleUuid", Role.class);
		query.setParameter("roleUuid", role.getUuid());
		query.setParameter("name", role.getName());
		List<Role> results = EntityUtil.getListResultSafely(query);
		if (CollectionUtils.isNotEmpty(results)) {
			throw new BusinessRuleValidationException(String.format("Global role '%s' cannot be created. There is already a role with the same name",
					role.getName()));
		}
	}

	private void checkSecurity(CrudAction action) {
		securityCheckBean.checkWriteForAnyContext(null, ResourceType.Role, action, persistentClass);
	}

}
