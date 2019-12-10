package hr.fer.api;

import hr.fer.model.Like;
import hr.fer.model.Movie;
import hr.fer.repository.LikeRepository;
import hr.fer.security.CurrentUser;
import hr.fer.security.UserPrincipal;
import hr.fer.service.MovieProvider;
import hr.fer.service.WeatherProvider;
import java.util.List;
import java.util.Optional;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

/**
 * @author lucija on 01/12/2019
 */
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MovieController {

	private final MovieProvider movieProvider;
	private final WeatherProvider weatherProvider;
	private final LikeRepository likeRepository;

	@GetMapping("/movies/liked")
	public ResponseEntity<List<Movie>> getLikedMovies(@CurrentUser UserPrincipal userPrincipal) {
		String userId = userPrincipal.getId();
		List<Like> likedMovies = likeRepository.findAllByUserId(userId);
		List<Movie> movies = StreamEx.of(likedMovies)
			.map(Like::getMovieId)
			.map(movieProvider::getMovie)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.toList();
		return ResponseEntity.ok(movies);
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/movies/popular")
	public ResponseEntity<List<Movie>> getPopular() {
		return ResponseEntity.ok(movieProvider.getPopularMovies());
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/movies/new-releases")
	public ResponseEntity<List<Movie>> getNewReleases() {
		return ResponseEntity.ok(movieProvider.getNewReleases());
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/movies/upcoming")
	public ResponseEntity<List<Movie>> getUpcoming() {
		return ResponseEntity.ok(movieProvider.getUpcoming());
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/movies/weather/{city}")
	public ResponseEntity<List<Movie>> getMoviesByWeather(@PathVariable String city) {
		List<Movie> byWeather = weatherProvider.findByWeather(city);
		return ResponseEntity.ok(byWeather);
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/similar/{movieId}")
	public ResponseEntity<List<Movie>> getSimilarMovies(@PathVariable int movieId) {
		List<Movie> similarMovies = movieProvider.getSimilarMovies(movieId);
		return ResponseEntity.ok(similarMovies);
	}

	@GetMapping("/movies/{movieId}")
	public ResponseEntity<Movie> getMovie(@PathVariable int movieId) {
		Optional<Movie> movie = movieProvider.getMovie(movieId);
		return movie.isPresent() ? ResponseEntity.ok(movie.get()) : ResponseEntity.badRequest().build();
	}
}
