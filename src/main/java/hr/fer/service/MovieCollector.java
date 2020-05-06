package hr.fer.service;

import hr.fer.model.Movie;
import hr.fer.repository.MovieRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lucija on 27/12/2019
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MovieCollector {
	private final MovieRepository movieRepository;

	public void updateOrSave(Movie movie) {
		Movie existingMovie = movieRepository.findByMovieDBid(movie.getMovieDBid());
		if (existingMovie != null) {
			movie.setId(existingMovie.getId());
		}

		movieRepository.save(movie);
	}

	public void updateOrSave(List<Movie> movies) {
		StreamEx.of(movies).forEach(this::updateOrSave);
	}
}
