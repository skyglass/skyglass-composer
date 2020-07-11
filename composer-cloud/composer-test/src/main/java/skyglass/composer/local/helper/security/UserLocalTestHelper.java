package skyglass.composer.local.helper.security;

import java.util.Arrays;
import java.util.function.Consumer;

import skyglass.composer.local.bean.MockHelper;
import skyglass.composer.security.api.UserApi;
import skyglass.composer.security.domain.GlobalRole;
import skyglass.composer.security.domain.User;
import skyglass.composer.security.dto.UserDTO;
import skyglass.composer.service.TestDataConstants;

public class UserLocalTestHelper {
	private static UserLocalTestHelper INSTANCE;

	private UserApi userApi;

	public static UserLocalTestHelper getInstance() {
		return INSTANCE;
	}

	public static UserLocalTestHelper create(UserApi userApi) {
		if (INSTANCE == null) {
			INSTANCE = new UserLocalTestHelper(userApi);
		}
		return INSTANCE;
	}

	private UserLocalTestHelper(UserApi userApi) {
		this.userApi = userApi;
	}

	public static void setCurrentUserRoles(GlobalRole... userRoles) {
		UserDTO currentUser = INSTANCE.getCurrentUser();
		if (currentUser != null) {
			INSTANCE.setUserRoles(currentUser, userRoles);
		}
	}

	public UserDTO createUser(String username) {
		return createUser(username, null);
	}

	public UserDTO createUser(String username, Consumer<UserDTO> consumer) {
		UserDTO dto = createUserDto(username, consumer);
		return createUserFromDto(dto);
	}

	public UserDTO createUserFromDto(UserDTO dto) {
		return userApi.createUser(dto, TestDataConstants.BUSINESSPARTNER_1_UUID);
	}

	public UserDTO getUser(String username) {
		return userApi.getUserInfoByName(username);
	}

	public UserDTO getCurrentUser() {
		User user = MockHelper.getCurrentUser();
		if (user != null) {
			return userApi.getUserInfoByName(user.getName());
		}
		return null;
	}

	public UserDTO getTestUser1() {
		return userApi.getUserInfoByName(TestDataConstants.getTestUser1().getUsername());
	}

	public UserDTO setUserRoles(UserDTO user, GlobalRole... userRoles) {
		user.setUserRoles(Arrays.asList(userRoles));
		return userApi.updateUser(user);
	}

	private UserDTO createUserDto(String username, Consumer<UserDTO> consumer) {
		UserDTO user = new UserDTO();
		user.setEmail("test@email.com");
		user.setFirstName("First");
		user.setLastName("Last");
		user.setUsername(username);
		if (consumer != null) {
			consumer.accept(user);
		}
		return user;
	}

}
