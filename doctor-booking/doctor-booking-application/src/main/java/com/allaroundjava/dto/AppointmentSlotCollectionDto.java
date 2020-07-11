package com.allaroundjava.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.RepresentationModel;

@XmlRootElement(name = "AppointmentSlotCollectionDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class AppointmentSlotCollectionDto extends RepresentationModel {
	@XmlElement(name = "AppointmentSlotCollection")
	private List<AppointmentSlotDto> appointmentSlotDtoList;

	public List<AppointmentSlotDto> getAppointmentSlotDtoList() {
		return appointmentSlotDtoList;
	}

	public void setAppointmentSlotDtoList(List<AppointmentSlotDto> appointmentSlotDtoList) {
		this.appointmentSlotDtoList = appointmentSlotDtoList;
	}
}
