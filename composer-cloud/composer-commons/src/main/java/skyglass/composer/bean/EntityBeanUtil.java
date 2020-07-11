/**
 *
 */
package skyglass.composer.bean;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class EntityBeanUtil {
	@PersistenceContext(unitName = "platform")
	private EntityManager entityManager;

	public Query createNativeQuery(String sqlString) {
		return entityManager.createNativeQuery(sqlString);
	}

	public Query createNativeQuery(String sqlString, Class<?> resultClass) {
		return entityManager.createNativeQuery(sqlString, resultClass);
	}

	public Query createQuery(String qlString) {
		return entityManager.createQuery(qlString);
	}

	public <E> TypedQuery<E> createQuery(CriteriaQuery<E> criteriaQuery) throws IllegalArgumentException {
		return entityManager.createQuery(criteriaQuery);
	}

	public <E> TypedQuery<E> createQuery(String qlString, Class<E> resultClass) throws IllegalArgumentException {
		return entityManager.createQuery(qlString, resultClass);
	}

	public <E> void detach(E entity) throws IllegalArgumentException {
		entityManager.detach(entity);
	}

	public <E> boolean exists(Class<E> type, String... uuids) {
		if (uuids == null || uuids.length == 0) {
			return false;
		}

		List<String> ids = new ArrayList<>();
		for (String uuid : uuids) {
			if (!StringUtils.isBlank(uuid)) {
				ids.add(uuid);
			}
		}

		if (ids.isEmpty()) {
			return false;
		}

		CriteriaBuilder criteriaBuilder = getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

		Root<E> from = criteriaQuery.from(type);

		criteriaQuery = criteriaQuery.select(criteriaBuilder.count(from)).where(from.get("uuid").in(ids));

		TypedQuery<Long> query = createQuery(criteriaQuery);
		return query.getSingleResult() == ids.size();
	}

	public <E> E find(Class<E> type, String uuid) {
		return uuid == null ? null : entityManager.find(type, uuid);
	}

	public CriteriaBuilder getCriteriaBuilder() throws IllegalStateException {
		return entityManager.getCriteriaBuilder();
	}

	public <E> E merge(E entity) throws IllegalArgumentException, TransactionRequiredException {
		return entityManager.merge(entity);
	}

	public <E> E persist(E entity)
			throws EntityExistsException, IllegalArgumentException, TransactionRequiredException {
		entityManager.persist(entity);

		return entity;
	}

	public <E> void remove(E entity) throws IllegalArgumentException, TransactionRequiredException {
		entityManager.remove(entity);
	}

	public void flush() {
		entityManager.flush();
	}

}
