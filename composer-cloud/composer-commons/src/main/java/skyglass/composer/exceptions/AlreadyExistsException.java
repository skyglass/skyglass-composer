package skyglass.composer.exceptions;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;

import skyglass.composer.entity.AEntity;

public class AlreadyExistsException extends ClientException {
	private static final long serialVersionUID = 1L;

	public AlreadyExistsException(@NotNull AEntity entity) {
		this(entity.getClass(), entity.getUuid(), null);
	}

	public AlreadyExistsException(@NotNull AEntity entity, Throwable cause) {
		this(entity.getClass(), entity.getUuid(), cause);
	}

	@SafeVarargs
	public AlreadyExistsException(Class<? extends AEntity> entityType, Pair<String, String>... criterias) {
		this(entityType, null, criterias);
	}

	@SafeVarargs
	public AlreadyExistsException(Class<? extends AEntity> entityType, Throwable cause,
			Pair<String, String>... criterias) {
		super(HttpStatus.CONFLICT, buildMessage(entityType, criterias), cause);
	}

	public AlreadyExistsException(Class<? extends AEntity> entityType, String uuid) {
		this(entityType, uuid, null);
	}

	public AlreadyExistsException(Class<? extends AEntity> entityType, String uuid, Throwable cause) {
		super(HttpStatus.CONFLICT, "A " + (entityType != null ? entityType.getSimpleName() : "unknown entity type")
				+ " already exists with the UUID: " + uuid, cause);
	}

	@SafeVarargs
	private static String buildMessage(Class<? extends AEntity> entityType, Pair<String, String>... criterias) {
		String entityPlaceholder = (entityType != null ? " " + entityType.getSimpleName() : "");
		if (StringUtils.isBlank(entityPlaceholder)) {
			entityPlaceholder = "n";
		} else if (StringUtils.startsWithAny(entityPlaceholder.toLowerCase(), "a", "e", "i", "o", "u")) {
			entityPlaceholder = "n " + entityPlaceholder;
		}

		String message = "A" + entityPlaceholder + " entity already exists";

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
