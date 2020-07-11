package skyglass.composer.security.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import skyglass.composer.entity.AEntity;

@Entity
public class RolePermission extends AEntity {

	private static final long serialVersionUID = 473744940627274590L;

	@ManyToOne(optional = false)
	private Role role;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ResourceType resourceType;

	@ManyToOne(optional = false)
	private Operation operation;

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	@Transient
	public boolean isWrite() {
		return this.operation.getName() == OperationType.Write;
	}

	@Transient
	public boolean isRead() {
		return this.operation.getName() == OperationType.Write
				|| this.operation.getName() == OperationType.Read;
	}

}
