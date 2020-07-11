package skyglass.composer.security.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import skyglass.composer.entity.AEntity;

@Entity
public class Operation extends AEntity {

	private static final long serialVersionUID = 8483199728104382980L;

	@ManyToOne(optional = true)
	private Operation parent;

	@Enumerated(EnumType.STRING)
	private OperationType name;

	public Operation getParent() {
		return parent;
	}

	public void setParent(Operation parent) {
		this.parent = parent;
	}

	public OperationType getName() {
		return name;
	}

	public void setName(OperationType name) {
		this.name = name;
	}

}
