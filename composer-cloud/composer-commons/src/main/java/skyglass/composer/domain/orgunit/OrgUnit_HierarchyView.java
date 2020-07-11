package skyglass.composer.domain.orgunit;

import javax.persistence.Column;
import javax.persistence.Entity;

import skyglass.composer.entity.AEntity;

@Entity
public class OrgUnit_HierarchyView extends AEntity {

	private static final long serialVersionUID = -5313362805561954738L;

	@Column
	private String child_uuid;

	@Column
	private String parent_uuid;

}
