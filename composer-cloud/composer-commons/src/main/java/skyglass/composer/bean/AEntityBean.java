package skyglass.composer.bean;

import javax.persistence.EntityExistsException;
import javax.persistence.TransactionRequiredException;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.domain.IDeletable;
import skyglass.composer.domain.IVersionable;
import skyglass.composer.entity.AEntity;
import skyglass.composer.exceptions.AlreadyExistsException;
import skyglass.composer.exceptions.NotAccessibleException;
import skyglass.composer.exceptions.NotNullableNorEmptyException;
import skyglass.composer.exceptions.PermissionDeniedException;

@Transactional
public abstract class AEntityBean<E extends AEntity> implements EntityRepository<E> {

	private static final Logger log = LoggerFactory.getLogger(AEntityBean.class);

	public static final int DEFAULT_PAGINATED_MAX_RESULTS = 100;

	public static final int DEFAULT_SEARCH_MAX_RESULTS = 20;

	@Autowired
	protected EntityBeanUtil entityBeanUtil;

	protected final Class<E> persistentClass;

	@SuppressWarnings("unchecked")
	public AEntityBean() {
		persistentClass = (Class<E>) GenericTypeResolver.resolveTypeArgument(getClass(), AEntityBean.class);
		if (persistentClass == null) {
			throw new IllegalStateException("persistentClass cannot be null");
		}
	}

	public void checkAccessibility(String uuid) throws NotAccessibleException {
		findByUuidSecure(uuid);
	}

	@NotNull
	@Override
	public E findByUuidSecure(String uuid) throws NotAccessibleException {
		if (StringUtils.isBlank(uuid)) {
			throw new NotNullableNorEmptyException(persistentClass.getSimpleName() + " UUID");
		}

		E result = findByUuid(uuid);
		if (result == null) {
			throw new PermissionDeniedException(persistentClass, uuid);
		}

		return result;
	}

	@NotNull
	public E findNullableByUuidSecure(String uuid) throws NotAccessibleException {
		if (StringUtils.isNotBlank(uuid)) {
			E result = findByUuid(uuid);
			if (result == null) {
				//check that entity actually exists in the database
				result = entityBeanUtil.find(persistentClass, uuid);
				if (result != null) {
					throw new NotAccessibleException(persistentClass, uuid);
				}
			}
			return result;
		}
		return null;
	}

	@Override
	public E findByUuid(String uuid) {
		if (uuid == null) {
			return null;
		}

		return entityBeanUtil.find(persistentClass, uuid);
	}

	@NotNull
	protected E preCreate(@NotNull E entity)
			throws IllegalArgumentException, IllegalStateException, AlreadyExistsException, NotAccessibleException {
		entity.setUuid(null);

		return entity;
	}

	@NotNull
	public E create(E entity)
			throws AlreadyExistsException, IllegalArgumentException, IllegalStateException, NotAccessibleException {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null");
		}

		E originalEntity = SerializationUtils.clone(entity);

		entity = preCreate(entity);
		return postCreate(originalEntity, persist(entity));
	}

	@NotNull
	protected E persist(@NotNull E entity)
			throws AlreadyExistsException, IllegalArgumentException, IllegalStateException {
		try {
			entityBeanUtil.persist(entity);
		} catch (TransactionRequiredException ex) {
			throw new IllegalStateException(ex);
		} catch (EntityExistsException ex) {
			throw new AlreadyExistsException(entity, ex);
		}

		return entity;
	}

	@NotNull
	protected E postCreate(@NotNull E entity, @NotNull E createdEntity)
			throws IllegalArgumentException, IllegalStateException, NotAccessibleException {
		return createdEntity;
	}

	protected E preUpdate(@NotNull E entity)
			throws IllegalArgumentException, IllegalStateException, NotAccessibleException {
		return entity;
	}

	@NotNull
	public E update(E entity) throws IllegalArgumentException, IllegalStateException, NotAccessibleException {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null");
		}

		String uuid = entity.getUuid();
		if (StringUtils.isBlank(uuid)) {
			throw new IllegalStateException("Entity without UUID cannot be updated");
		}

		entity = preUpdate(entity);
		return postUpdate(entity, merge(entity));
	}

	protected E merge(@NotNull E entity) throws IllegalArgumentException, IllegalStateException {
		try {
			if (entity instanceof IVersionable) {
				log.warn("Updating versioned entity '" + entity
						+ "', that makes usually no sense, please check implementation!");
			}

			return entityBeanUtil.merge(entity);
		} catch (TransactionRequiredException ex) {
			throw new IllegalStateException(ex);
		}
	}

	@NotNull
	protected E postUpdate(@NotNull E entity, E updatedEntity)
			throws IllegalArgumentException, IllegalStateException, NotAccessibleException {
		return updatedEntity;
	}

	@NotNull
	protected E preDeleteEntity(@NotNull E entity)
			throws IllegalArgumentException, IllegalStateException, NotAccessibleException {
		return entity;
	}

	@NotNull
	protected E preDeleteUuid(@NotNull String uuid)
			throws IllegalArgumentException, IllegalStateException, NotAccessibleException {
		E entity = findByUuid(uuid);
		if (entity == null) {
			throw new IllegalArgumentException("Could not delete " + persistentClass.getSimpleName() + " with UUID '"
					+ uuid + "', entity not accessible with that UUID");
		}

		return entity;
	}

	@NotNull
	protected E preDelete(@NotNull E entity)
			throws IllegalArgumentException, IllegalStateException, NotAccessibleException {
		return entity;
	}

	@NotNull
	public E delete(String uuid) throws IllegalArgumentException, IllegalStateException, NotAccessibleException {
		if (StringUtils.isBlank(uuid)) {
			throw new IllegalArgumentException("UUID cannot neither be null nor empty");
		}

		return postDelete(remove(preDelete(preDeleteUuid(uuid))));
	}

	public void delete(E entity) throws IllegalArgumentException, IllegalStateException, NotAccessibleException {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null");
		}

		postDelete(remove(preDelete(preDeleteEntity(entity))));
	}

	protected E remove(@NotNull E entity) throws IllegalArgumentException, IllegalStateException {
		try {
			if (entity instanceof IDeletable) {
				((IDeletable) entity).setDeleted(true);
				entity = merge(entity);
			} else {
				entityBeanUtil.remove(entity);
			}
		} catch (TransactionRequiredException ex) {
			throw new IllegalStateException(ex);
		}

		return entity;
	}

	@NotNull
	protected E postDelete(@NotNull E entity)
			throws IllegalArgumentException, IllegalStateException, NotAccessibleException {
		return entity;
	}

	protected void detachEntity(@NotNull E entity) {
		entityBeanUtil.detach(entity);
	}

}
