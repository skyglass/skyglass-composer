/**
 * 
 */
package skyglass.composer.security.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author skyglass
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtEmailDTO implements Serializable {

	private static final long serialVersionUID = 461051673298587967L;

	private boolean primary;

	private String value;

	public ExtEmailDTO() {
	}

	public ExtEmailDTO(String value) {
		super();
		this.value = value;
	}

	public ExtEmailDTO(String value, boolean primary) {
		super();
		this.value = value;
		this.primary = primary;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

}
