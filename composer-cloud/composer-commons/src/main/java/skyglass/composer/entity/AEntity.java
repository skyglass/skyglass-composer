package skyglass.composer.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.eclipse.persistence.annotations.UuidGenerator;

import skyglass.composer.domain.IIdentifiable;

@MappedSuperclass
@UuidGenerator(name = "ABSTRACT_ENTITY_ID")
public abstract class AEntity implements IIdentifiable {

	private static final long serialVersionUID = -4895128247398446344L;

	@Id
	@GeneratedValue(generator = "ABSTRACT_ENTITY_ID")

	protected String uuid;

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public void setUuid(String id) {
		this.uuid = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AEntity other = (AEntity) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + uuid;
	}

}
