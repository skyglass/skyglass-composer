package nl.jsprengers.weather.station;

import nl.jsprengers.weather.api.Hygrometer;
import nl.jsprengers.weather.api.TemperatureUnit;
import nl.jsprengers.weather.api.Thermometer;
import nl.jsprengers.weather.api.WeatherReportDTO;
import nl.jsprengers.weather.api.WeatherStation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherStationEndpoint implements WeatherStation {

    @Autowired
    Thermometer thermometer;

    @Autowired
    Hygrometer hygrometer;

    @RequestMapping(value = "weather", method = RequestMethod.GET)
    public WeatherReportDTO getWeatherReport() {
        return new WeatherReportDTO(thermometer.getTemperatureInCelsius(), TemperatureUnit.C, hygrometer.getHumidityPercentage());
    }
}
