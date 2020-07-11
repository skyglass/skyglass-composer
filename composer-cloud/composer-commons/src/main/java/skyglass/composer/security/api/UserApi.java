package skyglass.composer.security.api;

import skyglass.composer.security.dto.UserDTO;

public interface UserApi {

	UserDTO getUserInfoByName(String username);

	UserDTO updateUser(UserDTO dto);

	UserDTO createUser(UserDTO dto, String ownerUuid);

	UserDTO changeOwner(String userUuid, String ownerUuid);

	UserDTO deleteUser(String userUuid);

}
