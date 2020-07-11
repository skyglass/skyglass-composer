package skyglass.composer.security.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author skyglass
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtUserNameDTO implements Serializable {

	private static final long serialVersionUID = 1995946997708849320L;

	private String givenName;

	private String familyName;

	private String honorificPrefix;

	public ExtUserNameDTO() {

	}

	public ExtUserNameDTO(String familyName) {
		super();
		this.familyName = familyName;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getHonorificPrefix() {
		return honorificPrefix;
	}

	public void setHonorificPrefix(String honorificPrefix) {
		this.honorificPrefix = honorificPrefix;
	}
}
