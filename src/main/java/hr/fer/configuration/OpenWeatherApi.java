package hr.fer.configuration;

import lombok.Setter;
import org.openweathermap.api.DataWeatherClient;
import org.openweathermap.api.UrlConnectionDataWeatherClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lucija on 01/12/2019
 */
@Configuration
@ConfigurationProperties(prefix = "weather")
public class OpenWeatherApi {
	@Setter
	private String key;

	@Bean
	public DataWeatherClient getDataWeatherClient() {
		return new UrlConnectionDataWeatherClient(key);
	}
}
