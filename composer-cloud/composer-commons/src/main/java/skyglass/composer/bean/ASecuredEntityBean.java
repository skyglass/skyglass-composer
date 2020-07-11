package skyglass.composer.bean;

import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.entity.AEntity;
import skyglass.composer.entity.EntityUtil;
import skyglass.composer.security.api.PermissionApi;
import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.ResourceType;
import skyglass.composer.security.repository.ResourceOwnerRepository;
import skyglass.composer.security.repository.SecurityCheckBean;
import skyglass.composer.security.service.SecurityCheckHelper;

public abstract class ASecuredEntityBean<E extends AEntity> extends AEntityBean<E> {

	@Autowired
	protected PermissionApi permissionApi;

	@Autowired
	protected ResourceOwnerRepository resourceOwnerRepository;

	@Autowired
	protected SecurityCheckBean securityCheckBean;

	protected abstract ResourceType getResourceType();

	protected String jpa(String alias) {
		return SecurityCheckHelper.jpa(alias, getResourceType());
	}

	protected String jpa(String alias, OperationType operationType) {
		return SecurityCheckHelper.jpa(alias, getResourceType(), operationType);
	}

	protected String nativ(String alias) {
		return SecurityCheckHelper.nativ(alias, getResourceType());
	}

	protected String nativ(String alias, OperationType operationType) {
		return SecurityCheckHelper.nativ(alias, getResourceType(), operationType);
	}

	@Override
	public E findByUuid(String uuid) {
		if (StringUtils.isBlank(uuid)) {
			return null;
		}

		String queryStr = String.format("SELECT e FROM %s e %s WHERE e.uuid = :uuid",
				persistentClass.getSimpleName(), jpa("e"));
		TypedQuery<E> typedQuery = entityBeanUtil.createQuery(queryStr, persistentClass)
				.setParameter("uuid", uuid)
				.setParameter("userUuid", permissionApi.getUserFromCtx().getUuid());

		return EntityUtil.getSingleResultSafely(typedQuery);
	}

	@Override
	public E create(E entity) {
		throw new UnsupportedOperationException("The method is not supported");
	}

	@Override
	public E update(E entity) {
		throw new UnsupportedOperationException("The method is not supported");
	}

	@Override
	public E delete(String uuid) {
		throw new UnsupportedOperationException("The method is not supported");
	}

	@Override
	public void delete(E entity) {
		throw new UnsupportedOperationException("The method is not supported");
	}

}
