package skyglass.composer.security.dto;

import skyglass.composer.dto.AEntityDTO;
import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.ResourceType;

public class RolePermissionDTO extends AEntityDTO {

	private static final long serialVersionUID = -2027287672671831360L;

	private ResourceType resourceType;

	private OperationType operationType;

	private boolean inherited;

	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	public OperationType getOperationType() {
		return operationType;
	}

	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}

	public boolean isInherited() {
		return inherited;
	}

	public void setInherited(boolean inherited) {
		this.inherited = inherited;
	}

}
