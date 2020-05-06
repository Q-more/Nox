package hr.fer.api;

import hr.fer.model.api.MovieShort;
import hr.fer.model.Movie;
import hr.fer.service.MovieSearch;
import hr.fer.service.TimeComplexityAnalyzer;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
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
	private final TimeComplexityAnalyzer timeComplexityAnalyzer;

	@Autowired
	public MovieSearchController(MovieSearch movieSearch, TimeComplexityAnalyzer timeComplexityAnalyzer) {
		this.movieSearch = movieSearch;
		this.timeComplexityAnalyzer = timeComplexityAnalyzer;
	}

	@GetMapping("/search")
	public List<MovieShort> findMovie(@RequestParam String query) {
		timeComplexityAnalyzer.startTime();
		List<Movie> movie = movieSearch.findMovie(query);
		timeComplexityAnalyzer.stopTime();
		log.info("Searching for query => {} for {} s", query, timeComplexityAnalyzer.getSeconds());
		return StreamEx.of(movie)
			.map(m -> new MovieShort(m.getMovieDBid(), m.getTitle(), m.getImageURL()))
			.toList();
	}
}
