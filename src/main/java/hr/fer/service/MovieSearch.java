package hr.fer.service;

import hr.fer.model.Movie;
import hr.fer.utils.DateParser;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lucija on 01/10/2019
 */
@Component
public class MovieSearch {
	private final TmdbSearch search;
	private final ScoreProvider scoreProvider;

	@Autowired
	public MovieSearch(TmdbSearch search, ScoreProvider scoreProvider) {
		this.search = search;
		this.scoreProvider = scoreProvider;
	}

	public List<Movie> findMovie(String name) {
		MovieResultsPage movieDbs = search.searchMovie(name, null, null, true, 1);
		return movieDbs.getResults().stream().map(m -> {
				Movie movie = Movie.builder()
					.popularity(m.getPopularity())
					.title(m.getTitle())
					.releaseDate(m.getReleaseDate())
					.build();
				movie.setScore(scoreProvider.getScoresForMovie(movie.getTitle(), DateParser.getYear(movie.getReleaseDate())));
				return movie;
			}
		).collect(Collectors.toList());
	}
}
