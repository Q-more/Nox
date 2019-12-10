package hr.fer.api;

import hr.fer.model.Movie;
import hr.fer.service.MovieSearch;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lucija on 01/10/2019
 */
@Slf4j
@RestController
@RequestMapping("/movies")
public class MovieSearchController {
	private final MovieSearch movieSearch;

	@Autowired
	public MovieSearchController(MovieSearch movieSearch) {
		this.movieSearch = movieSearch;
	}

	@GetMapping("/search")
	public List<Movie> findMovie(@RequestParam String query) {
		log.debug("Got search request {}", query);
		return movieSearch.findMovie(query);
	}
}
