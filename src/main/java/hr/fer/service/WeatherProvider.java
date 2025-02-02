package hr.fer.service;

import hr.fer.model.Movie;
import hr.fer.model.Score;
import hr.fer.service.mapper.MovieMapper;
import hr.fer.utils.DateParser;
import info.movito.themoviedbapi.TmdbDiscover;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Credits;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Video;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.openweathermap.api.DataWeatherClient;
import org.openweathermap.api.common.Coordinate;
import org.openweathermap.api.model.Weather;
import org.openweathermap.api.model.currentweather.CurrentWeather;
import org.openweathermap.api.query.Language;
import org.openweathermap.api.query.QueryBuilderPicker;
import org.openweathermap.api.query.ResponseFormat;
import org.openweathermap.api.query.UnitFormat;
import org.openweathermap.api.query.currentweather.CurrentWeatherOneLocationQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lucija on 01/12/2019
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WeatherProvider {
	private final static Map<String, String> WEATHER_TO_GENRE = new HashMap<String, String>() {{
		put("Thunderstorm", "27"); // Horror
		put("Drizzle", "53"); //Thriller
		put("Rain", "18"); // Drama
		put("Snow", "10749"); // Romance
		put("Fog", "9648"); //Mystery
		put("Clear", "12"); //Adventure
		put("Clouds", "10751"); // Family
	}};

	private final TmdbDiscover tmdbDiscover;
	private final MovieMapper movieMapper;
	private final DataWeatherClient dataWeatherClient;
	private final ScoreProvider scoreProvider;
	private final TmdbMovies tmdbMovies;

	public List<Movie> findByWeather(String longitude, String latitude) {
		CurrentWeatherOneLocationQuery currentWeatherOneLocationQuery = QueryBuilderPicker.pick()
			.currentWeather()                   // get current weather
			.oneLocation()
			.byGeographicCoordinates(new Coordinate(longitude, latitude))// for one location
			.language(Language.ENGLISH)         // in English language
			.responseFormat(ResponseFormat.JSON)// with JSON response format
			.unitFormat(UnitFormat.METRIC)      // in metric units
			.build();
		CurrentWeather currentWeather = dataWeatherClient.getCurrentWeather(currentWeatherOneLocationQuery);
		List<String> genreBasedOnWeather = getGenresBasedOnWeather(currentWeather.getWeather());

		MovieResultsPage discoverByGenres = tmdbDiscover.getDiscover(
			1,
			null,
			null,
			false,
			2015,
			2015,
			0,
			0,
			String.join(",", genreBasedOnWeather),
			null,
			null,
			null,
			null,
			null
		);

		return extractMovies(discoverByGenres.getResults());
	}

	private List<String> getGenresBasedOnWeather(List<Weather> weather) {
		return StreamEx.of(weather)
			.map(Weather::getMain)
			.filter(WEATHER_TO_GENRE::containsKey)
			.map(WEATHER_TO_GENRE::get)
			.toList();
	}

	private List<Movie> extractMovies(List<MovieDb> movieDbs) {
		return StreamEx.of(movieDbs)
			.map(this::connectAll)
			.toList();
	}

	private Movie connectAll(MovieDb movieDb) {
		Credits credits = tmdbMovies.getCredits(movieDb.getId());
		List<Video> videos = tmdbMovies.getVideos(movieDb.getId(), movieDb.getOriginalLanguage());
		Movie movie = movieMapper.map(movieDb, videos, credits);
		Score scoresForMovie = scoreProvider.getScoresForMovie(movie.getTitle(), DateParser.getYear(movie.getReleaseDate()));
		movie.setScore(scoresForMovie);
		return movie;
	}
}
