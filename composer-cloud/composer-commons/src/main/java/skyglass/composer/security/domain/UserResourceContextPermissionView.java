package skyglass.composer.security.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import skyglass.composer.entity.AEntity;

@Entity
public class UserResourceContextPermissionView extends AEntity {

	private static final long serialVersionUID = 2141226460025001848L;

	@Column
	private String user_uuid;

	@Column
	private String context_uuid;

	@Column(nullable = false)
	private String contextType;

	@Column(nullable = true)
	private String relation;

	@Column(nullable = false)
	private String resourceType;

	@Column(nullable = true)
	private String resource_uuid;

	@Column(nullable = true)
	private String owner_uuid;

	@Column(nullable = false)
	private String operation_uuid;

	@Column(nullable = false)
	private String operation_name;

	public String getUser_uuid() {
		return user_uuid;
	}

	public void setUser_uuid(String user_uuid) {
		this.user_uuid = user_uuid;
	}

	public String getContext_uuid() {
		return context_uuid;
	}

	public void setContext_uuid(String context_uuid) {
		this.context_uuid = context_uuid;
	}

	public String getContextType() {
		return contextType;
	}

	public void setContextType(String contextType) {
		this.contextType = contextType;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getOperation_uuid() {
		return operation_uuid;
	}

	public void setOperation_uuid(String operation_uuid) {
		this.operation_uuid = operation_uuid;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
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

	public String getOperation_name() {
		return operation_name;
	}

	public void setOperation_name(String operation_name) {
		this.operation_name = operation_name;
	}

}
