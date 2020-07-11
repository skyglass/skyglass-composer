package skyglass.composer.utils.query.builder;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Contains the set of sql field paths, correspondent to to some alias
 * For example: alias = "searchTerm", correspondent field paths: "sm.createdBy.name", "sm.operator", where "sm" is alias of some table
 * This class encapsulates correspondence between alias and field paths, which helps later generate correspondent SQL part
 * (see SearchField and OrderField classes, which use this class to later generate correspondent SQL part)
 */
public class FieldResolver {

	private Set<String> fieldResolvers = new LinkedHashSet<String>();

	public FieldResolver(String... fieldResolvers) {
		addResolvers(fieldResolvers);
	}

	public boolean isEmpty() {
		return getResolvers().size() == 0;
	}

	public boolean isMultiple() {
		return getResolvers().size() > 1;
	}

	public boolean isSingle() {
		return getResolvers().size() == 1;
	}

	public String getResolver() {
		for (String fieldResolver : getResolvers()) {
			return fieldResolver;
		}
		return null;
	}

	public Set<String> getResolvers() {
		return fieldResolvers;
	}

	public String[] getResolversArray() {
		return fieldResolvers.toArray(new String[0]);
	}

	public void addResolvers(String... resolvers) {
		for (String resolver : resolvers) {
			fieldResolvers.add(resolver);
		}
	}

}
