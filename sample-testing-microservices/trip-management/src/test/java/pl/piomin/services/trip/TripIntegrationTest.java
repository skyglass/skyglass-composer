package pl.piomin.services.trip;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import pl.piomin.services.trip.client.api.TripsApi;
import pl.piomin.services.trip.client.model.Trip;
import pl.piomin.services.trip.client.model.TripInput;
import pl.piomin.services.trip.config.TestJpaConfig;
import pl.piomin.services.trip.config.TripManagementAppConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { TripManagementAppConfig.class,
		TestJpaConfig.class })
public class TripIntegrationTest {

	private static TripsApi tripsApi;

	@Test
	public void test1CreateNewTrip() throws Exception {
		TripInput ti = new TripInput();
		ti.setDestination("test");
		ti.setLocationX(10);
		ti.setLocationY(20);
		ti.setUsername("walker");
		Trip result = tripsApi.createTrip(ti);
		Assert.assertNotNull(result.getId());
		Assert.assertEquals(result.getStatus(), Trip.StatusEnum.NEW);

		result = tripsApi.cancelTrip(result.getId());
		Assert.assertEquals(result.getStatus(), Trip.StatusEnum.REJECTED);

		result = tripsApi.payTrip(result.getId());
		Assert.assertEquals(result.getStatus(), Trip.StatusEnum.PAYED);
	}

}
