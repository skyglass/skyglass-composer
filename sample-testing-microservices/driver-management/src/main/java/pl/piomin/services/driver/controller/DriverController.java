package pl.piomin.services.driver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import pl.piomin.services.driver.model.Driver;
import pl.piomin.services.driver.model.DriverInput;
import pl.piomin.services.driver.model.DriverStatus;
import pl.piomin.services.driver.repository.DriverRepository;
import pl.piomin.services.driver.service.DriverLocationService;

@RestController
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-06-26T23:58:22.588+02:00")
@Api(value = "drivers", description = "the drivers API")
@RequestMapping("/drivers")
public class DriverController {

	@Autowired
	private DriverRepository repository;

	@Autowired
	private DriverLocationService driverLocationService;

	@ApiOperation(value = "Add a new driver", nickname = "createDriver", notes = "", response = Driver.class, tags = { "drivers", })
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Created Driver ", response = Driver.class),
			@ApiResponse(code = 405, message = "Invalid input") })
	@RequestMapping(value = { "/", "" }, produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.POST)
	public Driver create(
			@ApiParam(value = "Driver object that needs to be added", required = true) @RequestBody Driver driver) {
		return repository.add(driver);
	}

	@ApiOperation(value = "Update driver", nickname = "updateDriver", notes = "", response = Driver.class, tags = { "drivers", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Updated Driver ", response = Driver.class),
			@ApiResponse(code = 405, message = "Invalid input") })
	@RequestMapping(value = { "/", "" }, produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.PUT)
	public Driver update(
			@ApiParam(value = "Driver object that needs to be updated", required = true) @RequestBody DriverInput driverInput) {
		Driver driver = repository.findById(driverInput.getId());
		driver.setBalance(driver.getBalance() + driverInput.getAmount());
		driver.setStatus(driverInput.getStatus());
		repository.update(driver);
		return driver;
	}

	@ApiOperation(value = "Retrieve existing driver information", nickname = "getDriver", notes = "", response = Driver.class, tags = { "drivers", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved Driver", response = Driver.class),
			@ApiResponse(code = 400, message = "Invalid ID supplied"),
			@ApiResponse(code = 404, message = "Driver with specified ID not found") })
	@RequestMapping(value = "/{id}", produces = { "application/json" }, method = RequestMethod.GET)
	public Driver getById(
			@ApiParam(value = "Id of driver that needs to be retrieved", required = true) @PathVariable Long id) {
		return repository.findById(id);
	}

	@ApiOperation(value = "Retrieve all drivers", nickname = "getAllDrivers", notes = "", response = Driver.class, responseContainer = "List", tags = { "drivers", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved Drivers", response = Driver.class, responseContainer = "List") })
	@RequestMapping(value = { "/", "" }, produces = { "application/json" }, method = RequestMethod.GET)
	public List<Driver> getAll() {
		return repository.findAll();
	}

	@ApiOperation(value = "Find nearest driver", nickname = "findNearestDriver", notes = "", response = Driver.class, tags = { "drivers", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved Driver", response = Driver.class),
			@ApiResponse(code = 400, message = "Invalid parameters supplied"),
			@ApiResponse(code = 404, message = "Driver with specified parameters not found") })
	@RequestMapping(value = "/{locationX}/{locationY}", produces = { "application/json" }, method = RequestMethod.GET)
	public Driver findNearestDriver(
			@ApiParam(value = "LocationX of driver that needs to be found", required = true) @PathVariable("locationX") int locationX,
			@ApiParam(value = "LocationY of driver that needs to be found", required = true) @PathVariable("locationY") int locationY) {
		List<Driver> drivers = repository.findByStatus(DriverStatus.AVAILABLE);
		return driverLocationService.searchNearestDriver(locationX, locationY, drivers);
	}
}
