package skyglass.composer.security.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author skyglass
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtUserDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<ExtEmailDTO> emails = new ArrayList<>();

	private List<ExtPhoneNumberDTO> phoneNumbers = new ArrayList<>();

	private ExtUserNameDTO name;

	private String id;

	private Boolean active;

	private List<ExtGroupForUserDTO> groups = new ArrayList<>();

	public ExtUserDTO() {

	}

	public List<ExtEmailDTO> getEmails() {
		return emails;
	}

	public void setEmails(List<ExtEmailDTO> emails) {
		this.emails = emails;
	}

	public void addEmail(ExtEmailDTO email) {
		this.emails.add(email);
	}

	public List<ExtPhoneNumberDTO> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<ExtPhoneNumberDTO> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public void addPhoneNumber(ExtPhoneNumberDTO number) {
		this.phoneNumbers.add(number);
	}

	public ExtUserNameDTO getName() {
		return name;
	}

	public void setName(ExtUserNameDTO name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public List<ExtGroupForUserDTO> getGroups() {
		return groups;
	}

	public void setGroups(List<ExtGroupForUserDTO> groups) {
		this.groups = groups;
	}

	public void addGroups(ExtGroupForUserDTO group) {
		this.groups.add(group);
	}
}
