package hr.fer.configuration;

import com.omertron.omdbapi.OmdbApi;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lucija on 01/12/2019
 */
@Configuration
@ConfigurationProperties(prefix = "om-db")
public class OmdbConfiguration {
	@Setter
	private String key;

	@Bean
	public OmdbApi getOmdbApi() {
		return new OmdbApi(key);
	}
}
