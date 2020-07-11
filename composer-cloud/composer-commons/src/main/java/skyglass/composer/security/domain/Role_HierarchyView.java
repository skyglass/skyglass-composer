package skyglass.composer.security.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import skyglass.composer.entity.AEntity;

@Entity
public class Role_HierarchyView extends AEntity {

	private static final long serialVersionUID = 338263596324771131L;

	@Column
	private String child_uuid;

	@Column
	private String parent_uuid;

	@Column
	private String name;

}
