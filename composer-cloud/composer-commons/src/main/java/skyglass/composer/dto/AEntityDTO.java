package skyglass.composer.dto;

import io.swagger.annotations.ApiModel;
import skyglass.composer.domain.IIdentifiable;

@ApiModel(parent = IIdentifiable.class)
public abstract class AEntityDTO implements IIdentifiable {

	private static final long serialVersionUID = 2168159179321003733L;

	private String uuid;

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public void setUuid(String uuid) {
		this.uuid = uuid;
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
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (obj instanceof AEntityDTO) {
			AEntityDTO other = (AEntityDTO) obj;
			if (uuid != null) {
				return uuid.equals(other.uuid);
			} else if (other.uuid == null) {
				return super.equals(obj);
			}
		}

		return false;
	}

}
