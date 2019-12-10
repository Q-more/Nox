package hr.fer.service;

import hr.fer.model.ImageInfo;
import hr.fer.model.Movie;
import hr.fer.model.Score;
import hr.fer.utils.DateParser;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.core.ResultsPage;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.SneakyThrows;

/**
 * @author lucija on 04/10/2019
 */
@Component
public class MovieProvider {

  private final TmdbMovies tmdbMovies;
  private final ScoreProvider scoreProvider;

  @Autowired
  public MovieProvider(TmdbMovies tmdbMovies, ScoreProvider scoreProvider) {
    this.tmdbMovies = tmdbMovies;
    this.scoreProvider = scoreProvider;
  }

  public List<Movie> getPopularMovies() {
    MovieResultsPage popularMovies = tmdbMovies.getPopularMovies(null, null);
    //return extractImageInfos(popularMovies);
    return extractMovies(popularMovies);
  }

  public List<Movie> getNewReleases() {
    MovieResultsPage newMovies = tmdbMovies.getNowPlayingMovies(null, null, null);
    return extractMovies(newMovies);
    //return extractImageInfos(newMovies);
  }

  public List<Movie> getUpcoming() {
    MovieResultsPage upcoming = tmdbMovies.getUpcoming(null, null, null);
    //return extractImageInfos(upcoming);
    return extractMovies(upcoming);
  }

  private List<ImageInfo> extractImageInfos(MovieResultsPage upcoming) {
    List<Movie> movies = extractMovies(upcoming);
    return extractImageInfo(movies);
  }

  @SneakyThrows
  public Optional<Movie> getMovie(int id) {
    MovieDb movieDb = tmdbMovies.getMovie(id, null);
    if (movieDb == null) {
      return Optional.empty();
    }
    Movie movie = Movie.getMovie(movieDb);
    Score scoresForMovie = scoreProvider.getScoresForMovie(movie.getTitle(), DateParser.getYear(movie.getReleaseDate()));
    movie.setScore(scoresForMovie);
    return Optional.of(movie);
  }

  private List<Movie> extractMovies(MovieResultsPage movies) {
    return StreamEx.of(movies)
        .map(ResultsPage::getResults)
        .flatMap(Collection::stream)
        .map(this::getMovie)
        .toList();
  }

  private Movie getMovie(MovieDb movieDb) {
    Movie movie = Movie.getMovie(movieDb);
    Score scoresForMovie = scoreProvider.getScoresForMovie(movie.getTitle(), DateParser.getYear(movie.getReleaseDate()));
    movie.setScore(scoresForMovie);
    return movie;
  }

  private List<ImageInfo> extractImageInfo(List<Movie> movies) {
    return StreamEx.of(movies)
        .map(movie -> new ImageInfo(movie.getImageURL(), movie.getMovieDBid()))
        .toList();
  }

  public List<Movie> getSimilarMovies(int movieId) {
    MovieResultsPage similarMovies = tmdbMovies.getSimilarMovies(movieId, null, null);
    return extractMovies(similarMovies);
  }
}
