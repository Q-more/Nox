package hr.fer.configuration;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbDiscover;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbSearch;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lucija on 01/10/2019
 */
@Configuration
@ConfigurationProperties(prefix = "movie-db")
public class MovieDBConfiguration {

	@Setter
	private String key;

	@Bean
	public TmdbSearch tmdbSearch() {
		return new TmdbApi(key).getSearch();
	}

	@Bean
	public TmdbMovies tmdbMovies() {
		return new TmdbApi(key).getMovies();
	}

	@Bean
	public TmdbDiscover tmdbDiscover() {
		return new TmdbApi(key).getDiscover();
	}
}