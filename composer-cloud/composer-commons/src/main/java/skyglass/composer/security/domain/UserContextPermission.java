package skyglass.composer.security.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import skyglass.composer.entity.AEntity;

@Entity
public class UserContextPermission extends AEntity {

	private static final long serialVersionUID = 297677639834007365L;

	@ManyToOne(optional = false)
	private User user;

	@Column(nullable = false)
	private String context_uuid;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ContextType contextType;

	@Column(nullable = true)
	@Enumerated(EnumType.STRING)
	private RelationType relation;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ResourceType resourceType;

	@Column(nullable = true)
	private String resource_uuid;

	@Column(nullable = true)
	private String owner_uuid;

	@ManyToOne(optional = false)
	private Operation operation;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ContextType getContextType() {
		return contextType;
	}

	public void setContextType(ContextType contextType) {
		this.contextType = contextType;
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

	public RelationType getRelation() {
		return relation;
	}

	public void setRelation(RelationType relation) {
		this.relation = relation;
	}

	public String getContext_uuid() {
		return context_uuid;
	}

	public void setContext_uuid(String context_uuid) {
		this.context_uuid = context_uuid;
	}

	public String getResource_uuid() {
		return resource_uuid;
	}

	public void setResource_uuid(String resource_uuid) {
		this.resource_uuid = resource_uuid;
	}

	public String getOwner_uuid() {
		return owner_uuid;
	}

	public void setOwner_uuid(String owner_uuid) {
		this.owner_uuid = owner_uuid;
	}

}
