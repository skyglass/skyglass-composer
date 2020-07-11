package skyglass.composer.utils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class EnumUtil {

	public static List<String> getEnumNameList(Collection<? extends Enum<?>> entities) {
		return entities.stream().map(e -> e.name()).collect(Collectors.toList());
	}

	public static List<String> getEnumOrdinalList(Collection<? extends Enum<?>> entities) {
		return entities.stream().map(e -> Integer.toString(e.ordinal())).collect(Collectors.toList());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object getEnumInstanceObject(Object value, Class enumClass) {
		String stringValue = value == null ? null : value.toString();
		if (StringUtils.isBlank(stringValue)) {
			return null;
		}
		return Enum.valueOf(enumClass, stringValue);
	}

	public static <T extends Enum<T>> T getEnumInstance(Object value, Class<T> enumClass) {
		String stringValue = value == null ? null : value.toString();
		if (StringUtils.isBlank(stringValue)) {
			return null;
		}
		return Enum.valueOf(enumClass, value.toString());
	}

}
