/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skyglass.composer.security.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author skyglass
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtGroupMemberDTO implements Serializable {

	private static final long serialVersionUID = 9217593981240364712L;

	String value;

	String display;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

}
