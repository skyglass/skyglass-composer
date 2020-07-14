package nl.jsprengers.weather.station;

import nl.jsprengers.weather.api.Hygrometer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StubHygrometer implements Hygrometer {

    @Value("${humidity:80}")
    private int humidity;

    public int getHumidityPercentage() {
        return humidity;
    }
}
