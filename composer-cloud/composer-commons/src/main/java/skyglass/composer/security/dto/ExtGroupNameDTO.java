package skyglass.composer.security.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author skyglass
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtGroupNameDTO implements Serializable {

	private static final long serialVersionUID = -8164856220516283761L;

	String name;

	String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
