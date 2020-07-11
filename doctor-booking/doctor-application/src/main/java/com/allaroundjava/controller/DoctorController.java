package com.allaroundjava.controller;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.allaroundjava.dto.DoctorDto;
import com.allaroundjava.dto.mapper.DoctorDtoMapper;
import com.allaroundjava.exception.NotFoundException;
import com.allaroundjava.model.Doctor;
import com.allaroundjava.service.DoctorService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-06-26T23:58:22.588+02:00")
@Api(value = "doctors", description = "the doctors API")
@RequestMapping("/doctors")
public class DoctorController {
	private final DoctorService doctorService;

	@Autowired
	public DoctorController(DoctorService doctorService) {
		this.doctorService = doctorService;
	}

	//@Override
	@ApiOperation(value = "Add a new doctor to Medical Clinic", nickname = "createDoctor", notes = "", response = DoctorDto.class, tags = { "doctors", })
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Created Doctor", response = DoctorDto.class),
			@ApiResponse(code = 405, message = "Invalid input") })
	@RequestMapping(value = { "/", "" }, produces = { "application/xml", "application/json" }, consumes = { "application/xml", "application/json" }, method = RequestMethod.POST)
	public ResponseEntity<DoctorDto> createDoctor(
			@ApiParam(value = "Doctor object that needs to be added to the clinic", required = true) @RequestBody DoctorDto doctorInput) {
		Doctor doctor = DoctorDtoMapper.toEntity(doctorInput);
		doctorService.addDoctor(doctor);
		DoctorDto doctorDto = DoctorDtoMapper.toDto(doctor);
		doctorDto.add(linkTo(methodOn(DoctorController.class).getDoctor(doctor.getId())).withSelfRel());
		return ResponseEntity.status(HttpStatus.CREATED).body(doctorDto);
	}

	//@Override
	@ApiOperation(value = "Retrieve existing doctor information", nickname = "getDoctor", notes = "", response = DoctorDto.class, tags = { "doctors", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved Doctor", response = DoctorDto.class),
			@ApiResponse(code = 400, message = "Invalid ID supplied"),
			@ApiResponse(code = 404, message = "Doctor with specified ID not found") })
	@RequestMapping(value = "/{id}", produces = { "application/xml" }, method = RequestMethod.GET)
	public ResponseEntity<DoctorDto> getDoctor(
			@ApiParam(value = "Id of doctor that needs to be retrieved", required = true) @PathVariable("id") Long id) {
		Optional<Doctor> doctorOptional = doctorService.getById(id);
		Doctor doctor = doctorOptional.orElseThrow(() -> new NotFoundException("Doctor Not Found"));
		DoctorDto doctorDto = DoctorDtoMapper.toDto(doctor);
		doctorDto.add(linkTo(methodOn(DoctorController.class).getDoctor(id)).withSelfRel().withType(MediaType.APPLICATION_XML_VALUE));
		doctorDto.add(linkTo(methodOn(DoctorController.class).getAllDoctors()).withRel("collection").withType(MediaType.APPLICATION_XML_VALUE));
		return ResponseEntity.status(HttpStatus.OK).body(doctorDto);
	}

	//@Override
	@ApiOperation(value = "Retrieve all doctors", nickname = "getAllDoctors", notes = "", response = DoctorDto.class, responseContainer = "List", tags = { "doctors", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved Doctors", response = DoctorDto.class, responseContainer = "List"),
			@ApiResponse(code = 400, message = "Invalid ID supplied"),
			@ApiResponse(code = 404, message = "Doctor with specified ID not found") })
	@RequestMapping(value = { "/", "" }, produces = { "application/xml" }, method = RequestMethod.GET)
	public ResponseEntity<List<DoctorDto>> getAllDoctors() {
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Collections.emptyList());
	}
}
