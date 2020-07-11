/**
 *
 */
package skyglass.composer.security.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import skyglass.composer.domain.IUser;
import skyglass.composer.dto.AEntityDTO;
import skyglass.composer.security.domain.GlobalRole;

/**
 * @author skyglass
 *
 */
@ApiModel(parent = AEntityDTO.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO extends AEntityDTO implements IUser {

	private static final long serialVersionUID = 1L;

	private String email;

	private String username;

	private String firstName;

	private String lastName;

	private boolean active;

	private boolean deleted;

	private String mainBusinessPartnerName;

	private String mainBusinessPartnerUuid;

	private List<GlobalRole> userRoles = new ArrayList<>();

	private List<String> groups = new ArrayList<>();

	private int acceptedPrivacyPolicyVersion;

	private String phoneNumber;

	private String ownerUuid;

	private String ownerName;

	@Override
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMainBusinessPartnerUuid() {
		return mainBusinessPartnerUuid;
	}

	public void setMainBusinessPartnerUuid(String mainBusinessPartnerUuid) {
		this.mainBusinessPartnerUuid = mainBusinessPartnerUuid;
	}

	public String getMainBusinessPartnerName() {
		return mainBusinessPartnerName;
	}

	public void setMainBusinessPartnerName(String mainBusinessPartnerName) {
		this.mainBusinessPartnerName = mainBusinessPartnerName;
	}

	public List<GlobalRole> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(List<GlobalRole> userRoles) {

		this.userRoles = userRoles;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public void addGroups(String group) {
		this.groups.add(group);
	}

	public int getAcceptedPrivacyPolicyVersion() {
		return acceptedPrivacyPolicyVersion;
	}

	public void setAcceptedPrivacyPolicyVersion(int acceptedPrivacyPolicyVersion) {
		this.acceptedPrivacyPolicyVersion = acceptedPrivacyPolicyVersion;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getOwnerUuid() {
		return ownerUuid;
	}

	public void setOwnerUuid(String ownerUuid) {
		this.ownerUuid = ownerUuid;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

}
