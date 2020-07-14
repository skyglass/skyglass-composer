package nl.jsprengers.weather.server;

import nl.jsprengers.weather.api.WeatherReportDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("weather")
public class WeatherServerEndpoint {

    @Autowired
    WeatherStationRestClient restClient;

    @RequestMapping(method = RequestMethod.GET)
    public WeatherReportDTO getTemperatureByPostalCode(
            @RequestParam(value = "postCode") int postCode,
            @RequestParam(value = "unit", required = false) String unit) {
        return restClient.getTemperatureByPostalCode(postCode, unit);
    }

}
