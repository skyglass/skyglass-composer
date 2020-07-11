package skyglass.composer.exceptions;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;

import skyglass.composer.domain.IIdentifiable;

public class NotAccessibleException extends ClientException {
	private static final long serialVersionUID = 1L;

	public NotAccessibleException(String field, String... fields) {
		this(null, field, fields);
	}

	public NotAccessibleException(Throwable cause, String field, String... fields) {
		this(buildMessage(ArrayUtils.add(fields, field)), cause);
	}

	public NotAccessibleException(@NotNull IIdentifiable object) {
		this(object, null);
	}

	public NotAccessibleException(@NotNull IIdentifiable object, Throwable cause) {
		this(object.getClass(), object.getUuid(), cause);
	}

	public NotAccessibleException(Class<? extends IIdentifiable> type) {
		this(type, (Throwable) null);
	}

	public NotAccessibleException(Class<? extends IIdentifiable> type, Throwable cause) {
		this(type, null, cause);
	}

	public NotAccessibleException(Class<? extends IIdentifiable> type, String uuid) {
		this(type, uuid, null);
	}

	public NotAccessibleException(Class<? extends IIdentifiable> type, String uuid, Throwable cause) {
		this(null, type, uuid, cause);
	}

	public NotAccessibleException(Class<? extends IIdentifiable> subType, Class<? extends IIdentifiable> type,
			String uuid) {
		this(subType, type, uuid, null);
	}

	public NotAccessibleException(Class<? extends IIdentifiable> subType, Class<? extends IIdentifiable> type,
			String uuid, Throwable cause) {
		this(buildMessage(subType, true, type, uuid), cause);
	}

	public NotAccessibleException(Class<? extends IIdentifiable> subType, boolean singleSubType,
			Class<? extends IIdentifiable> type, String uuid) {
		this(subType, singleSubType, type, uuid, null);
	}

	public NotAccessibleException(Class<? extends IIdentifiable> subType, boolean singleSubType,
			Class<? extends IIdentifiable> type, String uuid, Throwable cause) {
		this(buildMessage(subType, singleSubType, type, uuid), cause);
	}

	@SafeVarargs
	public NotAccessibleException(Class<? extends IIdentifiable> type, Pair<String, String>... criterias) {
		this(type, null, criterias);
	}

	@SafeVarargs
	public NotAccessibleException(String typeName, Pair<String, String> criteria, Pair<String, String>... criterias) {
		this(typeName, (Throwable) null, criteria, criterias);
	}

	@SafeVarargs
	public NotAccessibleException(String typeName, Throwable cause, Pair<String, String> criteria,
			Pair<String, String>... criterias) {
		this(buildMessage(typeName, criteria != null, ArrayUtils.add(criterias, criteria)), cause);
	}

	@SafeVarargs
	public NotAccessibleException(Class<? extends IIdentifiable> type, Throwable cause,
			Pair<String, String>... criterias) {
		this(buildMessage(type == null ? "" : type.getSimpleName(), criterias != null && criterias.length != 0,
				criterias), cause);
	}

	@SafeVarargs
	public NotAccessibleException(Class<? extends IIdentifiable> type, boolean single,
			Pair<String, String>... criterias) {
		this(type, single, null, criterias);
	}

	@SafeVarargs
	public NotAccessibleException(Class<? extends IIdentifiable> type, boolean single, Throwable cause,
			Pair<String, String>... criterias) {
		this(buildMessage(type == null ? "" : type.getSimpleName(), single, criterias), cause);
	}

	private NotAccessibleException(String message, Throwable cause) {
		super(HttpStatus.NOT_FOUND, message, cause);
	}

	protected NotAccessibleException(HttpStatus status, String message, Throwable cause) {
		super(status, message, cause);
	}

	private static String buildMessage(Class<? extends IIdentifiable> subType, boolean singleSubType,
			Class<? extends IIdentifiable> type, String uuid) {
		String subTypePlaceholder = "";
		if (subType != null) {
			if (singleSubType) {
				subTypePlaceholder = "the " + subType.getSimpleName() + " from ";
			} else {
				subTypePlaceholder = "the " + subType.getSimpleName() + "s from ";
			}
		}

		String typePlaceholder;
		if (StringUtils.isBlank(uuid)) {
			typePlaceholder = "the " + (type != null ? type.getSimpleName() : "") + " entities";
		} else {
			typePlaceholder = "the " + (type != null ? type.getSimpleName() : "entity");
		}

		String message = "The current user is not able to access " + subTypePlaceholder + typePlaceholder
				+ (StringUtils.isBlank(uuid) ? "" : " with the UUID '" + uuid + "'");
		if (StringUtils.isBlank(subTypePlaceholder)) {
			message += ", either due to lack of permissions or it doesn't exist";
		} else {
			message += ", either due to lack of permissions or inconsistent data";
		}

		return message;
	}

	@SafeVarargs
	private static String buildMessage(String typeName, boolean single, Pair<String, String>... criterias) {
		String typeNamePlaceholder;
		if (!single && (criterias == null || criterias.length == 0)) {
			typeNamePlaceholder = "the "
					+ (!typeName.toUpperCase().endsWith("DTO") ? typeName + " entities" : typeName + "s");
		} else {
			typeNamePlaceholder = "the " + (!typeName.toUpperCase().endsWith("DTO") ? typeName + " entity" : typeName);
		}

		String message = "The current user is not able to access " + typeNamePlaceholder;

		if (criterias != null && criterias.length > 0) {
			message += " for the given";

			for (Pair<String, String> criteria : criterias) {
				if (criterias.length > 1 && !Objects.equals(criterias[0], criteria)) {
					if (Objects.equals(criterias[criterias.length - 1], criteria)) {
						message += " and";
					} else {
						message += ",";
					}
				}

				message += " " + criteria.getKey() + " '" + criteria.getValue() + "'";
			}
		}

		return message;
	}

	private static String buildMessage(String... fields) {
		String message = "The current user is not able to access";

		for (String field : fields) {
			if (fields.length > 1 && !Objects.equals(fields[0], field)) {
				if (Objects.equals(fields[fields.length - 1], field)) {
					message += " and";
				} else {
					message += ",";
				}
			}

			message += " " + field;
		}

		message += ", either due to lack of permissions or it doesn't exist";

		return message;
	}
}
