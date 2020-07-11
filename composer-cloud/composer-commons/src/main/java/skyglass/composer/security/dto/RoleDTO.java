package skyglass.composer.security.dto;

import java.util.ArrayList;
import java.util.List;

import skyglass.composer.dto.AEntityDTO;

/**
 * @author skyglass
 *
 */
public class RoleDTO extends AEntityDTO {

	private static final long serialVersionUID = -2669364611522383014L;

	private String parentUuid;

	private boolean global;

	private String name;

	private List<RolePermissionDTO> permissions = new ArrayList<>();

	private List<RoleDTO> children = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RolePermissionDTO> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<RolePermissionDTO> permissions) {
		this.permissions = permissions;
	}

	public String getParentUuid() {
		return parentUuid;
	}

	public void setParentUuid(String parentUuid) {
		this.parentUuid = parentUuid;
	}

	public boolean isGlobal() {
		return global;
	}

	public void setGlobal(boolean global) {
		this.global = global;
	}

	public List<RoleDTO> getChildren() {
		return children;
	}

	public void setChildren(List<RoleDTO> children) {
		this.children = children;
	}

}
