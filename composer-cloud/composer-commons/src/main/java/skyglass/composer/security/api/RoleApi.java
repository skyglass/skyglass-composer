package skyglass.composer.security.api;

import skyglass.composer.security.dto.RoleDTO;

public interface RoleApi {

	RoleDTO createRole(RoleDTO dto);

	RoleDTO findByName(String name);

}
