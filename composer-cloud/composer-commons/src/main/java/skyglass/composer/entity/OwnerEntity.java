package skyglass.composer.entity;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import skyglass.composer.security.domain.ResourceOwnerView;

@MappedSuperclass
public abstract class OwnerEntity extends AEntity {

	private static final long serialVersionUID = -3942830740415163280L;

	@ManyToOne
	private ResourceOwnerView owner;

	public ResourceOwnerView getOwner() {
		return owner;
	}

	public void setOwner(ResourceOwnerView owner) {
		this.owner = owner;
	}

}
