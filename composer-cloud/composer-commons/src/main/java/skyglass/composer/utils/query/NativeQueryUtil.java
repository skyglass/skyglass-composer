package skyglass.composer.utils.query;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.Query;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import skyglass.composer.domain.IIdentifiable;
import skyglass.composer.utils.EnumUtil;
import skyglass.composer.utils.query.builder.QueryRequestDTO;

public class NativeQueryUtil {

	/**
	 * Returns the list of OR conditions represented as SQL string from the list
	 * of parameters, for a given property path *
	 *
	 * @param propertyPath
	 *        path to the property in the following format:
	 *        {alias}.{propertyName}, where alias is the alias of the table,
	 *        and propertyName is the name of the table field
	 * @param params
	 *        list of string params, used in OR conditions
	 *
	 * @return SQL string part
	 *
	 *         Warning! Vulnerable to SQL injections! List of parameters should
	 *         never be user-provided! Only use the list of parameters, which
	 *         you totally trust! For example: list of uuids, or list of
	 *         enumerated values
	 */
	public static String getOrList(String propertyPath, List<String> params) {
		return String.format("(%s = '", propertyPath) + String.join(String.format("' OR %s = '", propertyPath), params)
				+ "')";
	}

	/**
	 * Returns a String seperated by ','
	 *
	 * @param <T>
	 *        the type of the Objects inside the List
	 * @param function
	 *        a function how to convert the Object T into a String
	 * @param list
	 *        the elements which should be seperated by ','
	 * @return a String like '( o1, o2, o3)' where o1,o2,o3 are elements of the
	 *         list
	 *
	 *         Warning! Vulnerable to SQL injections! List of parameters should
	 *         never be user-provided! Only use the list of parameters, which
	 *         you totally trust! For example: list of uuids, or list of
	 *         enumerated values
	 */
	public static <T> String getInString(Function<T, String> function, Collection<T> list) {
		if (function == null) {
			return "(" + list.stream().map(s -> "'" + s.toString() + "'").collect(Collectors.joining(", ")) + ")";
		}
		return "(" + list.stream().map(s -> "'" + function.apply(s) + "'").collect(Collectors.joining(", ")) + ")";
	}

	public static <T> String getInString(Collection<T> list) {
		return getInString(null, list);
	}

	/**
	 * Sql injection safe values
	 */
	public static void setInQueryParamValues(List<String> values, Query query) {
		setInQueryParamValues("p", values, query);
	}

	public static void setInQueryParamValues(String prefix, List<String> values, Query query) {
		if (CollectionUtils.isEmpty(values)) {
			return;
		}
		IntStream.range(0, values.size()).forEach(i -> query.setParameter(prefix + (i + 1), values.get(i)));
	}

	/**
	 * Sql injection safe
	 */
	public static String getInQueryParams(int size) {
		return getInQueryParams("p", size);
	}

	public static String getInQueryParams(String prefix, int size) {
		return "(" + IntStream.rangeClosed(1, size).mapToObj(i -> "?" + prefix + i).collect(Collectors.joining(",")) + ")";
	}

	public static <T> String getNullableInString(String path, Collection<T> list) {
		return getNullableInString(path, list, "AND ");
	}

	public static <T> String getNullableInString(String path, Collection<T> list, String separator) {
		return CollectionUtils.isEmpty(list) ? "" : (separator + path + " IN " + getInString(list) + " ");
	}

	/*
	 * Conversion from an Object native query result to a double.
	 * @param object Result cell value
	 * @return Double, default 0
	 */
	public static double getDoubleValueSafely(Object object) {
		if (object == null) {
			return 0;
		}
		if (object instanceof Double) {
			return (double) object;
		}
		try {
			return Double.parseDouble(object.toString());
		} catch (NumberFormatException ex) {
			return 0.0D;
		}
	}

	/*
	 * Conversion from an Object native query result to an integer.
	 * @param object Result cell value
	 * @return Integer, default 0
	 */
	public static int getIntValueSafely(Object object) {
		if (object == null) {
			return 0;
		}
		if (object instanceof Integer) {
			return (int) object;
		}
		if (object instanceof Double) {
			return ((Double) object).intValue();
		}
		try {
			return Integer.parseInt(object.toString());
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	/**
	 * Conversion from an Object native query result to a string.
	 *
	 * @param object
	 *        Result cell value
	 * @return String, empty string in case of NULL
	 */
	public static String getStringValueSafely(Object object) {
		if (object == null) {
			return "";
		}
		if (object instanceof Integer) {
			return String.valueOf(object);
		}
		return (String) object;
	}

	/**
	 * Conversion from an Object native query result to a float.
	 *
	 * @param object
	 *        Result cell value
	 * @return Float, default 0
	 */
	public static float getFloatValueSafely(Object object) {
		if (object == null) {
			return 0;
		}
		if (object instanceof BigDecimal) {
			return ((BigDecimal) object).floatValue();
		}
		return (float) object;
	}

	/**
	 * Conversion from an Object native query result to a long.
	 *
	 * @param object
	 *        Result cell value
	 * @return long, default 0
	 */
	public static long getLongValueSafely(Object object) {
		if (object == null) {
			return 0L;
		}
		if (object instanceof Long) {
			return (long) object;
		}
		try {
			return Long.parseLong(object.toString());
		} catch (NumberFormatException ex) {
			return 0L;
		}
	}

	/**
	 * Converts native query result, represented by collection of Object[] to correspondent DTO object.​
	 * ​ The DTO property names correspond to select fields of parsed selectString.
	 * <p>
	 * <ul>
	 * <li>selectString example: "SELECT DISTINCT sm.test1 as test1, sm.test2 AS test2, sm.test3",
	 * <li>correspondent DTO property names: test1, test2, test3
	 * <li>correspondent Object[] result values: result[0], result[1], result[2]
	 * </ul>
	 * <p>
	 *
	 * <pre>
	 * DTO dto = dtoSupplier.get();
	 * dto.setTest1(result[0];
	 * dto.setTest2(result[1];
	 * dto.setTest3(result[2];
	 * </pre>
	 *
	 * Note: if DTO property type is Enum, then correspondent result[i] value is converted to correspondent Enum type
	 */
	public static <DTO> List<DTO> buildDtoListFromSelectFields(Supplier<DTO> dtoSupplier, Collection<Object[]> queryResult, String selectString) {
		List<DTO> dtoList = new ArrayList<>();
		for (Object[] result : queryResult) {
			dtoList.add(buildDtoFromSelectFields(dtoSupplier, result, selectString));
		}
		return dtoList;
	}

	public static <DTO> DTO buildDtoFromSelectFields(Supplier<DTO> dtoSupplier, Object[] queryResult, String selectString) {
		DTO dto = dtoSupplier.get();
		int i = 0;
		for (String selectAlias : parseSelect(selectString)) {
			Object propValue = queryResult[i];
			if (propValue != null) {
				if (selectAlias.equalsIgnoreCase("uuid") && (dto instanceof IIdentifiable)) {
					IIdentifiable idObject = (IIdentifiable) dto;
					idObject.setUuid(propValue.toString());
				} else {
					try {
						Field typeField = dto.getClass().getDeclaredField(selectAlias);
						@SuppressWarnings("rawtypes")
						Class typeClass = typeField.getType();
						if (typeClass.isEnum()) {
							propValue = EnumUtil.getEnumInstanceObject(propValue, typeClass);
						}
						PropertyUtils.setSimpleProperty(dto, selectAlias, propValue);
					} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | NoSuchFieldException ex) {
						throw new IllegalArgumentException("Could not set value of the property " + selectAlias + " to " + propValue, ex);
					}
				}
			}
			i++;
		}
		return dto;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> convertToMap(Collection<Object[]> queryResult) {
		if (CollectionUtils.isEmpty(queryResult)) {
			return Collections.emptyMap();
		}
		Map<K, V> result = new HashMap<>();
		for (Object[] row : queryResult) {
			K key = (K) row[0];
			V value = (V) row[1];
			result.put(key, value);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, Collection<V>> convertToSetMap(Collection<Object[]> queryResult) {
		if (CollectionUtils.isEmpty(queryResult)) {
			return Collections.emptyMap();
		}
		Map<K, Collection<V>> result = new HashMap<>();
		for (Object[] row : queryResult) {
			K key = (K) row[0];
			V value = (V) row[1];
			result.computeIfAbsent(key, k -> new HashSet<>()).add(value);
		}
		return result;
	}

	private static List<String> parseSelect(String selectString) {
		if (StringUtils.isEmpty(selectString)) {
			return Collections.emptyList();
		}

		List<String> resultList = new ArrayList<>();
		String[] parts = selectString.replaceAll("(?i)select", "").replaceAll("(?i)distinct", "").split(", ");
		for (String part : parts) {
			String[] subParts = part.split("(?i) as ");
			String result = null;
			if (subParts.length == 1) {
				String path = subParts[0].trim();
				String[] pathParts = path.split("\\.");
				result = pathParts[pathParts.length - 1];
			} else {
				result = subParts[1].trim();
			}
			resultList.add(result);
		}

		return resultList;
	}

	public static void setPaging(QueryRequestDTO queryRequest, Query query) {
		if (queryRequest != null) {
			if (queryRequest.getPageNumber() > 0 && queryRequest.getRowsPerPage() > 0) {
				query.setParameter("offset", (queryRequest.getPageNumber() - 1) * queryRequest.getRowsPerPage());
				query.setParameter("limit", queryRequest.getRowsPerPage());
			}
			if (queryRequest.getOffset() >= 0 && queryRequest.getLimit() > 0) {
				query.setParameter("offset", queryRequest.getOffset());
				query.setParameter("limit", queryRequest.getLimit());
			}
		}
	}

}
