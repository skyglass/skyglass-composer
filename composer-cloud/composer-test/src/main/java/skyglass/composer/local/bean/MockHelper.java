package skyglass.composer.local.bean;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import skyglass.composer.client.user.UserInfo;
import skyglass.composer.security.api.PermissionApi;
import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.ResourceType;
import skyglass.composer.security.domain.User;
import skyglass.composer.security.dto.SecurityCacheRegistry;
import skyglass.composer.security.service.RolePermissionService;
import skyglass.composer.service.TestDataConstants;

@Component
public class MockHelper {
	public static final String TEST_USER1 = TestDataConstants.TEST_USER_1_USERNAME;

	private static User currentUser;

	@Autowired
	private PermissionApi permissionBean;

	@Autowired
	private RolePermissionService rolePermissionService;

	public static User getCurrentUser() {
		return currentUser;
	}

	public UserInfo getDefaultUser() {
		return TestDataConstants.TEST_USER_1;
	}

	public void setupDefault() {
		mockUser(TestDataConstants.TEST_USER_1_USERNAME);
		rolePermissionService.assignOperationToUserResourceType(
				currentUser.getName(), ResourceType.Role, OperationType.Write);
		rolePermissionService.assignOperationToUserOwnerResourceType(
				currentUser.getName(), currentUser.getOwner().getUuid(), ResourceType.User, OperationType.Write);
		rolePermissionService.assignOperationToUserResourceType(
				currentUser.getName(), ResourceType.ResourceOwner, OperationType.Read);
	}

	public void mockDefaultUser() {
		mockUser(TestDataConstants.TEST_USER_1_USERNAME);
	}

	public void logout() {
		Mockito.doReturn(null).when(permissionBean).getUserFromCtx();
		Mockito.doReturn(null).when(permissionBean).getUsernameFromCtx();
		SecurityCacheRegistry.close();
	}

	public void mockUser(String username) {
		SecurityCacheRegistry.close();
		currentUser = permissionBean.getUser(username);
		Mockito.doReturn(currentUser).when(permissionBean).getUserFromCtx();
		Mockito.doReturn(username).when(permissionBean).getUsernameFromCtx();
	}

}
