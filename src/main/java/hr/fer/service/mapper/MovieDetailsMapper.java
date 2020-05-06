package hr.fer.service.mapper;

import hr.fer.model.Movie;
import hr.fer.model.api.MovieDetails;

/**
 * @author lucija on 05/01/2020
 */
public interface MovieDetailsMapper {
	MovieDetails map(Movie movie);
}
