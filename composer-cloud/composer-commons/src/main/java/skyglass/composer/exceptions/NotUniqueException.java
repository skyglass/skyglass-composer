package skyglass.composer.exceptions;

import org.springframework.http.HttpStatus;

public class NotUniqueException extends ClientException {
	private static final long serialVersionUID = 1L;

	public NotUniqueException(String message) {
		this(message, null);
	}

	public NotUniqueException(Throwable cause) {
		this(null, cause);
	}

	public NotUniqueException(String message, Throwable cause) {
		super(HttpStatus.CONFLICT, message, cause);
	}
}
