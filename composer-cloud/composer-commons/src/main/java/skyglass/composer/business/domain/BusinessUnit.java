package skyglass.composer.business.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import skyglass.composer.entity.AEntity;

@Entity
public class BusinessUnit extends AEntity {

	private static final long serialVersionUID = -6591374795347929140L;

	@Column
	private String name;

	@ManyToOne
	private BusinessOwner owner;

	@ManyToOne
	private BusinessUnit parent;

	public BusinessUnit getParent() {
		return parent;
	}

	public void setParent(BusinessUnit parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BusinessOwner getOwner() {
		return owner;
	}

	public void setOwner(BusinessOwner owner) {
		this.owner = owner;
	}

}
