package hr.fer;

import hr.fer.configuration.AppConfiguration;
import hr.fer.configuration.MovieDBConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author lucija on 01/10/2019
 */
@SpringBootApplication
@EnableConfigurationProperties({
	MovieDBConfiguration.class,
	AppConfiguration.class
})
public class Nox {

	public static void main(String[] args) {
		SpringApplication.run(Nox.class, args);
	}

}