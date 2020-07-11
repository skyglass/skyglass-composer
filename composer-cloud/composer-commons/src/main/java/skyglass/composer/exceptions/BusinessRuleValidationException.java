package skyglass.composer.exceptions;

import org.springframework.http.HttpStatus;

public class BusinessRuleValidationException extends ClientException {
	private static final long serialVersionUID = 1L;

	public BusinessRuleValidationException(String message) {
		this(message, null);
	}

	public BusinessRuleValidationException(Throwable cause) {
		this(null, cause);
	}

	public BusinessRuleValidationException(String message, Throwable cause) {
		super(HttpStatus.BAD_REQUEST, message, cause);
	}
}
