package com.allaroundjava.controller;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.allaroundjava.dto.PatientDto;
import com.allaroundjava.dto.mapper.PatientDtoMaper;
import com.allaroundjava.exception.NotFoundException;
import com.allaroundjava.model.Patient;
import com.allaroundjava.service.PatientService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-06-26T23:58:22.588+02:00")
@Api(value = "patients", description = "the patients API")
@RequestMapping("/patients")
public class PatientController {

	private static final String PATIENT_NOT_FOUND = "Patient not found";

	private final PatientService patientService;

	@Autowired
	public PatientController(PatientService patientService) {
		this.patientService = patientService;
	}

	//@Override
	@ApiOperation(value = "Retrieve existing patient information", nickname = "getPatient", notes = "", response = PatientDto.class, tags = { "patients", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved Patient", response = PatientDto.class),
			@ApiResponse(code = 400, message = "Invalid ID supplied"),
			@ApiResponse(code = 404, message = "PAtient with specified ID not found") })
	@RequestMapping(value = "/{id}", produces = { "application/xml" }, method = RequestMethod.GET)
	public ResponseEntity<PatientDto> getPatient(
			@ApiParam(value = "Id of patient that needs to be retrieved", required = true) @PathVariable("id") Long id) {
		Patient patient = patientService.getById(id).orElseThrow(() -> new NotFoundException(PATIENT_NOT_FOUND));
		PatientDto patientDto = PatientDtoMaper.toDto(patient);
		patientDto.add(linkTo(methodOn(PatientController.class).getPatient(id)).withSelfRel());
		return ResponseEntity.status(HttpStatus.OK).body(patientDto);
	}

	//@Override
	@ApiOperation(value = "Add a new patient to Medical Clinic", nickname = "createPatient", notes = "", response = PatientDto.class, tags = { "patients", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Created Patient", response = PatientDto.class),
			@ApiResponse(code = 405, message = "Invalid input") })
	@RequestMapping(value = { "/", "" }, produces = { "application/xml" }, consumes = { "application/xml" }, method = RequestMethod.POST)
	public ResponseEntity<PatientDto> createPatient(
			@ApiParam(value = "Patient object that needs to be added to the clinic", required = true) @RequestBody PatientDto patientDto) {
		Patient patient = PatientDtoMaper.toEntity(patientDto);
		patientService.addPatient(patient);
		PatientDto resultDto = PatientDtoMaper.toDto(patient);
		resultDto.add(linkTo(methodOn(PatientController.class).getPatient(patient.getId())).withSelfRel());
		return ResponseEntity.status(HttpStatus.CREATED).body(resultDto);
	}
}
