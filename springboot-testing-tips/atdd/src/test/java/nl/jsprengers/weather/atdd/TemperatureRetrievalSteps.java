package nl.jsprengers.weather.atdd;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.data.Offset;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nl.jsprengers.weather.api.WeatherReportDTO;

public class TemperatureRetrievalSteps {
	private RestTemplate client = new RestTemplate();

	private ResponseEntity<WeatherReportDTO> response = null;

	@When("^I get the temperature for postal code (\\d+) in (celsius|fahrenheit)")
	public void getTemperature(String postCode, String unit) {
		String url = String
				.format("http://localhost:%d/weather?postCode=%s&unit=%s", ServerEnvironment.SERVER_PORT, postCode, getUnitCode(unit));
		response = client
				.getForEntity(url, WeatherReportDTO.class);
	}

	@Then("^the temperature is (.*?) in (celsius|fahrenheit)$")
	public void theTemperatureIs(double temperature, String unit) {
		assertCorrectResponse("Did not retrieve temperature: ");
		assertThat(response.getBody().getTemperature()).as("temperature").isEqualTo(temperature, Offset.offset(0.1));
		assertThat(response.getBody().getUnit().name()).as("temperature unit").isEqualTo(getUnitCode(unit));
	}

	@Then("^the humidity is (\\d+) per cent$")
	public void theHumidityIs(int humidity) {
		assertCorrectResponse("Did not retrieve humidity: ");
		assertThat(response.getBody().getHumidity()).as("humidity").isEqualTo(humidity);
	}

	private void assertCorrectResponse(String errorMessage) {
		if (response.getStatusCode() == null || !response.getStatusCode().is2xxSuccessful()) {
			throw new IllegalStateException(errorMessage);
		}
		assertThat(response).as("response is null").isNotNull();
		assertThat(response.getStatusCode().is2xxSuccessful()).as("Statuscode is not 200").isTrue();
	}

	private String getUnitCode(String unit) {
		return "fahrenheit".equalsIgnoreCase(unit) ? "F" : "C";
	}

}
