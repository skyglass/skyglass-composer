package nl.jsprengers.weather.server;

import nl.jsprengers.weather.api.TemperatureUnit;
import nl.jsprengers.weather.api.WeatherReportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherStationRestClient implements WeatherStationClient {

    private RestTemplate restTemplate = new RestTemplate();
    Logger log = LoggerFactory.getLogger(WeatherStationRestClient.class);

    @Value("${weather.host.1000-3000}")
    private String host1000_3000;
    @Value("${weather.host.3001-6000}")
    private String host3001_6000;
    @Value("${weather.host.6001-9999}")
    private String host6001_9999;

    public WeatherReportDTO getTemperatureByPostalCode(int postCode, String unit) {
        String urlForPostCode = getUrlForPostCode(postCode);
        log.info("Getting report for postcode {}", urlForPostCode);
        ResponseEntity<WeatherReportDTO> entity = restTemplate.getForEntity(urlForPostCode, WeatherReportDTO.class);
        if (!entity.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Unable to parse ");
        }
        return createOutput(unit, entity.getBody());
    }

    private WeatherReportDTO createOutput(String unit, WeatherReportDTO weatherReportDTO) {
        TemperatureUnit temperatureUnit = TemperatureUnit.valueOf(unit);
        if (temperatureUnit == TemperatureUnit.F) {
            double fahrenheit = (weatherReportDTO.getTemperature() * 1.8) + 32;
            return new WeatherReportDTO(fahrenheit, TemperatureUnit.F, weatherReportDTO.getHumidity());
        } else {
            return weatherReportDTO;
        }
    }

    private String getUrlForPostCode(int postCode) {
        if (postCode > 1000 && postCode < 3000) {
            return host1000_3000;
        } else if (postCode < 6000) {
            return host3001_6000;
        } else if (postCode < 9999) {
            return host6001_9999;
        } else {
            throw new IllegalArgumentException("Invalid postcode");
        }
    }
}
