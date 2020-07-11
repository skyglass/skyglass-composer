package skyglass.composer.utils.query;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import skyglass.composer.entity.i18n.SupportedLanguage;
import skyglass.composer.utils.query.builder.FieldResolver;
import skyglass.composer.utils.query.builder.QueryRequestDTO;
import skyglass.composer.utils.query.builder.SearchBuilder;
import skyglass.composer.utils.query.builder.SearchField;
import skyglass.composer.utils.query.builder.SearchType;

public class QuerySearchUtil {

	private static final List<String> LANGUAGES = Stream.of(SupportedLanguage.values()).map(e -> e.getLanguageCode()).collect(Collectors.toList());

	public static String applySearch(QueryRequestDTO queryRequest, boolean nativeQuery, String... searchFields) {
		return applySearch(queryRequest, SearchType.IgnoreCase, false, nativeQuery, searchFields);
	}

	public static String applySearch(QueryRequestDTO queryRequest, SearchType searchType, boolean nativeQuery, String... searchFields) {
		return applySearch(queryRequest, searchType, false, nativeQuery, searchFields);
	}

	public static String applyTranslatableSearch(QueryRequestDTO queryRequest, boolean nativeQuery, String... searchFields) {
		return applySearch(queryRequest, SearchType.IgnoreCase, true, nativeQuery, searchFields);
	}

	public static String applyTranslatableSearch(QueryRequestDTO queryRequest, SearchType searchType, boolean nativeQuery, String... searchFields) {
		return applySearch(queryRequest, searchType, true, nativeQuery, searchFields);
	}

	public static String applySearch(QueryRequestDTO queryRequest, SearchType searchType, boolean translatable, boolean nativeQuery, String... searchFields) {
		SearchField search = new SearchField(new FieldResolver(searchFields), SearchBuilder.SEARCH_TERM_FIELD, searchType, translatable, queryRequest.getLang());
		return applySearch(nativeQuery, true, search);
	}

	public static String applySearch(boolean nativeQuery, SearchBuilder searchBuilder) {
		return applySearch(nativeQuery, searchBuilder.isSearchAllTranslations(), searchBuilder.getSearchFields());
	}

	private static String applySearch(boolean nativeQuery, boolean searchAllTranslations, SearchField... searchFields) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (SearchField searchField : searchFields) {
			if (searchField.getFieldResolver().isEmpty()) {
				continue;
			}
			if (first) {
				builder.append("( ");
				first = false;
			} else {
				builder.append(" OR ");
			}
			builder.append(getSearchTerm(searchField, nativeQuery, searchAllTranslations));
		}
		if (first) {
			return null;
		}
		builder.append(" )");
		return builder.toString();
	}

	private static String getTranslatableSearchTerm(String fieldResolver, SearchField searchField, boolean nativeQuery, boolean searchAllTranslations) {
		StringBuilder builder = new StringBuilder();
		String parameterChar = nativeQuery ? "?" : ":";
		boolean first = true;
		for (String lang : getLanguages(searchField, searchAllTranslations)) {
			if (first) {
				first = false;
			} else {
				builder.append(" OR ");
			}
			if (searchField.isIgnoreCase()) {
				builder.append("LOWER(");
			}
			builder.append(fieldResolver).append(".").append(lang);
			if (searchField.isIgnoreCase()) {
				builder.append(")");
			}
			builder.append(" LIKE ");
			builder.append("LOWER(").append(parameterChar).append(searchField.getParamName());
			if (searchField.isIgnoreCase()) {
				builder.append(")");
			}
		}
		return builder.toString();
	}

	public static String getSearchTerm(SearchField searchField, boolean nativeQuery, boolean searchAllTranslations) {
		StringBuilder builder = new StringBuilder();
		builder.append("( ");
		String parameterChar = nativeQuery ? "?" : ":";
		boolean first = true;
		for (String fieldResolver : searchField.getFieldResolver().getResolvers()) {
			if (first) {
				first = false;
			} else {
				builder.append(" OR ");
			}
			if (searchField.isTranslatable()) {
				builder.append(getTranslatableSearchTerm(fieldResolver, searchField, nativeQuery, searchAllTranslations));
			} else {
				if (searchField.isIgnoreCase()) {
					builder.append("LOWER(");
				}
				builder.append(fieldResolver);
				if (searchField.isIgnoreCase()) {
					builder.append(")");
				}
				builder.append(" LIKE ");
				if (searchField.isIgnoreCase()) {
					builder.append("LOWER(");
				}
				builder.append(parameterChar).append(searchField.getParamName());
				if (searchField.isIgnoreCase()) {
					builder.append(")");
				}
			}
		}
		builder.append(" )");
		return builder.toString();
	}

	private static List<String> getLanguages(SearchField searchField, boolean searchAllTranslations) {
		return StringUtils.isBlank(searchField.getLang()) || searchAllTranslations ? LANGUAGES : Collections.singletonList(searchField.getLang());
	}

}
