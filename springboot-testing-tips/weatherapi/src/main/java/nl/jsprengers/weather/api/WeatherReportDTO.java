package nl.jsprengers.weather.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherReportDTO {
	private double temperature;

	private TemperatureUnit unit;

	private int humidity;
}
