package skyglass.composer.security.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import skyglass.composer.entity.AEntity;

@Entity
public class Context_HierarchyView extends AEntity {

	private static final long serialVersionUID = 7532601422197086215L;

	@ManyToOne
	private ResourceContextView child;

	@ManyToOne
	private ResourceContextView parent;

	@ManyToOne
	private ResourceOwnerView owner;

}
