package skyglass.composer.client.user;

import java.util.Objects;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class UserInfo {
	private final String username;

	private final String password;

	private final String firstname;

	private final String lastname;

	private final String email;

	public UserInfo(String username, String password, String firstname, String lastname, String email) {
		this.username = username;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public String getLastname() {
		return this.lastname;
	}

	public String getEmail() {
		return this.email;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof UserInfo) {
			UserInfo other = (UserInfo) obj;

			return Objects.equals(this.username, other.username);
		}

		return false;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder();
		hcb.append(this.username);

		return hcb.build();
	}
}
