package nl.jsprengers.weather.server;

import nl.jsprengers.weather.api.WeatherReportDTO;

public interface WeatherStationClient {
    WeatherReportDTO getTemperatureByPostalCode(int postCode, String unit);
}
