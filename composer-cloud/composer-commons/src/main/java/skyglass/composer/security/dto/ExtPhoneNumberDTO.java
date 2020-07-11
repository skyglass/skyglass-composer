package skyglass.composer.security.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author skyglass
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtPhoneNumberDTO implements Serializable {

	private static final long serialVersionUID = -2575501824379306306L;

	private String value;

	private String type;

	public ExtPhoneNumberDTO() {
	}

	public ExtPhoneNumberDTO(String value) {
		this.value = value;
		this.type = "work";
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
