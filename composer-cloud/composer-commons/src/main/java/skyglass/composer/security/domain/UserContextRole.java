package skyglass.composer.security.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import skyglass.composer.entity.AEntity;

@Entity
public class UserContextRole extends AEntity {

	private static final long serialVersionUID = 7930680583822318751L;

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

	@Column(nullable = true)
	private String resource_uuid;

	@Column(nullable = true)
	private String owner_uuid;

	@ManyToOne(optional = false)
	private Role role;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getContext_uuid() {
		return context_uuid;
	}

	public void setContext_uuid(String context_uuid) {
		this.context_uuid = context_uuid;
	}

	public ContextType getContextType() {
		return contextType;
	}

	public void setContextType(ContextType contextType) {
		this.contextType = contextType;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public RelationType getRelation() {
		return relation;
	}

	public void setRelation(RelationType relation) {
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

}
