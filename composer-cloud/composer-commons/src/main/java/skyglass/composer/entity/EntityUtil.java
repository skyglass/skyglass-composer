package skyglass.composer.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import skyglass.composer.bean.AEntityBean;
import skyglass.composer.domain.IIdentifiable;
import skyglass.composer.dto.AEntityDTOFactory;
import skyglass.composer.exceptions.NotUniqueException;

public class EntityUtil {

	public static <T extends IIdentifiable> Map<String, T> asMap(Collection<? extends T> entities) {
		if (entities == null) {
			return new HashMap<>();
		}

		return entities.stream().collect(Collectors.toMap(entity -> entity.getUuid(), entity -> entity));
	}

	public static String getUuid(IIdentifiable entity) {
		return AEntityDTOFactory.provideUuidFromReference(entity);
	}

	public static <K extends IIdentifiable, V extends IIdentifiable> List<String> getMapperUuidList(Collection<K> entities, Function<K, V> mapper) {
		return getMapperUuidList(entities, x -> true, mapper);
	}

	public static <K extends IIdentifiable, V extends IIdentifiable> List<String> getMapperUuidList(Collection<K> entities, Predicate<? super K> filter, Function<K, V> mapper) {
		return EntityUtil.getUuidList(entities.stream().filter(filter).map(e -> mapper.apply(e)).collect(Collectors.toSet()));
	}

	public static List<String> getUuidList(Collection<? extends IIdentifiable> entities) {
		return EntityUtil.getUuidList(entities, null);
	}

	public static <T extends IIdentifiable> List<String> getUuidList(Collection<T> entities, Predicate<T> filter) {
		return AEntityDTOFactory.provideUuidsFromReferences(entities, filter);
	}

	public static Object getSingleResultSafely(Query query)
			throws IllegalStateException, UnsupportedOperationException {
		if (query != null) {
			@SuppressWarnings("rawtypes")
			List results = getListResultSavely(query);
			if (!results.isEmpty()) {
				return results.iterator().next();
			}
		}

		return null;
	}

	public static <R extends Object> R getSingleResultSafely(TypedQuery<R> typedQuery)
			throws IllegalStateException, UnsupportedOperationException {
		if (typedQuery != null) {
			List<R> results = getListResultSafely(typedQuery);
			if (!results.isEmpty()) {
				return results.iterator().next();
			}
		}

		return null;
	}

	public static Object getUniqueSingleResultSavely(Query query)
			throws IllegalArgumentException, IllegalStateException, UnsupportedOperationException, NotUniqueException {
		if (query != null) {
			try {
				return query.getSingleResult();
			} catch (NoResultException ex) {
				return null;
			} catch (NonUniqueResultException ex) {
				throw new NotUniqueException(ex);
			} catch (IllegalStateException ex) {
				throw new IllegalArgumentException(ex);
			} catch (TransactionRequiredException ex) {
				throw new IllegalStateException(ex);
			} catch (PersistenceException ex) {
				throw new UnsupportedOperationException(ex);
			} catch (Exception ex) {
				throw new RuntimeException("Could not get results from query '" + query, ex);
			}
		}

		return null;
	}

	public static <R extends Object> R getUniqueSingleResultSavely(TypedQuery<R> typedQuery)
			throws IllegalArgumentException, IllegalStateException, UnsupportedOperationException, NotUniqueException {
		if (typedQuery != null) {
			try {
				return typedQuery.getSingleResult();
			} catch (NoResultException ex) {
				return null;
			} catch (NonUniqueResultException ex) {
				throw new NotUniqueException(ex);
			} catch (IllegalStateException ex) {
				throw new IllegalArgumentException(ex);
			} catch (TransactionRequiredException ex) {
				throw new IllegalStateException(ex);
			} catch (PersistenceException ex) {
				throw new UnsupportedOperationException(ex);
			} catch (Exception ex) {
				throw new RuntimeException("Could not get results from query '" + typedQuery, ex);
			}
		}

		return null;
	}

	@NotNull
	public static <R extends Object> List<R> getListResultSafely(TypedQuery<R> typedQuery)
			throws IllegalStateException, UnsupportedOperationException {
		if (typedQuery != null) {
			try {
				List<R> results = typedQuery.getResultList();
				if (results != null) {
					return results;
				}
			} catch (IllegalStateException ex) {
				throw ex;
			} catch (TransactionRequiredException ex) {
				throw new IllegalStateException(ex);
			} catch (PersistenceException ex) {
				throw new UnsupportedOperationException(ex);
			} catch (Exception ex) {
				throw new RuntimeException("Could not get results from query '" + typedQuery, ex);
			}
		}

		return new ArrayList<>();
	}

	@SuppressWarnings("rawtypes")
	@NotNull
	public static List getListResultSavely(Query query) throws IllegalStateException, UnsupportedOperationException {
		if (query != null) {
			try {
				List results = query.getResultList();
				if (results != null) {
					return results;
				}
			} catch (IllegalStateException ex) {
				throw ex;
			} catch (TransactionRequiredException ex) {
				throw new IllegalStateException(ex);
			} catch (PersistenceException ex) {
				throw new UnsupportedOperationException(ex);
			} catch (Exception ex) {
				throw new RuntimeException("Could not get results from query '" + query, ex);
			}
		}

		return new ArrayList();
	}

	public static <E> Collection<E> findPaginated(TypedQuery<E> typedQuery, int offset, int limit) {
		if (typedQuery != null) {
			if (offset >= 0) {
				typedQuery.setFirstResult(offset);
			}

			typedQuery.setMaxResults(limit <= 0 ? AEntityBean.DEFAULT_PAGINATED_MAX_RESULTS : limit);

			List<E> list = typedQuery.getResultList();
			if (list == null) {
				list = Collections.emptyList();
			}

			return list;
		}

		return Collections.emptyList();
	}
}
