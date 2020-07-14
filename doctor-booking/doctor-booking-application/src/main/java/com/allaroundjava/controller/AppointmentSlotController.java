package com.allaroundjava.controller;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.methodOn;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.allaroundjava.client.api.DoctorsApi;
import com.allaroundjava.client.invoker.ApiClient;
import com.allaroundjava.client.model.DoctorDto;
import com.allaroundjava.dto.AppointmentSlotCollectionDto;
import com.allaroundjava.dto.AppointmentSlotDto;
import com.allaroundjava.dto.mapper.AppointmentSlotMapper;
import com.allaroundjava.exception.NotFoundException;
import com.allaroundjava.model.AppointmentSlot;
import com.allaroundjava.model.Doctor;
import com.allaroundjava.service.AppointmentSlotService;
import com.allaroundjava.service.DoctorService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-06-26T23:58:22.588+02:00")
@Api(value = "slots", description = "the slots API")
@RequestMapping("/slots")
public class AppointmentSlotController {
	private final AppointmentSlotService appointmentSlotService;

	private final DoctorService doctorService;

	private final DoctorsApi doctorsApi;

	@Autowired
	public AppointmentSlotController(AppointmentSlotService appointmentSlotService, DoctorService doctorService) {
		this.appointmentSlotService = appointmentSlotService;
		this.doctorService = doctorService;
		ApiClient apiClient = new ApiClient();
		//apiClient.setBasePath(apiClient.getBasePath().replace("8080", Integer.toString(port)));
		doctorsApi = new DoctorsApi(apiClient);
	}

	//@Override
	@ApiOperation(value = "Add a new appointment slot at given date for specified doctor", nickname = "createSlot", notes = "", response = AppointmentSlotDto.class, tags = { "slots", })
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Created Appointment Slot ", response = AppointmentSlotDto.class),
			@ApiResponse(code = 405, message = "Invalid input") })
	@RequestMapping(value = { "/", "" }, produces = { "application/xml" }, consumes = { "application/xml" }, method = RequestMethod.POST)
	public ResponseEntity<AppointmentSlotDto> createSlot(
			@ApiParam(value = "Details of Appointment", required = true) @RequestBody AppointmentSlotDto appointmentSlotInput) {
		Doctor doctor = doctorService.getById(appointmentSlotInput.getDoctorId())
				.orElseThrow(() -> new NotFoundException(String.format("Doctor with ID %d not found", appointmentSlotInput.getDoctorId())));
		AppointmentSlot appointmentSlot = AppointmentSlotMapper.toEntity(appointmentSlotInput, doctor);
		appointmentSlotService.addAppointmentSlot(appointmentSlot);

		AppointmentSlotDto appointmentSlotDto = AppointmentSlotMapper.toDto(appointmentSlot);
		appointmentSlotDto.add(linkTo(methodOn(AppointmentSlotController.class).getSlotById(appointmentSlot.getId())).withSelfRel());
		return ResponseEntity.status(HttpStatus.CREATED).body(appointmentSlotDto);
	}

	@ApiOperation(value = "Retrieve existing Appointment Slot by Id", nickname = "getSlotById", notes = "", response = AppointmentSlotDto.class, tags = { "slots", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved Appointment Slot", response = AppointmentSlotDto.class),
			@ApiResponse(code = 400, message = "Invalid ID supplied"),
			@ApiResponse(code = 404, message = "Appointment Slot with specified ID not found") })
	@RequestMapping(value = "/{id}", produces = { "application/xml" }, method = RequestMethod.GET)
	public ResponseEntity<AppointmentSlotDto> getSlotById(
			@ApiParam(value = "Appointment Slot id", required = true) @PathVariable("id") Long id) {
		AppointmentSlot appointmentSlot = appointmentSlotService.getById(id)
				.orElseThrow(() -> new NotFoundException(String.format("Appointment Slot with id %d not found", id)));
		AppointmentSlotDto appointmentSlotDto = AppointmentSlotMapper.toDto(appointmentSlot);
		appointmentSlotDto.add(linkTo(methodOn(AppointmentSlotController.class).getSlotById(appointmentSlot.getId())).withSelfRel());
		return ResponseEntity.status(HttpStatus.OK).body(appointmentSlotDto);
	}

	//@Override
	@ApiOperation(value = "Retrieve all appointment slots for given doctor in specified dates", nickname = "getSlots", notes = "", response = AppointmentSlotCollectionDto.class, tags = { "slots", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved appointment slots", response = AppointmentSlotCollectionDto.class),
			@ApiResponse(code = 400, message = "Invalid data supplied") })
	@RequestMapping(value = { "/", "" }, produces = { "application/xml" }, method = RequestMethod.GET)
	public ResponseEntity<AppointmentSlotCollectionDto> getSlots(
			@ApiParam(value = "Id of doctor for whom appointments to retrieve", required = true) @RequestParam(value = "doctorId", required = true) Long doctorId,
			@ApiParam(value = "Slots after this date will be retrieved", required = true) @RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@ApiParam(value = "Slots up to this date will be retrieved", required = true) @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

		Doctor doctor = doctorService.getById(doctorId).orElseThrow(() -> new NotFoundException(String.format("Doctor with ID %d not found", doctorId)));

		List<AppointmentSlot> result = appointmentSlotService.getAppointmentSlotsBetween(doctor, startDate, endDate);

		AppointmentSlotCollectionDto collectionDto = AppointmentSlotMapper.toCollectionDto(result);
		buildHateoasForSlotCollection(doctorId, startDate, endDate, collectionDto);
		return ResponseEntity.status(HttpStatus.OK).body(collectionDto);
	}

	@ApiOperation(value = "Retrieve all appointment slots for given doctor in specified dates", nickname = "getSlots", notes = "", response = AppointmentSlotCollectionDto.class, tags = { "slots", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved appointment slots", response = AppointmentSlotCollectionDto.class),
			@ApiResponse(code = 400, message = "Invalid data supplied") })
	@RequestMapping(value = { "/integration-test" }, produces = { "application/xml" }, method = RequestMethod.GET)
	public ResponseEntity<AppointmentSlotCollectionDto> getSlotsForIntegrationTest(
			@ApiParam(value = "Id of doctor for whom appointments to retrieve", required = true) @RequestParam(value = "doctorId", required = true) Long doctorId,
			@ApiParam(value = "Slots after this date will be retrieved", required = true) @RequestParam(value = "startDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@ApiParam(value = "Slots up to this date will be retrieved", required = true) @RequestParam(value = "endDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
		//Doctor doctor = doctorService.getById(doctorId)
		DoctorDto doctorDto = doctorsApi.getDoctor(doctorId);
		if (doctorDto == null) {
			throw new NotFoundException(String.format("Doctor with ID %d not found", doctorId));
		}
		Doctor doctor = doctorService.getById(doctorDto.getEntityId()).get();

		List<AppointmentSlot> result = appointmentSlotService.getAppointmentSlotsBetween(doctor, startDate, endDate);

		AppointmentSlotCollectionDto collectionDto = AppointmentSlotMapper.toCollectionDto(result);
		buildHateoasForSlotCollection(doctorId, startDate, endDate, collectionDto);
		return ResponseEntity.status(HttpStatus.OK).body(collectionDto);
	}

	private void buildHateoasForSlotCollection(Long doctorId,
			LocalDateTime startDate,
			LocalDateTime endDate,
			AppointmentSlotCollectionDto collectionDto) {
		for (AppointmentSlotDto dto : collectionDto.getAppointmentSlotDtoList()) {
			dto.add(linkTo(methodOn(AppointmentSlotController.class).getSlotById(dto.getEntityId())).withSelfRel());
		}
		collectionDto.add(linkTo(methodOn(AppointmentSlotController.class).getSlots(doctorId, startDate, endDate)).withSelfRel());
	}
}
