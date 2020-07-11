package pl.piomin.services.trip.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import pl.piomin.services.trip.client.DriverManagementClient;
import pl.piomin.services.trip.client.PassengerManagementClient;
import pl.piomin.services.trip.model.Driver;
import pl.piomin.services.trip.model.DriverInput;
import pl.piomin.services.trip.model.DriverStatus;
import pl.piomin.services.trip.model.Passenger;
import pl.piomin.services.trip.model.PassengerInput;
import pl.piomin.services.trip.model.Trip;
import pl.piomin.services.trip.model.TripInput;
import pl.piomin.services.trip.model.TripStatus;
import pl.piomin.services.trip.repository.TripRepository;

@RestController
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2020-06-26T23:58:22.588+02:00")
@Api(value = "trips", description = "the trips API")
@RequestMapping("/trips")
public class TripController {

	@Value("${app.updateDriverStatus:true}")
	private boolean updateDriver;

	@Autowired
	private TripRepository repository;

	@Autowired
	private DriverManagementClient driverManagementClient;

	@Autowired
	private PassengerManagementClient passengerManagementClient;

	@ApiOperation(value = "Add a new trip", nickname = "createTrip", notes = "", response = Trip.class, tags = { "trips", })
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Created Trip ", response = Trip.class),
			@ApiResponse(code = 405, message = "Invalid input") })
	@RequestMapping(value = { "/", "" }, produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.POST)
	public Trip create(
			@ApiParam(value = "Trip object that needs to be added", required = true) @RequestBody TripInput tripInput) {
		Trip trip = new Trip();
		Passenger passenger = passengerManagementClient.getPassenger(tripInput.getUsername());
		if (passenger.getBalance() < 0) {
			trip.setStatus(TripStatus.REJECTED);
			return trip;
		}
		trip.setPassengerId(passenger.getId());
		trip.setDestination(tripInput.getDestination());
		Driver driver = null;
		if (tripInput.getLocationX() != null && tripInput.getLocationY() != null) {
			trip.setLocationX(tripInput.getLocationX());
			trip.setLocationY(tripInput.getLocationY());
			driver = driverManagementClient.getNearestDriver(passenger.getHomeLocationX(), passenger.getHomeLocationY());
		} else {
			trip.setLocationX(passenger.getHomeLocationX());
			trip.setLocationY(passenger.getHomeLocationY());
			driver = driverManagementClient.getNearestDriver(tripInput.getLocationX(), tripInput.getLocationY());
		}
		if (driver == null) {
			trip.setStatus(TripStatus.REJECTED);
			return trip;
		}
		trip.setDriverId(driver.getId());
		trip.setStatus(TripStatus.NEW);
		trip.setStartTime(System.currentTimeMillis());
		trip = repository.add(trip);
		if (updateDriver)
			driverManagementClient.updateDriver(new DriverInput(driver.getId(), DriverStatus.AVAILABLE));
		return trip;
	}

	@ApiOperation(value = "Cancel trip", nickname = "cancelTrip", notes = "", response = Trip.class, tags = { "trips", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Cancelled Trip ", response = Trip.class),
			@ApiResponse(code = 405, message = "Invalid input") })
	@RequestMapping(value = "/cancel/{id}", produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.PUT)
	public Trip cancel(
			@ApiParam(value = "Id of the trip that needs to be cancelled", required = true) @PathVariable("id") Long id) {
		Trip trip = repository.findById(id);
		driverManagementClient.updateDriver(new DriverInput(trip.getDriverId(), DriverStatus.AVAILABLE));
		Driver driver = driverManagementClient.getNearestDriver(trip.getLocationX(), trip.getLocationY());
		if (driver != null) {
			trip.setDriverId(driver.getId());
			trip.setStatus(TripStatus.IN_PROGRESS);
		} else {
			trip.setStatus(TripStatus.REJECTED);
		}
		return trip;
	}

	@ApiOperation(value = "Pay for trip", nickname = "payTrip", notes = "", response = Trip.class, tags = { "trips", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Paid Trip ", response = Trip.class),
			@ApiResponse(code = 405, message = "Invalid input") })
	@RequestMapping(value = "/payment/{id}", produces = { "application/json" }, consumes = { "application/json" }, method = RequestMethod.PUT)
	public Trip payment(
			@ApiParam(value = "Id of the trip that needs to be paid", required = true) @PathVariable("id") Long id) {
		Trip trip = repository.findById(id);
		long duration = System.currentTimeMillis() - trip.getStartTime();
		trip.setPrice((int) (duration / 1000));
		passengerManagementClient.updatePassenger(new PassengerInput(trip.getPassengerId(), (-1) * trip.getPrice()));
		driverManagementClient.updateDriver(new DriverInput(trip.getDriverId(), DriverStatus.AVAILABLE, trip.getPrice()));
		trip.setStatus(TripStatus.PAYED);
		return repository.update(trip);
	}

	@ApiOperation(value = "Retrieve existing trip information", nickname = "getTrip", notes = "", response = Trip.class, tags = { "trips", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved Trip", response = Trip.class),
			@ApiResponse(code = 400, message = "Invalid ID supplied"),
			@ApiResponse(code = 404, message = "Trip with specified ID not found") })
	@RequestMapping(value = "/{id}", produces = { "application/json" }, method = RequestMethod.GET)
	public Trip getById(
			@ApiParam(value = "Id of trip that needs to be retrieved", required = true) @PathVariable Long id) {
		return repository.findById(id);
	}

	@ApiOperation(value = "Retrieve all trips", nickname = "getAllTrips", notes = "", response = Trip.class, responseContainer = "List", tags = { "trips", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved Trips", response = Trip.class, responseContainer = "List") })
	@RequestMapping(value = { "/", "" }, produces = { "application/json" }, method = RequestMethod.GET)
	public List<Trip> getAll() {
		return repository.findAll();
	}

	@ApiOperation(value = "Retrieve the list of trips by status", nickname = "getTripsByStatus", notes = "", response = Trip.class, responseContainer = "List", tags = { "trips", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Successfully retrieved List of Trips", response = Trip.class, responseContainer = "List"),
			@ApiResponse(code = 400, message = "Invalid status supplied") })
	@RequestMapping(value = "/status/{status}", produces = { "application/json" }, method = RequestMethod.GET)
	public List<Trip> getByStatus(
			@ApiParam(value = "Id of trip that needs to be retrieved", required = true) @PathVariable TripStatus status) {
		return repository.findByStatus(status);
	}

}
