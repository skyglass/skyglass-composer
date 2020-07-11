package skyglass.composer.business.service;

import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import skyglass.composer.bean.ASecuredEntityBean;
import skyglass.composer.business.domain.BusinessOwner;
import skyglass.composer.entity.EntityUtil;
import skyglass.composer.exceptions.BusinessRuleValidationException;
import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.ResourceType;
import skyglass.composer.security.domain.UserContextPermission;
import skyglass.composer.security.domain.UserContextRole;

@Repository
public class BusinessOwnerRepository extends ASecuredEntityBean<BusinessOwner> {

	@Override
	protected ResourceType getResourceType() {
		return ResourceType.BusinessOwner;
	}

	@NotNull
	public BusinessOwner createBusinessOwner(String name) {
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("Business Owner cannot be null or empty.");
		}

		BusinessOwner dbBusinessOwner = findUnsecuredByName(name);
		if (dbBusinessOwner != null) {
			throw new BusinessRuleValidationException(
					String.format("Business Owner with such name (%s) already exists", name));
		}
		BusinessOwner businessOwner = createEntity(name);

		checkSecurity(businessOwner, OperationType.Create);

		entityBeanUtil.persist(businessOwner);
		return businessOwner;
	}

	public BusinessOwner changeBusinessOwnerName(String businessOwnerUuid, String name) {
		if (StringUtils.isBlank(businessOwnerUuid)) {
			throw new IllegalArgumentException("BusinessOwner UUID cannot be null");
		}

		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("BusinessOwner New Name cannot be null");
		}

		BusinessOwner dbBusinessOwner = findByUuidSecure(businessOwnerUuid);

		if (StringUtils.isBlank(dbBusinessOwner.getName())) {
			throw new IllegalArgumentException("BusinessOwner Old Name cannot be null");
		}

		BusinessOwner test = findUnsecuredByName(name);
		if (test != null) {
			throw new BusinessRuleValidationException(
					String.format("Business Owner with such name (%s) already exists", name));
		}

		checkSecurity(dbBusinessOwner, OperationType.Update);
		dbBusinessOwner.setName(name);
		entityBeanUtil.merge(dbBusinessOwner);

		return dbBusinessOwner;
	}

	public void deleteBusinessOwner(String businessOwnerUuid) {
		if (StringUtils.isBlank(businessOwnerUuid)) {
			throw new IllegalArgumentException("BusinessOwner UUID cannot be null");
		}

		BusinessOwner dbBusinessOwner = findByUuidSecure(businessOwnerUuid);

		checkSecurity(dbBusinessOwner, OperationType.Delete);

		securityCheckBean.deleteResourceAssociations(businessOwnerUuid);
		checkAnyOwnedResourceExists(businessOwnerUuid);
		deleteOwnerAssociations(businessOwnerUuid);
		entityBeanUtil.remove(dbBusinessOwner);
	}

	private void deleteOwnerAssociations(String ownerUuid) {
		String queryStr = "DELETE FROM UserContextPermission ucp WHERE ucp.owner_uuid = :ownerUuid";
		TypedQuery<UserContextPermission> query = entityBeanUtil.createQuery(queryStr, UserContextPermission.class);
		query.setParameter("ownerUuid", ownerUuid);
		query.executeUpdate();

		String queryStr2 = "DELETE FROM UserContextRole ucr WHERE ucr.owner_uuid = :ownerUuid";
		TypedQuery<UserContextRole> query2 = entityBeanUtil.createQuery(queryStr2, UserContextRole.class);
		query2.setParameter("ownerUuid", ownerUuid);
		query2.executeUpdate();
	}

	private void checkAnyOwnedResourceExists(String businessOwnerUuid) {
		String queryStr = "SELECT COUNT(u) FROM User u"
				+ " WHERE u.owner.uuid = :businessOwnerUuid";
		TypedQuery<Long> query = entityBeanUtil.createQuery(queryStr, Long.class);
		query.setParameter("businessOwnerUuid", businessOwnerUuid);
		Long result = EntityUtil.getSingleResultSafely(query);
		if (result == null) {
			result = 0L;
		}
		if (result > 0) {
			throw new BusinessRuleValidationException(String.format("Can't delete business owner (uuid = %s): there are "
					+ "%s users associated with it", businessOwnerUuid, result.toString()));
		}
	}

	private BusinessOwner createEntity(String name) {
		BusinessOwner businessOwner = new BusinessOwner();
		businessOwner.setName(name);
		return businessOwner;
	}

	private BusinessOwner findUnsecuredByName(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}

		String queryStr = "SELECT bo FROM BusinessOwner bo WHERE LOWER(bo.name) = LOWER(:name)";
		TypedQuery<BusinessOwner> typedQuery = entityBeanUtil.createQuery(queryStr, BusinessOwner.class)
				.setParameter("name", name);

		return EntityUtil.getSingleResultSafely(typedQuery);
	}

	public BusinessOwner findByName(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}

		String queryStr = String.format("SELECT bo FROM BusinessOwner bo %s WHERE LOWER(bo.name) = LOWER(:name)", jpa("bo"));
		TypedQuery<BusinessOwner> typedQuery = entityBeanUtil.createQuery(queryStr, BusinessOwner.class)
				.setParameter("name", name)
				.setParameter("userUuid", permissionApi.getUserFromCtx().getUuid());

		return EntityUtil.getSingleResultSafely(typedQuery);
	}

	private void checkSecurity(BusinessOwner entity, OperationType operation) {
		permissionApi.checkAdmin();
		securityCheckBean.checkPermissionForResource(entity.getUuid(), ResourceType.BusinessOwner, operation, entity.getUuid(), persistentClass);
	}

}
