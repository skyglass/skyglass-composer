package skyglass.composer.business.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import skyglass.composer.entity.AEntity;

@Entity
public class BusinessOwner extends AEntity {

	private static final long serialVersionUID = 1657678467998856893L;

	@Column
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
