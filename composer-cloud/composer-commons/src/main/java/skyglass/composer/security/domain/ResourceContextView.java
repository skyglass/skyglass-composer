package skyglass.composer.security.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import skyglass.composer.entity.AEntity;

@Entity
public class ResourceContextView extends AEntity {

	private static final long serialVersionUID = -686058020943948945L;

	@Column
	private String name;

	@ManyToOne
	private ResourceContextView parent;

	@ManyToOne
	private ResourceOwnerView contextOwner;

	@ManyToOne
	private ResourceOwnerView resourceOwner;

	//relation of contextOwner to resourceOwner
	@Column(nullable = false)
	private String relation;

}
