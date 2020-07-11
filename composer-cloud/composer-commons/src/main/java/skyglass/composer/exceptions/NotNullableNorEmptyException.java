package skyglass.composer.exceptions;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.ArrayUtils;

public class NotNullableNorEmptyException extends NotNullableException {
	private static final long serialVersionUID = 1L;

	public NotNullableNorEmptyException(@NotNull String field, String... fields) {
		this((Throwable) null, field, fields);
	}

	public NotNullableNorEmptyException(Throwable cause, @NotNull String field, String... fields) {
		super(buildMessage(null, ArrayUtils.add(fields, field)), cause);
	}

	public NotNullableNorEmptyException(Class<? extends Serializable> type, @NotNull String field, String... fields) {
		this(type, null, field, fields);
	}

	public NotNullableNorEmptyException(Class<? extends Serializable> type, Throwable cause, @NotNull String field,
			String... fields) {
		super(buildMessage(type, ArrayUtils.add(fields, field)), cause);
	}

	private static String buildMessage(Class<? extends Serializable> type, String... fields) {
		String message = "The given";

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

		if (type != null) {
			message += " of the " + type.getSimpleName();
		}

		message += " can neither be null nor empty";

		return message;
	}
}
