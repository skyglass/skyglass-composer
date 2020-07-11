package skyglass.composer.utils.query.builder;

import java.util.ArrayList;
import java.util.List;

/**
 * This class allows to build search fields from QueryRequestDTO in a declarative way.
 * Each SearchField class contains information on how to build correspondent SQL LIKE part in WHERE clause
 * 
 */
public class SearchBuilder {

	public static final String SEARCH_TERM_FIELD = "searchTerm";

	private List<SearchField> searchFields = new ArrayList<>();

	private QueryRequestDTO queryRequest;

	private SearchType searchType;

	private String searchTermField;

	private boolean searchAllTranslations = true;

	public SearchBuilder(QueryRequestDTO queryRequest, String... searchFields) {
		this(queryRequest, SearchType.IgnoreCase, SEARCH_TERM_FIELD, false, searchFields);
	}

	public SearchBuilder(QueryRequestDTO queryRequest, String searchTermField, boolean translatable, String... searchFields) {
		this(queryRequest, SearchType.IgnoreCase, searchTermField, translatable, searchFields);
	}

	public SearchBuilder(QueryRequestDTO queryRequest, boolean translatable, String... searchFields) {
		this(queryRequest, SearchType.IgnoreCase, SEARCH_TERM_FIELD, translatable, searchFields);
	}

	private SearchBuilder(QueryRequestDTO queryRequest, SearchType searchType, String searchTermField, boolean translatable, String... searchFields) {
		this.searchType = searchType;
		this.queryRequest = queryRequest;
		this.searchTermField = searchTermField;
		this.searchFields.add(new SearchField(new FieldResolver(searchFields), searchTermField, searchType, translatable, queryRequest.getLang()));
	}

	public SearchBuilder addSearch(String... searchFields) {
		return addSearch(searchType, false, searchFields);
	}

	public SearchBuilder addTranslatableSearch(String... searchFields) {
		return addSearch(searchType, true, searchFields);
	}

	public SearchBuilder addSearch(SearchType searchType, String... searchFields) {
		return addSearch(searchType, false, searchFields);
	}

	public SearchBuilder addTranslatableSearch(SearchType searchType, String... searchFields) {
		return addSearch(searchType, true, searchFields);
	}

	private SearchBuilder addSearch(SearchType searchType, boolean translatable, String... searchFields) {
		this.searchFields.add(new SearchField(new FieldResolver(searchFields), searchTermField, searchType, translatable, queryRequest.getLang()));
		return this;
	}

	public SearchField[] getSearchFields() {
		return searchFields.toArray(new SearchField[0]);
	}

	public boolean isSearchAllTranslations() {
		return searchAllTranslations;
	}

	public void setSearchAllTranslations(boolean searchAllTranslations) {
		this.searchAllTranslations = searchAllTranslations;
	}

}
