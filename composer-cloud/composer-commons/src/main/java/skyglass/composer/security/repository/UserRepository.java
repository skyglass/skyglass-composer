package skyglass.composer.security.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.bean.ASecuredEntityBean;
import skyglass.composer.domain.CrudAction;
import skyglass.composer.domain.businesspartner.BusinessPartner;
import skyglass.composer.domain.orgunit.OrganizationalUnit;
import skyglass.composer.entity.EntityUtil;
import skyglass.composer.exceptions.BusinessRuleValidationException;
import skyglass.composer.exceptions.ClientException;
import skyglass.composer.exceptions.NotAccessibleException;
import skyglass.composer.exceptions.NotAllowedException;
import skyglass.composer.exceptions.NotNullableNorEmptyException;
import skyglass.composer.security.domain.GlobalRole;
import skyglass.composer.security.domain.OperationType;
import skyglass.composer.security.domain.ResourceOwnerView;
import skyglass.composer.security.domain.ResourceType;
import skyglass.composer.security.domain.User;
import skyglass.composer.security.domain.UserContextPermission;
import skyglass.composer.security.domain.UserContextRole;
import skyglass.composer.security.domain.UserHelper;
import skyglass.composer.security.dto.ExtUserDTO;
import skyglass.composer.security.dto.UserDTO;
import skyglass.composer.security.dto.UserDTOFactory;
import skyglass.composer.utils.PlatformUtil;

/**
 * @author skyglass
 */
@Repository
@Transactional
public class UserRepository extends ASecuredEntityBean<User> {

	public static final String ALLOWED_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz123456789.,+-@";

	@Override
	protected ResourceType getResourceType() {
		return ResourceType.User;
	}

	/**
	 * Convenience method to get the user from the session context.
	 *
	 * @return User based on the principal's name
	 */
	public UserDTO getUserDTOFromCtx() {
		return getUserInfo();
	}

	/**
	 * Checks if current user has one of the given roles.
	 *
	 * @param userRoles
	 *        {@link GlobalRole}
	 *
	 * @return {@code true} if user contains at least one role from the list or
	 *         if the list is empty (null) {@code false} otherwise.
	 */
	public boolean hasCurrentUserRole(GlobalRole... userRoles) {
		if (userRoles == null || userRoles.length == 0) {
			return true;
		}

		User user = permissionApi.getUserFromCtx();
		if (user == null) {
			return false;
		}

		List<GlobalRole> currentUserRoles = user.getGlobalRoles();
		if (CollectionUtils.isEmpty(currentUserRoles)) {
			return false;
		}

		return CollectionUtils.containsAny(currentUserRoles, userRoles);
	}

	@NotNull
	public UserDTO getUserInfo() {
		User user = permissionApi.getUserFromCtx();
		if (user != null) {
			ExtUserDTO extUser = getUserFromExt(user.getName());
			if (extUser == null) {
				throw new ClientException(HttpStatus.NOT_FOUND, "Currently logged in user could not be found");
			}

			return UserDTOFactory.createUserDTO(user, extUser);
		}

		throw new ClientException(HttpStatus.NOT_FOUND, "Currently logged in user could not be found");
	}

	public User findUser(String userNameOrId) {
		if (StringUtils.isBlank(userNameOrId)) {
			return null;
		}

		User user = findByUuid(userNameOrId);
		if (user == null) {
			user = findByName(userNameOrId);
		}

		return user;
	}

	@Override
	public User findByUuid(String uuid) {
		if (StringUtils.isBlank(uuid)) {
			return null;
		}

		String queryStr = String.format("SELECT e FROM User e %s WHERE e.uuid = :uuid", jpa("e"));
		TypedQuery<User> typedQuery = entityBeanUtil.createQuery(queryStr, User.class)
				.setParameter("uuid", uuid)
				.setParameter("userUuid", permissionApi.getUserFromCtx().getUuid());

		return EntityUtil.getSingleResultSafely(typedQuery);
	}

	public User findByName(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}

		String queryStr = String.format("SELECT e FROM User e %s WHERE LOWER(e.name) = LOWER(:name)", jpa("e"));
		TypedQuery<User> typedQuery = entityBeanUtil.createQuery(queryStr, User.class)
				.setParameter("name", name)
				.setParameter("userUuid", permissionApi.getUserFromCtx().getUuid());

		return EntityUtil.getSingleResultSafely(typedQuery);
	}

	@NotNull
	public List<User> findByNames(List<String> names) {
		if (names == null || names.isEmpty()) {
			return null;
		}

		String queryStr = String.format("SELECT u FROM User u %s WHERE u.name IN :names", jpa("u"));
		TypedQuery<User> typedQuery = entityBeanUtil.createQuery(queryStr, User.class);
		typedQuery.setParameter("names", names);
		typedQuery.setParameter("userUuid", permissionApi.getUserFromCtx().getUuid());

		return EntityUtil.getListResultSafely(typedQuery);
	}

	@NotNull
	private List<ExtUserDTO> getExtUsersFromResponse(Map<String, Object> resultMap)
			throws IOException, IllegalArgumentException {

		return Collections.emptyList();
	}

	@NotNull
	public ExtUserDTO getUserFromExt(String name) {
		if (StringUtils.isBlank(name)) {
			throw new NotNullableNorEmptyException("Name");
		}

		return PlatformUtil.createDummyUser(name);
	}

	public boolean isCurrentUserAdmin() {
		permissionApi.checkAdmin();
		return true;
	}

	public boolean isCurrentUserAllowedToEditGlobalEntities() {
		return isCurrentUserAdmin();
	}

	public UserDTO getExtUser(User user) {
		if (user == null) {
			return null;
		}

		ExtUserDTO extUser = getUserFromExt(user.getName());

		UserDTO dto = UserDTOFactory.createUserDTO(user, extUser);

		if (dto == null) {
			throw new ClientException("Could not create user dto");
		}

		return dto;
	}

	@NotNull
	public User create(ExtUserDTO entity, UserDTO userDTO, String ownerUuid) {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null.");
		}

		User user;
		if (entity.getId() == null || entity.getId().isEmpty()) {
			user = createExtUser(entity, userDTO, ownerUuid);
		} else {
			user = getExtUser(entity.getId(), userDTO, ownerUuid);
		}

		if (user == null) {
			throw new UnsupportedOperationException("Ext user creation failed.");
		}

		return user;
	}

	@NotNull
	private User getExtUser(String extUserId, UserDTO userDTO, String ownerUuid) {
		if (StringUtils.isBlank(extUserId)) {
			throw new IllegalArgumentException("Ext user ID cannot be null or empty.");
		}

		// check if the Ext user also exists
		getUserFromExt(extUserId);

		return createUser(extUserId, userDTO, ownerUuid);
	}

	@NotNull
	private User createUser(String extUserId, UserDTO userDTO, String ownerUuid) throws IllegalArgumentException, IllegalStateException {
		if (StringUtils.isBlank(extUserId)) {
			throw new IllegalArgumentException("Ext user ID cannot be null or empty.");
		}

		if (StringUtils.isBlank(ownerUuid)) {
			throw new NotNullableNorEmptyException("Owner UUID");
		}

		User dbUser = permissionApi.findByName(extUserId);
		UserHelper.checkExists(dbUser);
		ResourceOwnerView owner = resourceOwnerRepository.findByUuidSecure(ownerUuid);
		userDTO.setUsername(extUserId);
		User user = UserDTOFactory.createUser(userDTO, owner);
		checkSecurity(user, OperationType.Create);

		try {
			entityBeanUtil.persist(user);
		} catch (TransactionRequiredException ex) {
			throw new IllegalStateException(ex);
		}

		return user;
	}

	private User createExtUser(ExtUserDTO extUser, UserDTO userDTO, String ownerUuid) {
		if (extUser == null) {
			throw new IllegalArgumentException("Ext user cannot be null");
		}

		if (extUser.getId() == null || extUser.getId().isEmpty()) {
			return null;
		}

		return createUser(extUser.getId(), userDTO, ownerUuid);
	}

	@NotNull
	public User createUser(String username, List<GlobalRole> userRoles, BusinessPartner mainBusinessPartner,
			List<OrganizationalUnit> orgUnits)
			throws IllegalStateException {
		return createUser(username, userRoles, mainBusinessPartner, orgUnits, 0);
	}

	@NotNull
	private User createUser(String username, List<GlobalRole> userRoles, BusinessPartner mainBusinessPartner,
			List<OrganizationalUnit> orgUnits,
			int acceptedPrivacyPolicyVersion)
			throws IllegalStateException {
		if (!isCurrentUserAdmin()) {
			throw new NotAllowedException(User.class, CrudAction.CREATE);
		}

		User user = findByName(username);
		if (user == null) {
			user = new User();
			user.setName(username);
			user.setGlobalRoles(userRoles);

			try {
				user = entityBeanUtil.persist(user);
			} catch (TransactionRequiredException ex) {
				throw new IllegalStateException(ex);
			}
		}

		return user;
	}

	@NotNull
	public User createUser(User user) {
		if (user == null || StringUtils.isBlank(user.getName())) {
			throw new IllegalArgumentException("User name cannot be null or empty.");
		}

		if (!isCurrentUserAdmin()) {
			throw new NotAllowedException(User.class, CrudAction.CREATE);
		}

		User dbUser = permissionApi.findByName(user.getName());
		if (dbUser != null) {
			throw new BusinessRuleValidationException(
					String.format("User with such name (%s) already exists", user.getName()));
		}

		checkSecurity(user, OperationType.Create);

		entityBeanUtil.persist(user);
		return user;
	}

	@NotNull
	public User createTechnicalUser(String username, List<GlobalRole> userRoles, List<OrganizationalUnit> orgUnits) {

		return createUser(username, userRoles, null, orgUnits, 1);
	}

	@NotNull
	public User updateUserPrivacyPolicyVersion(String userUuid, Integer version)
			throws IllegalArgumentException, IllegalStateException {
		User dbUser = findByUuid(userUuid);
		if (dbUser == null) {
			throw new NotAccessibleException(User.class, userUuid);
		}
		try {
			entityBeanUtil.merge(dbUser);
		} catch (TransactionRequiredException ex) {
			throw new IllegalStateException(ex);
		}
		return dbUser;
	}

	@NotNull
	public User update(ExtUserDTO extUser, UserDTO userDTO) {
		if (!isCurrentUserAdmin()) {
			throw new NotAllowedException(userDTO);
		}

		if (extUser == null || StringUtils.isBlank(extUser.getId())) {
			throw new IllegalArgumentException("Ext user reference missing");
		}

		return updateUser(extUser.getId(), userDTO, extUser.isActive() == null || extUser.isActive());
	}

	@NotNull
	public User changeOwner(String userUuid, String ownerUuid) {
		if (!isCurrentUserAdmin()) {
			throw new NotAllowedException(User.class);
		}

		if (StringUtils.isBlank(userUuid)) {
			throw new NotNullableNorEmptyException("User UUID");
		}

		if (StringUtils.isBlank(ownerUuid)) {
			throw new NotNullableNorEmptyException("Owner UUID");
		}

		User dbUser = findByUuidSecure(userUuid);
		if (dbUser != null) {
			checkSecurity(dbUser, OperationType.ChangeOwner);
			ResourceOwnerView owner = resourceOwnerRepository.findByUuidSecure(ownerUuid);
			dbUser.setOwner(owner);

			try {
				entityBeanUtil.merge(dbUser);
			} catch (TransactionRequiredException ex) {
				throw new IllegalStateException(ex);
			}

		}

		return dbUser;
	}

	@NotNull
	private User updateUser(String extUserId, UserDTO userDTO, boolean isUserActive)
			throws IllegalArgumentException, IllegalStateException, BusinessRuleValidationException {
		if (StringUtils.isBlank(extUserId) && StringUtils.isBlank(userDTO.getUuid())) {
			throw new IllegalArgumentException("User ID cannot be null");
		}

		User dbUser = findByName(extUserId);
		if (dbUser == null) {
			dbUser = findByUuid(userDTO.getUuid());
			if (dbUser != null) {
				//the name has been changed
				dbUser.setName(extUserId);
			}
		}
		if (dbUser != null) {
			checkSecurity(dbUser, OperationType.Update);
			dbUser.setGlobalRoles(userDTO.getUserRoles());

			try {
				entityBeanUtil.merge(dbUser);
			} catch (TransactionRequiredException ex) {
				throw new IllegalStateException(ex);
			}

			return dbUser;
		} else {
			throw new IllegalArgumentException("Could not find user with ID: " + extUserId);
		}
	}

	public void deleteUser(User user) {
		if (user == null) {
			throw new IllegalArgumentException("User cannot be null");
		}

		if (!isCurrentUserAdmin()) {
			throw new NotAllowedException(user);
		}

		deleteUserAndAssociations(user.getUuid());
	}

	private void deleteUserAndAssociations(String userUuid) {
		User user = findByUuidSecure(userUuid);
		checkSecurity(user, OperationType.Delete);
		securityCheckBean.deleteResourceAssociations(userUuid);
		deleteUserAssociations(userUuid);
		entityBeanUtil.remove(user);
	}

	private void deleteUserAssociations(String userUuid) {
		String queryStr = "DELETE FROM UserContextPermission ucp WHERE ucp.user.uuid = :userUuid";
		TypedQuery<UserContextPermission> query = entityBeanUtil.createQuery(queryStr, UserContextPermission.class);
		query.setParameter("userUuid", userUuid);
		query.executeUpdate();

		String queryStr2 = "DELETE FROM UserContextRole ucr WHERE ucr.user.uuid = :userUuid";
		TypedQuery<UserContextRole> query2 = entityBeanUtil.createQuery(queryStr2, UserContextRole.class);
		query2.setParameter("userUuid", userUuid);
		query2.executeUpdate();
	}

	public String getCustomerQuery() {
		return "SELECT DISTINCT bp FROM BusinessPartner bp JOIN bp.customer cust" + " JOIN bp.users u "
				+ "WHERE u.name = :username AND cust IS NOT NULL  ";
	}

	public String getSupplierQuery() {
		return "SELECT DISTINCT bp FROM BusinessPartner bp JOIN bp.supplier sup" + " JOIN bp.users u "
				+ "WHERE u.name = :username AND sup IS NOT NULL  ";
	}

	public UserDTO getUserByExtID(String id) {
		ExtUserDTO extUser = getUserFromExt(id);
		User user = findByName(extUser.getId());
		if (user == null) {
			return null;
		}

		return UserDTOFactory.createUserDTO(user, extUser);
	}

	/**
	 * Search for a list of users specified by their UUIDs
	 *
	 * @param assigneeUuidList
	 * @return
	 */
	@NotNull
	public List<User> findByUuids(@NotNull List<String> assigneeUuidList) {
		if (!assigneeUuidList.isEmpty()) {
			TypedQuery<User> usersQuery = entityBeanUtil.createQuery("SELECT u FROM User u WHERE u.uuid in :uuidList",
					User.class);
			usersQuery.setParameter("uuidList", assigneeUuidList);

			return EntityUtil.getListResultSafely(usersQuery);
		}

		return new ArrayList<>();
	}

	@NotNull
	public List<User> findByRole(String roleName) {
		if (StringUtils.isBlank(roleName)) {
			throw new NotNullableNorEmptyException("Role name");
		}

		String queryStr = "SELECT u FROM User u JOIN u.userRoles r WHERE r = :roleName";
		TypedQuery<User> query = entityBeanUtil.createQuery(queryStr, User.class);
		query.setParameter("roleName", roleName);

		return EntityUtil.getListResultSafely(query);
	}

	public void updateUserGroups(List<String> userGroups, String userName) throws ClientException {
		ExtUserDTO extUser = this.getUserFromExt(userName);

		extUser.setGroups(UserDTOFactory.createExtGroupDTO(userGroups));

	}

	private void checkSecurity(User entity, OperationType operation) {
		securityCheckBean.checkPermissionForResource(entity.getOwner().getUuid(), ResourceType.User, operation, entity.getUuid(), persistentClass);
	}
}
