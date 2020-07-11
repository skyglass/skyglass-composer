package skyglass.composer.security.domain;

import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import skyglass.composer.entity.OwnerEntity;

@Entity
@Table(name = "\"USER\"")
public class User extends OwnerEntity {

	private static final long serialVersionUID = 1L;

	//@NotEmpty
	@Size(min = 5, max = 15)
	private String username;

	//@NotEmpty
	@Size(min = 5)
	private String password;

	@Email
	//@NotEmpty
	private String email;

	@Column
	private String name;

	@ElementCollection(targetClass = GlobalRole.class)
	@Enumerated(EnumType.STRING)
	private List<GlobalRole> globalRoles;

	public User() {

	}

	public User(String username, String password, String name, String email) {
		this.username = username;
		this.password = password;
		this.name = username;
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "User " + name + " (" + uuid + ")";
	}

	public List<GlobalRole> getGlobalRoles() {
		return globalRoles;
	}

	public void setGlobalRoles(List<GlobalRole> globalRoles) {
		this.globalRoles = globalRoles;
	}

	public void addGlobalRoles(Collection<GlobalRole> globalRoles) {
		this.globalRoles.addAll(globalRoles);
	}

}
