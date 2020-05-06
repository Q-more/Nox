package hr.fer.service.mapper;

import hr.fer.model.Crew;
import hr.fer.model.Crew.Cast;
import hr.fer.model.Crew.Director;
import hr.fer.model.Movie;
import hr.fer.model.Trailer;
import hr.fer.model.api.CreditsDto;
import hr.fer.model.api.MovieDetails;
import hr.fer.model.api.VideoDto;
import hr.fer.service.ImagePathCreator;
import info.movito.themoviedbapi.model.Credits;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Video;
import info.movito.themoviedbapi.model.core.NamedIdElement;
import info.movito.themoviedbapi.model.people.PersonCast;
import info.movito.themoviedbapi.model.people.PersonCrew;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Component;

/**
 * @author lucija on 05/01/2020
 */
@Slf4j
@Component
public class MovieMapperImpl implements MovieMapper, MovieDetailsMapper {
	private final String DIRECTOR_JOB = "Director";

	public Movie map(MovieDb movieDb, List<Video> videos, Credits credits) {
		log.info("mapping {} to movie", movieDb.toString());
		Crew crew = credits == null ? null : mapCredits(credits);
		return Movie.builder()
			.movieDBid(movieDb.getId())
			.imageURL(ImagePathCreator.getPath(movieDb.getPosterPath()))
			.overview(movieDb.getOverview())
			.title(movieDb.getTitle())
			.popularity(movieDb.getPopularity())
			.releaseDate(movieDb.getReleaseDate())
			.budget(movieDb.getBudget())
			.crew(crew)
			.runtimeInMinutes(movieDb.getRuntime())
			.genres(mapGenres(movieDb.getGenres()))
			.trailers(mapVideos(videos))
			.synopsis(movieDb.getOverview())
			.build();
	}

	public MovieDetails map(Movie movie) {
		if (movie == null) {
			return null;
		}
		log.info("mapping {} to movieDetails", movie.toString());
		CreditsDto creditsDto = movie.getCrew() == null ? null : CreditsDto.map(movie.getCrew());
		return MovieDetails.builder()
			.credits(creditsDto)
			.genres(movie.getGenres())
			.movieDBid(movie.getMovieDBid())
			.posterPath(movie.getImageURL())
			.releaseDate(movie.getReleaseDate())
			.runtimeInMinutes(movie.getRuntimeInMinutes())
			.score(movie.getScore())
			.synopsis(movie.getSynopsis())
			.title(movie.getTitle())
			.videos(mapTrailers(movie.getTrailers()))
			.build();
	}

	private List<VideoDto> mapTrailers(List<Trailer> trailers) {
		return trailers == null ? null : StreamEx.of(trailers)
			.map(VideoDto::map)
			.toList();
	}

	private List<Trailer> mapVideos(List<Video> videos) {
		return videos == null ?
			null :
			StreamEx.of(videos)
				.map(this::mapVideo)
				.toList();
	}

	private Trailer mapVideo(Video v) {
		return v == null ?
			null :
			Trailer.builder()
				.id(v.getId())
				.key(v.getKey())
				.site(v.getSite())
				.type(v.getType())
				.videoName(v.getName())
				.build();
	}

	private Crew mapCredits(Credits credits) {
		if (credits == null) {
			return null;
		}

		PersonCrew personCrew = StreamEx.of(credits.getCrew())
			.filter(c -> c.getJob().equals(DIRECTOR_JOB))
			.findFirst()
			.orElse(null);
		Director director = personCrew == null ? null : mapDirector(personCrew);

		List<Cast> casts = StreamEx.of(credits.getCast())
			.map(this::mapCast)
			.toList();

		return new Crew(director, casts);
	}

	private Director mapDirector(PersonCrew personCrew) {
		return personCrew == null ? null :
			Director.builder()
				.actorImagePath(ImagePathCreator.getPath(personCrew.getProfilePath()))
				.id(personCrew.getId())
				.job(personCrew.getJob())
				.name(personCrew.getName())
				.build();
	}

	private Cast mapCast(PersonCast personCast) {
		return personCast == null ?
			null :
			Cast.builder()
				.actor(personCast.getName())
				.actorImagePath(ImagePathCreator.getPath(personCast.getProfilePath()))
				.characterName(personCast.getCharacter())
				.id(personCast.getId())
				.build();
	}

	private List<String> mapGenres(List<Genre> genres) {
		return genres == null ?
			null :
			StreamEx.of(genres)
				.map(NamedIdElement::getName)
				.toList();
	}
}
