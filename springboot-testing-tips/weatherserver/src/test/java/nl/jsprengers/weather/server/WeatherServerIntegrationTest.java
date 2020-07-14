package nl.jsprengers.weather.server;

import nl.jsprengers.weather.api.WeatherReportDTO;
import org.assertj.core.data.Offset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        WeatherServerApplication.class,
        NorthWeatherStation.class, SouthWeatherStation.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "weather.host.1000-3000=http://localhost:8090/north/weather",
        "weather.host.3001-6000=http://localhost:8090/south/weather",
        "weather.host.6001-9999=http://localhost:8090/south/weather"})
public class WeatherServerIntegrationTest {

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testTemperature() {
        assertWeatherForPostcode("2999", -3.2, "C", 30);
        assertWeatherForPostcode("3001", 18.5, "C", 70);
        assertWeatherForPostcode("5999", 18.5, "C", 70);
        assertWeatherForPostcode("6002", 18.5, "C", 70);
    }

    @Test
    public void testTemperatureFahrenheit() {
        assertWeatherForPostcode("2999", 26.24, "F", 30);
        assertWeatherForPostcode("3001", 65.3, "F", 70);
        assertWeatherForPostcode("5999", 65.3, "F", 70);
        assertWeatherForPostcode("6002", 65.3, "F", 70);
    }

    private void assertWeatherForPostcode(String postcode, double temperature, String unit, int humidity) {
        String url = String.format("http://localhost:8090/weather?postCode=%s&unit=%s", postcode, unit);
        WeatherReportDTO temperatureObject = restTemplate.getForObject(url, WeatherReportDTO.class);
        assertThat(temperatureObject.getTemperature()).isEqualTo(temperature, Offset.offset(0.1));
        assertThat(temperatureObject.getUnit().name()).isEqualTo(unit);
        assertThat(temperatureObject.getHumidity()).isEqualTo(humidity);
    }

}
