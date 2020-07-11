package skyglass.composer.security.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import skyglass.composer.entity.AEntity;

@Entity
public class RolePermissionView extends AEntity {

	private static final long serialVersionUID = 2327795705228700010L;

	@Column(nullable = false)
	private String role_uuid;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ResourceType resourceType;

	@Column(nullable = false)
	private String operation_uuid;

	public String getRole_uuid() {
		return role_uuid;
	}

	public void setRole_uuid(String role_uuid) {
		this.role_uuid = role_uuid;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	public String getOperation_uuid() {
		return operation_uuid;
	}

	public void setOperation_uuid(String operation_uuid) {
		this.operation_uuid = operation_uuid;
	}

}
