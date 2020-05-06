package hr.fer.service;

import hr.fer.model.api.MovieShort;
import hr.fer.model.Movie;
import hr.fer.model.Score;
import hr.fer.repository.MovieRepository;
import hr.fer.service.mapper.MovieMapper;
import hr.fer.utils.DateParser;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Credits;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Video;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.core.ResultsPage;
import info.movito.themoviedbapi.tools.MovieDbException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.SneakyThrows;

/**
 * @author lucija on 04/10/2019
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MovieProvider {

	private final TmdbMovies tmdbMovies;
	private final MovieRepository movieRepository;
	private final ScoreProvider scoreProvider;
	private final MovieCollector movieCollector;
	private final MovieMapper movieMapper;

	public List<MovieShort> getPopularMovies() {
		MovieResultsPage popularMovies = tmdbMovies.getPopularMovies(null, null);
		return extractImageInfos(popularMovies);
	}

	public List<MovieShort> getNewReleases() {
		MovieResultsPage newMovies = tmdbMovies.getNowPlayingMovies(null, null, null);
		return extractImageInfos(newMovies);
	}

	public List<MovieShort> getUpcoming() {
		MovieResultsPage upcoming = tmdbMovies.getUpcoming(null, null, null);
		return extractImageInfos(upcoming);
	}

	private List<MovieShort> extractImageInfos(MovieResultsPage upcoming) {
		List<Movie> movies = extractMovies(upcoming);
		return extractImageInfo(movies);
	}

	@SneakyThrows
	public Optional<Movie> getMovie(int id) {
		try {
			MovieDb movieDb = tmdbMovies.getMovie(id, null);
			if (movieDb == null) {
				return Optional.empty();
			}

			MovieExtra extra = getExtra(movieDb.getId(), movieDb.getOriginalLanguage());
			Movie movie = movieMapper.map(movieDb, extra.videos, extra.credits);
			Score scoresForMovie = scoreProvider.getScoresForMovie(movie.getTitle(), DateParser.getYear(movie.getReleaseDate()));
			movie.setScore(scoresForMovie);
			movieCollector.updateOrSave(movie);
			return Optional.of(movie);
		} catch (MovieDbException ex) {
			log.info("tmdb not available.");
		}

		Movie movie = movieRepository.findByMovieDBid(id);
		return movie == null ? Optional.empty() : Optional.of(movie);
	}

	private List<Movie> extractMovies(MovieResultsPage movies) {
		return StreamEx.of(movies)
			.map(ResultsPage::getResults)
			.flatMap(Collection::stream)
			.map(this::getMovie)
			.toList();
	}

	private Movie getMovie(MovieDb movieDb) {
		MovieExtra extra = getExtra(movieDb.getId(), movieDb.getOriginalLanguage());
		Movie movie = movieMapper.map(movieDb, extra.videos, extra.credits);
		Score scoresForMovie = scoreProvider.getScoresForMovie(movie.getTitle(), DateParser.getYear(movie.getReleaseDate()));
		movie.setScore(scoresForMovie);
		return movie;
	}

	private List<MovieShort> extractImageInfo(List<Movie> movies) {
		return StreamEx.of(movies)
			.map(movie -> new MovieShort(movie.getMovieDBid(), movie.getTitle(), movie.getImageURL()))
			.toList();
	}

	public List<Movie> getSimilarMovies(int movieId) {
		MovieResultsPage similarMovies = tmdbMovies.getSimilarMovies(movieId, null, null);
		return extractMovies(similarMovies);
	}

	private MovieExtra getExtra(int movieId, String originalLanguage) {
		List<Video> videos = tmdbMovies.getVideos(movieId, originalLanguage);
		Credits credits = tmdbMovies.getCredits(movieId);
		return new MovieExtra(videos, credits);
	}

	@Value
	static class MovieExtra {
		private final List<Video> videos;
		private final Credits credits;
	}
}
