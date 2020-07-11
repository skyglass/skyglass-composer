package pl.piomin.services.trip;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.DigestUtils;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.LazyFuture;

import pl.piomin.services.trip.client.api.TripsApi;
import pl.piomin.services.trip.client.invoker.ApiClient;
import pl.piomin.services.trip.client.model.Trip;
import pl.piomin.services.trip.client.model.TripInput;

@Testcontainers
public class TripIntegrationTests {

	private static TripsApi tripsApi;

	private static final Future<String> IMAGE_FUTURE = new LazyFuture<String>() {

		@Override
		protected String resolve() {
			// Find project's root dir
			File cwd;
			for (cwd = new File("."); !new File(cwd, "pom.xml").isFile(); cwd = cwd.getParentFile()) {

			}

			// Make it unique per folder (for caching)
			String imageName = String.format("local/app-%s:%s", DigestUtils.md5DigestAsHex(cwd.getAbsolutePath().getBytes()), System.currentTimeMillis());

			// Make it unique per folder (for caching)
			Properties properties = new Properties();
			properties.put("spring-boot.build-image.name", imageName);
			// Avoid recursion :D
			properties.put("skipTests", "true");

			InvocationRequest request = new DefaultInvocationRequest().setPomFile(new File(cwd, "pom.xml")).setGoals(Arrays.asList("spring-boot:build-image")).setProperties(properties);

			try {
				InvocationResult invocationResult = new DefaultInvoker().execute(request);
				if (invocationResult.getExitCode() != 0) {
					throw new RuntimeException(invocationResult.getExecutionException());
				}
			} catch (MavenInvocationException e) {
				throw new RuntimeException(e);
			}

			return imageName;
		}
	};

	@Container
	static final GenericContainer<?> APP = new GenericContainer<>(IMAGE_FUTURE)
			.withNetwork(Network.SHARED)
			.withExposedPorts(8080)
			// Forward logs
			.withLogConsumer(new ToStringConsumer() {
				@Override
				public void accept(OutputFrame outputFrame) {
					if (outputFrame.getBytes() != null) {
						try {
							System.out.write(outputFrame.getBytes());
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				}
			});

	@Before
	public void setUp() {
		APP.start();
		String endpoint = String.format(
				"http://%s:%d/",
				APP.getContainerIpAddress(),
				APP.getFirstMappedPort());
		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath(endpoint);
		tripsApi = new TripsApi(apiClient);
	}

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
