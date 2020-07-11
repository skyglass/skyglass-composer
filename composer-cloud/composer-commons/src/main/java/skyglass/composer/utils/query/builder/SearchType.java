package skyglass.composer.utils.query.builder;

public enum SearchType {

	IgnoreCase, StartsIgnoreCase;

	public static boolean isIgnoreCase(SearchType searchType) {
		return true;
	}

	public static String getExpression(SearchType searchType, String value) {
		return searchType == SearchType.StartsIgnoreCase ? ("%" + value) : ("%" + value + "%");
	}

}
