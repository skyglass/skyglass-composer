package skyglass.composer.domain;

import java.io.Serializable;

public interface IIdentifiable extends Serializable {

	public String getUuid();

	public void setUuid(String uuid);
}
