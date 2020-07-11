package com.allaroundjava.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.allaroundjava.dto.AppointmentDto;
import com.allaroundjava.dto.mapper.AppointmentMapper;
import com.allaroundjava.exception.NotFoundException;
import com.allaroundjava.model.Appointment;
import com.allaroundjava.model.AppointmentSlot;
import com.allaroundjava.model.Patient;
import com.allaroundjava.service.AppointmentService;
import com.allaroundjava.service.AppointmentSlotService;
import com.allaroundjava.service.PatientService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-06-26T23:58:22.588+02:00")
@Api(value = "appointments", description = "the appointments API")
@RequestMapping("/appointments")
public class AppointmentController {
	private AppointmentService appointmentService;

	private AppointmentSlotService appointmentSlotService;

	private PatientService patientService;

	public AppointmentController(AppointmentService appointmentService, AppointmentSlotService appointmentSlotService, PatientService patientService) {
		this.appointmentService = appointmentService;
		this.appointmentSlotService = appointmentSlotService;
		this.patientService = patientService;

	}

	//@Override
	@ApiOperation(value = "Add a new appointment for given patient and appointment slot", nickname = "createAppointment", notes = "", response = AppointmentDto.class, tags = { "appointments", })
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Created Appointment ", response = AppointmentDto.class),
			@ApiResponse(code = 405, message = "Invalid input") })
	@RequestMapping(value = { "/", "" }, produces = { "application/xml" }, consumes = { "application/xml" }, method = RequestMethod.POST)
	public ResponseEntity<AppointmentDto> createAppointment(
			@ApiParam(value = "Details of Appointment", required = true) @RequestBody AppointmentDto appointmentDto) {
		Patient patient = patientService.getById(appointmentDto.getPatientId()).orElseThrow(() -> new NotFoundException("Patient cannot be found"));
		AppointmentSlot appointmentSlot = appointmentSlotService.getById(appointmentDto.getAppointmentSlotId()).orElseThrow(() -> new NotFoundException("Appointment Slot cannot be found"));

		Appointment appointment = appointmentService.createAppointment(patient, appointmentSlot);
		return ResponseEntity.status(HttpStatus.CREATED).body(AppointmentMapper.toDto(appointment));
	}
}
