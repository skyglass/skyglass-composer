package skyglass.composer.security.repository;

import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.bean.EntityBeanUtil;
import skyglass.composer.entity.EntityUtil;
import skyglass.composer.exceptions.NotAccessibleException;
import skyglass.composer.security.api.PermissionApi;
import skyglass.composer.security.domain.User;
import skyglass.composer.security.domain.UserHelper;
import skyglass.composer.utils.PlatformUtil;

@Component
public class PermissionBean implements PermissionApi {

	@Autowired
	private EntityBeanUtil entityBeanUtil;

	@Override
	public String getUsernameFromCtx() {
		return PlatformUtil.getUsernameFromCtx();
	}

	@Transactional(readOnly = true)
	@Override
	public User getUserFromCtx() {
		User currentUser = null;

		String username = getUsernameFromCtx();
		if (StringUtils.isNotBlank(username)) {
			currentUser = findByName(username);
		}

		return currentUser;
	}

	@Override
	@Transactional(readOnly = true)
	public User getUser(String userId) {
		User user = null;
		if (StringUtils.isBlank(userId)) {
			user = getUserFromCtx();
		} else {
			user = findByName(userId);
		}
		if (user == null) {
			throw new NotAccessibleException(User.class, userId);
		}
		return user;
	}

	@Override
	@Transactional(readOnly = true)
	public void checkAdmin() {
		UserHelper.checkAdmin(getUserFromCtx());
	}

	@Override
	public User findByName(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}

		String queryStr = "SELECT e FROM User e WHERE LOWER(e.name) = LOWER(:name)";
		TypedQuery<User> typedQuery = entityBeanUtil.createQuery(queryStr, User.class)
				.setParameter("name", name);

		return EntityUtil.getSingleResultSafely(typedQuery);
	}

}
