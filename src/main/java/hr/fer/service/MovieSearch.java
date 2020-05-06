package hr.fer.service;

import hr.fer.model.Movie;
import hr.fer.service.mapper.MovieMapper;
import hr.fer.utils.DateParser;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.Credits;
import info.movito.themoviedbapi.model.Video;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lucija on 01/10/2019
 */

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MovieSearch {
	private final MovieMapper movieMapper;
	private final TmdbSearch search;
	private final ScoreProvider scoreProvider;
	private final MovieCollector movieCollector;
	private final TmdbMovies tmdbMovies;

	public List<Movie> findMovie(String name) {
		MovieResultsPage movieDbs = search.searchMovie(name, null, null, true, 1);
		return movieDbs.getResults().stream()
			.filter(Objects::nonNull)
			.map(m -> {
				Credits credits = tmdbMovies.getCredits(m.getId());
				List<Video> videos = tmdbMovies.getVideos(m.getId(), m.getOriginalLanguage());
				Movie movie = movieMapper.map(m, videos, credits);
				String releaseDate = movie.getReleaseDate();
				if (releaseDate != null && !releaseDate.isEmpty()) {
					movie.setScore(scoreProvider.getScoresForMovie(movie.getTitle(), DateParser.getYear(releaseDate)));
				}
				movieCollector.updateOrSave(movie);
				return movie;
			})
			.collect(Collectors.toList());
	}
}
