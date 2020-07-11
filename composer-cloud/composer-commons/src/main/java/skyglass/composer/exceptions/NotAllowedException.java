package skyglass.composer.exceptions;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;

import skyglass.composer.domain.CrudAction;
import skyglass.composer.domain.IIdentifiable;

public class NotAllowedException extends NotAccessibleException {
	private static final long serialVersionUID = 1L;

	public NotAllowedException(String objectName, @NotNull CrudAction action) {
		this(objectName, action, null);
	}

	public NotAllowedException(String objectName, @NotNull CrudAction action, Throwable cause) {
		this(buildMessage(objectName, action), cause);
	}

	public NotAllowedException(@NotNull IIdentifiable entity) {
		this(entity, null);
	}

	public NotAllowedException(@NotNull IIdentifiable entity, Throwable cause) {
		this(entity.getClass(), entity.getUuid(), cause);
	}

	public NotAllowedException(Class<? extends Serializable> entityType, @NotNull CrudAction action) {
		this(entityType, action, null);
	}

	public NotAllowedException(Class<? extends Serializable> entityType, @NotNull CrudAction action, Throwable cause) {
		this(buildMessage(entityType, action), cause);
	}

	@SafeVarargs
	public NotAllowedException(Class<? extends IIdentifiable> entityType, Pair<String, String>... criterias) {
		this(entityType, null, criterias);
	}

	@SafeVarargs
	public NotAllowedException(Class<? extends IIdentifiable> entityType, Throwable cause,
			Pair<String, String>... criterias) {
		this(buildMessage(entityType, criterias), cause);
	}

	public NotAllowedException(Class<? extends IIdentifiable> entityType) {
		this(entityType, (Throwable) null);
	}

	public NotAllowedException(Class<? extends IIdentifiable> entityType, Throwable cause) {
		this(entityType, (String) null, cause);
	}

	public NotAllowedException(Class<? extends IIdentifiable> entityType, String uuid) {
		this(entityType, uuid, null);
	}

	public NotAllowedException(Class<? extends IIdentifiable> entityType, String uuid, Throwable cause) {
		this(buildMessage(entityType, uuid), cause);
	}

	protected NotAllowedException(String message, Throwable cause) {
		super(HttpStatus.FORBIDDEN, message, cause);
	}

	private static String buildMessage(Class<? extends IIdentifiable> type, String uuid) {
		String typePlaceholder;
		if (StringUtils.isBlank(uuid)) {
			typePlaceholder = (type != null ? type.getSimpleName() : "") + " entities";
		} else {
			typePlaceholder = "the " + (type != null ? type.getSimpleName() : "entity with the UUID '" + uuid + "'");
		}

		return "The current user is not allowed to access " + typePlaceholder;
	}

	private static String buildMessage(Class<? extends Serializable> type, @NotNull CrudAction action) {
		String typeName = "";
		if (type != null) {
			typeName = type.getSimpleName();
		}

		if (StringUtils.isBlank(typeName)) {
			typeName = "entities";
		} else {
			typeName = (!typeName.toUpperCase().endsWith("DTO") ? typeName + " entities" : typeName + "s");
		}

		return buildMessage(typeName, action);
	}

	private static String buildMessage(String typeName, @NotNull CrudAction action) {
		return "Not allowed to " + action.name().toLowerCase() + " " + typeName;
	}

	@SafeVarargs
	private static String buildMessage(Class<? extends IIdentifiable> type, Pair<String, String>... criterias) {
		String typeName = "";
		if (type != null) {
			typeName = type.getSimpleName();
		}

		String typeNamePlaceholder;
		if (criterias == null || criterias.length == 0) {
			typeNamePlaceholder = "the "
					+ (!typeName.toUpperCase().endsWith("DTO") ? typeName + " entities" : typeName + "s");
		} else {
			typeNamePlaceholder = "the " + (!typeName.toUpperCase().endsWith("DTO") ? typeName + " entity" : typeName);
		}

		String message = "The current user is not allowed to access " + typeNamePlaceholder;

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
}
