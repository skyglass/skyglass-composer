package skyglass.composer.utils.query;

import org.apache.commons.lang3.StringUtils;

import skyglass.composer.domain.Direction;

public class QueryFunctions {

	private static final String DIRECTION_STRING = initDirectionString();

	private static String coalesce(String[] fieldResolvers, boolean lower) {
		StringBuilder sb = new StringBuilder();
		sb.append("COALESCE(");
		int i = 0;
		for (String fieldResolver : fieldResolvers) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(lower ? lower(fieldResolver) : fieldResolver);
			i++;
		}
		sb.append(")");
		return sb.toString();
	}

	public static String lower(String fieldResolver) {
		return String.format("LOWER(TRIM(%s))", fieldResolver);
	}

	public static String coalesce(String... fieldResolvers) {
		return coalesce(fieldResolvers, false);
	}

	public static String lowerCoalesce(String... fieldResolvers) {
		return coalesce(fieldResolvers, true);
	}

	public static String and(String queryStr1, String queryStr2) {
		return StringUtils.isNotBlank(queryStr1) ? (queryStr1 + " AND " + queryStr2) : queryStr2;
	}

	public static String direction(String path) {
		return String.format(DIRECTION_STRING, path);
	}

	private static String initDirectionString() {
		String result = "CASE %s";
		for (Direction direction : Direction.values()) {
			result += " WHEN " + direction.ordinal() + " THEN '" + direction.toString() + "'";
		}
		result += " END";
		return result;
	}

}
