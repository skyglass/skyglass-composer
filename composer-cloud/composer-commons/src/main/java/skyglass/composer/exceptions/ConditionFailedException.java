package skyglass.composer.exceptions;

import org.springframework.http.HttpStatus;

public class ConditionFailedException extends ClientException {
	private static final long serialVersionUID = 1L;

	public ConditionFailedException(String message) {
		this(message, null);
	}

	public ConditionFailedException(Throwable cause) {
		this(null, cause);
	}

	public ConditionFailedException(String message, Throwable cause) {
		super(HttpStatus.PRECONDITION_FAILED, message, cause);
	}
}
