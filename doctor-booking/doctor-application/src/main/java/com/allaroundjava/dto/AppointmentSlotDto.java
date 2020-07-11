package com.allaroundjava.dto;

import java.time.LocalDateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.springframework.hateoas.RepresentationModel;

import com.allaroundjava.adapter.LocalDateTimeAdapter;

@XmlRootElement(name = "AppointmentSlotDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class AppointmentSlotDto extends RepresentationModel {
	private Long entityId;

	private Long doctorId;

	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	private LocalDateTime startTime;

	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	private LocalDateTime endTime;

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public Long getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Long doctorId) {
		this.doctorId = doctorId;
	}
}
