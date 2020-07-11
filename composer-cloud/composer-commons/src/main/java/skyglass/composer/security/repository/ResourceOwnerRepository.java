package skyglass.composer.security.repository;

import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.bean.AEntityBean;
import skyglass.composer.bean.EntityBeanUtil;
import skyglass.composer.entity.EntityUtil;
import skyglass.composer.exceptions.NotNullableNorEmptyException;
import skyglass.composer.security.domain.ResourceOwnerView;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class ResourceOwnerRepository extends AEntityBean<ResourceOwnerView> {

	@Autowired
	protected EntityBeanUtil entityBeanUtil;

	@Override
	public ResourceOwnerView findByUuid(String uuid) {
		if (StringUtils.isBlank(uuid)) {
			throw new NotNullableNorEmptyException("Resource Owner UUID");
		}

		checkReadSecurity();

		TypedQuery<ResourceOwnerView> query = entityBeanUtil.createQuery(
				"SELECT r FROM ResourceOwnerView r WHERE r.uuid = :uuid",
				ResourceOwnerView.class);
		query.setParameter("uuid", uuid);
		return EntityUtil.getSingleResultSafely(query);
	}

	public ResourceOwnerView findByName(String name) {
		if (StringUtils.isBlank(name)) {
			throw new NotNullableNorEmptyException("Resource Owner Name");
		}

		checkReadSecurity();

		TypedQuery<ResourceOwnerView> query = entityBeanUtil.createQuery(
				"SELECT r FROM ResourceOwnerView r WHERE r.name = :name",
				ResourceOwnerView.class);
		query.setParameter("name", name);
		return EntityUtil.getSingleResultSafely(query);
	}

	private void checkReadSecurity() {
		//securityCheckBean.checkReadForAnyContext(ResourceType.ResourceOwner, persistentClass);
	}

}
