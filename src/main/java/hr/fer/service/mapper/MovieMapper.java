package hr.fer.service.mapper;

import hr.fer.model.Movie;
import info.movito.themoviedbapi.model.Credits;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Video;
import java.util.List;

/**
 * @author lucija on 05/01/2020
 */
public interface MovieMapper {
	Movie map(MovieDb movieDb, List<Video> videos, Credits credits);
}
