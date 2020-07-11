package skyglass.composer.bean;

import skyglass.composer.entity.AEntity;
import skyglass.composer.exceptions.NotAccessibleException;

public interface EntityRepository<E extends AEntity> {

	public E findByUuid(String uuid);

	public E findByUuidSecure(String uuid) throws NotAccessibleException;

}
