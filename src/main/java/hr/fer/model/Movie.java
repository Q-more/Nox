package hr.fer.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author lucija on 01/12/2019
 */
@Data
@Builder
@Document(collection = "movies")
public class Movie {
	@Id
	private String id;
	private final int movieDBid;
	private final String title;
	private final float popularity;
	private final String synopsis;
	private String releaseDate;
	private long budget;
	private final String imageURL;
	private final String overview;
	private Score score;
	private final List<String> genres;
	private final Integer runtimeInMinutes;
	private final List<Trailer> trailers;
	private Crew crew;
}
