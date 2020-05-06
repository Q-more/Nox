package hr.fer.model.api;

import lombok.Value;

/**
 * @author lucija on 01/12/2019
 */
@Value
public class MovieShort {
	private final int id;
	private final String movieTitle;
	private final String url;
}
