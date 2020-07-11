package skyglass.composer.security.api;

import skyglass.composer.security.domain.User;

public interface PermissionApi {

	public String getUsernameFromCtx();

	public User getUserFromCtx();

	public User getUser(String userId);

	public void checkAdmin();

	public User findByName(String name);

}
