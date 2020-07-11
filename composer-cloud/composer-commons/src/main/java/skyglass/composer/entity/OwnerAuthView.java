package skyglass.composer.entity;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import skyglass.composer.security.domain.ResourceOwnerView;
import skyglass.composer.security.domain.User;

@MappedSuperclass
public abstract class OwnerAuthView extends AEntity {

	private static final long serialVersionUID = 971241151881141824L;

	@ManyToOne(optional = false)
	private User user;

	@ManyToOne(optional = false)
	private ResourceOwnerView owner;

	@Column(nullable = false)
	private String operation;

}
