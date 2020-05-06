package hr.fer.repository;

import hr.fer.model.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author lucija on 27/12/2019
 */
public interface MovieRepository extends MongoRepository<Movie, String> {

	boolean existsByTitleAndReleaseDate(String title, String releaseDate);

	boolean existsByMovieDBid(int movieDbId);

	Movie findByMovieDBid(int movieDbId);
}
