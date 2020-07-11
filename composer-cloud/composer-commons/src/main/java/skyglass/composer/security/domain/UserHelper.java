package skyglass.composer.security.domain;

import skyglass.composer.exceptions.BusinessRuleValidationException;

public class UserHelper {

	public static boolean isAdmin(User user) {
		return user != null && user.getGlobalRoles().contains(GlobalRole.Admin);
	}

	public static void checkAdmin(User user) {
		if (!isAdmin(user)) {
			throw new BusinessRuleValidationException(
					"Security violation! Not enough permissions for access");
		}
	}

	public static void checkExists(User user) {
		if (user != null) {
			throw new BusinessRuleValidationException(
					String.format("User with such name (%s) already exists", user.getName()));
		}
	}

}
