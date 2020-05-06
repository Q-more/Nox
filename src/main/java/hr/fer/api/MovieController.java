package hr.fer.api;

import hr.fer.model.api.MovieDetails;
import hr.fer.model.api.MovieShort;
import hr.fer.model.Movie;
import hr.fer.security.CurrentUser;
import hr.fer.security.UserPrincipal;
import hr.fer.service.MovieProvider;
import hr.fer.service.MovieRecommendation;
import hr.fer.service.MovieSearch;
import hr.fer.service.TimeComplexityAnalyzer;
import hr.fer.service.WeatherProvider;
import hr.fer.service.mapper.MovieDetailsMapper;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

/**
 * @author lucija on 01/12/2019
 */
@Slf4j
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MovieController {

	private final MovieProvider movieProvider;
	private final MovieDetailsMapper movieDetailsMapper;
	private final WeatherProvider weatherProvider;
	private final MovieRecommendation movieRecommendation;
	private final TimeComplexityAnalyzer timeComplexityAnalyzer;

	@GetMapping("/movies/recommended")
	public ResponseEntity<List<MovieShort>> getRecommended(@CurrentUser UserPrincipal userPrincipal) {
		timeComplexityAnalyzer.startTime();
		List<MovieShort> recommendation = movieRecommendation.getRecommendation(userPrincipal.getId());
		timeComplexityAnalyzer.stopTime();
		log.info("recommendation time = {}", timeComplexityAnalyzer.getSeconds());
		return ResponseEntity.ok(recommendation);

	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/movies/popular")
	public ResponseEntity<List<MovieShort>> getPopular() {
		timeComplexityAnalyzer.startTime();
		List<MovieShort> popularMovies = movieProvider.getPopularMovies();
		timeComplexityAnalyzer.stopTime();
		log.info("Popular movies time = {}", timeComplexityAnalyzer.getSeconds());

		return ResponseEntity.ok(popularMovies);
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/movies/new-releases")
	public ResponseEntity<List<MovieShort>> getNewReleases() {
		return ResponseEntity.ok(movieProvider.getNewReleases());
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/movies/upcoming")
	public ResponseEntity<List<MovieShort>> getUpcoming() {
		return ResponseEntity.ok(movieProvider.getUpcoming());
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/movies/weather")
	public ResponseEntity<List<MovieShort>> getMoviesByWeather(@RequestParam String longitude, @RequestParam String latitude) {
		timeComplexityAnalyzer.startTime();
		List<Movie> byWeather = weatherProvider.findByWeather(longitude, latitude);
		timeComplexityAnalyzer.stopTime();
		log.info("Weather recommendation time = {}", timeComplexityAnalyzer.getSeconds());
		return ResponseEntity.ok(getImageInfos(byWeather));
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/similar/{movieId}")
	public ResponseEntity<List<MovieShort>> getSimilarMovies(@PathVariable int movieId) {
		timeComplexityAnalyzer.startTime();
		List<Movie> similarMovies = movieProvider.getSimilarMovies(movieId);
		timeComplexityAnalyzer.stopTime();
		log.info("similar time = {}", timeComplexityAnalyzer.getSeconds());
		return ResponseEntity.ok(getImageInfos(similarMovies));
	}

	@GetMapping("/movies/{movieId}")
	public ResponseEntity<MovieDetails> getMovie(@PathVariable int movieId) {
		Optional<Movie> movie = movieProvider.getMovie(movieId);
		return movie.map(body -> {
			MovieDetails movieDetails = movieDetailsMapper.map(body);
			return ResponseEntity.ok(movieDetails);
		}).orElseGet(() -> ResponseEntity.badRequest().build());
	}

	public List<MovieShort> getImageInfos(List<Movie> movies) {
		return StreamEx.of(movies)
			.map(m -> new MovieShort(m.getMovieDBid(), m.getTitle(), m.getImageURL()))
			.toList();
	}

}
