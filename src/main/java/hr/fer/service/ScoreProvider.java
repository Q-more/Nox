package hr.fer.service;

import com.omertron.omdbapi.OmdbApi;
import com.omertron.omdbapi.model.OmdbVideoFull;
import com.omertron.omdbapi.tools.OmdbBuilder;
import com.omertron.omdbapi.tools.OmdbParameters;
import hr.fer.model.Score;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

/**
 * @author lucija on 01/12/2019
 */
@Component
public class ScoreProvider {
	private final OmdbApi omdbApi;

	public ScoreProvider(OmdbApi omdbApi) {
		this.omdbApi = omdbApi;
	}

	@SneakyThrows
	public Score getScoresForMovie(String title, int year) {
	  try {
      OmdbVideoFull result = omdbApi.getInfo(new OmdbBuilder()
          .setTitle(title)
          .setYear(year)
          .setTomatoes(true)
          .build()
      );

      return new Score(result.getImdbRating(), result.getTomatoRotten(),String.valueOf(result.getMetascore()));
    } catch (Exception ex) {
      System.out.println("Not found for " + title);
      return new Score("N/A", "N/A", "N/A");
    }
	}
}
