package nl.jsprengers.weather.station;

import nl.jsprengers.weather.api.WeatherReportDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        WeatherStationApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "temperature=15.3", "humidity=50"})
public class WeatherStationEndpointIntegrationTest {

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testTemperature() {
        WeatherReportDTO weatherReportDTO = restTemplate.getForObject("http://localhost:8091/weather", WeatherReportDTO.class);
        assertThat(weatherReportDTO.getTemperature()).isEqualTo(15.3);
        assertThat(weatherReportDTO.getHumidity()).isEqualTo(50);
    }

}
