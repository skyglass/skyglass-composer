package skyglass.composer.security.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import skyglass.composer.entity.AEntity;

/**
 * Represents role hierarchy
 *
 * @author skyglass
 *
 */
@Entity
public class Role extends AEntity {

	private static final long serialVersionUID = 1816903693917453093L;

	@ManyToOne(optional = true)
	private Role parent;

	@Column(nullable = false)
	private String name;

	@OneToMany(mappedBy = "role", cascade = { CascadeType.ALL })
	private List<RolePermission> permissions = new ArrayList<>();

	public Role getParent() {
		return parent;
	}

	public void setParent(Role parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RolePermission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<RolePermission> permissions) {
		this.permissions = permissions;
	}

}
