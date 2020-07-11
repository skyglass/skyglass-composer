package skyglass.composer.business.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import skyglass.composer.entity.AEntity;
import skyglass.composer.security.domain.RelationType;

@Entity
public class BusinessContext extends AEntity {

	private static final long serialVersionUID = 8688023232735243129L;

	@Column
	private String name;

	@ManyToOne
	private BusinessContext parent;

	@ManyToOne
	private BusinessOwner contextOwner;

	@ManyToOne
	private BusinessOwner resourceOwner;

	//relation of contextOwner to resourceOwner
	@Enumerated(EnumType.STRING)
	private RelationType relation;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BusinessContext getParent() {
		return parent;
	}

	public void setParent(BusinessContext parent) {
		this.parent = parent;
	}

	public RelationType getRelation() {
		return relation;
	}

	public void setRelation(RelationType relation) {
		this.relation = relation;
	}

	public BusinessOwner getContextOwner() {
		return contextOwner;
	}

	public void setContextOwner(BusinessOwner contextOwner) {
		this.contextOwner = contextOwner;
	}

	public BusinessOwner getResourceOwner() {
		return resourceOwner;
	}

	public void setResourceOwner(BusinessOwner resourceOwner) {
		this.resourceOwner = resourceOwner;
	}

}
