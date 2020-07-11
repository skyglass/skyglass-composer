package skyglass.composer.security.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import skyglass.composer.entity.AEntity;

@Entity
public class ResourceOwnerView extends AEntity {

	private static final long serialVersionUID = -111183434026942066L;

	@Column
	private String name;

	public String getName() {
		return name;
	}

}
