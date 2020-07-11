package com.allaroundjava.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.RepresentationModel;

@XmlRootElement(name = "PatientDto")
public class PatientDto extends RepresentationModel {
	private Long entityId;

	private String name;

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
