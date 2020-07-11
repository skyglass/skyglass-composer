package skyglass.composer.security.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author skyglass
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtGroupForUserDTO implements Serializable {

	private static final long serialVersionUID = 7604936902756249829L;

	private String value;

	public ExtGroupForUserDTO() {
	}

	public ExtGroupForUserDTO(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
