package hr.fer.model.api;

import hr.fer.model.Score;
import java.util.List;
import lombok.Builder;
import lombok.Value;


/**
 * @author lucija on 05/01/2020
 */
@Value
@Builder
public class MovieDetails {
	private final int movieDBid;
	private final String title;
	private final String synopsis;
	private final String releaseDate;
	private final List<String> genres;
	private final Integer runtimeInMinutes;
	private final String posterPath;
	private final List<VideoDto> videos;
	private final CreditsDto credits;
	private final Score score;
}
