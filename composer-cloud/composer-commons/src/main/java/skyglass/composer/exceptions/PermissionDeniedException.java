package skyglass.composer.exceptions;

import skyglass.composer.domain.CrudAction;
import skyglass.composer.domain.IIdentifiable;

public class PermissionDeniedException extends NotAllowedException {

	private static final long serialVersionUID = -3993014845712597385L;

	public PermissionDeniedException(Class<? extends IIdentifiable> entityType, CrudAction action) {
		super(entityType, action);
	}

	public PermissionDeniedException(Class<? extends IIdentifiable> entityType, CrudAction action, Throwable cause) {
		super(entityType, action, cause);
	}

	public PermissionDeniedException(Class<? extends IIdentifiable> entityType, String uuid) {
		super(entityType, uuid);
	}

	public PermissionDeniedException(Class<? extends IIdentifiable> entityType, String uuid, Throwable cause) {
		super(entityType, uuid, cause);
	}

}
