package hr.fer.model;

import info.movito.themoviedbapi.model.MovieDb;
import lombok.Builder;
import lombok.Data;

/**
 * @author lucija on 01/12/2019
 */
@Data
@Builder
public class Movie {
	private final int movieDBid;
	private final String title;
	private final float popularity;
	private String releaseDate;
	private long budget;
	private final String imageURL;
	private final String overview;
	private Score score;

	public static Movie getMovie(MovieDb movieDb) {
		return Movie.builder()
			.movieDBid(movieDb.getId())
			.imageURL("https://image.tmdb.org/t/p/original" + movieDb.getPosterPath())
			.overview(movieDb.getOverview())
			.title(movieDb.getTitle())
			.popularity(movieDb.getPopularity())
			.releaseDate(movieDb.getReleaseDate())
			.budget(movieDb.getBudget())
			.build();
	}
}
