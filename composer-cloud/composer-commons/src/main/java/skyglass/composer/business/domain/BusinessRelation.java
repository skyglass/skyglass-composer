package skyglass.composer.business.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import skyglass.composer.entity.AEntity;
import skyglass.composer.security.domain.RelationType;

@Entity
public class BusinessRelation extends AEntity {

	private static final long serialVersionUID = 3218540072579485356L;

	@ManyToOne
	private BusinessOwner resourceOwner;

	@ManyToOne
	private BusinessOwner owner;

	//relation of owner to resource owner
	@Enumerated(EnumType.STRING)
	private RelationType relation;

	public RelationType getRelation() {
		return relation;
	}

	public void setRelation(RelationType relation) {
		this.relation = relation;
	}

	public BusinessOwner getResourceOwner() {
		return resourceOwner;
	}

	public void setResourceOwner(BusinessOwner resourceOwner) {
		this.resourceOwner = resourceOwner;
	}

	public BusinessOwner getOwner() {
		return owner;
	}

	public void setOwner(BusinessOwner owner) {
		this.owner = owner;
	}

}
