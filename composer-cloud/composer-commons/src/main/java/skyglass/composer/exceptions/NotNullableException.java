package skyglass.composer.exceptions;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.HttpStatus;

public class NotNullableException extends ClientException {
	private static final long serialVersionUID = 1L;

	public NotNullableException(Class<? extends Serializable> type) {
		this(buildMessage(type), (Throwable) null);
	}

	public NotNullableException(Class<? extends Serializable> type, Throwable cause) {
		this(buildMessage(type), cause);
	}

	public NotNullableException(String field, String... fields) {
		this((Throwable) null, field, fields);
	}

	public NotNullableException(Throwable cause, String field, String... fields) {
		this(null, cause, field, fields);
	}

	public NotNullableException(Class<? extends Serializable> type, String field, String... fields) {
		this(type, null, field, fields);
	}

	public NotNullableException(Class<? extends Serializable> type, Throwable cause, String field, String... fields) {
		this(buildMessage(type, ArrayUtils.add(fields, field)), cause);
	}

	protected NotNullableException(String message, Throwable cause) {
		super(HttpStatus.BAD_REQUEST, message, cause);
	}

	private static String buildMessage(Class<? extends Serializable> type, String... fields) {
		String message = "The given";

		if (fields != null && fields.length != 0) {
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
		} else {
			String typeName = type.getSimpleName();

			message += " " + (!typeName.toUpperCase().endsWith("DTO") ? typeName + " entity" : typeName);
		}

		message += " cannot be null";

		return message;
	}
}
