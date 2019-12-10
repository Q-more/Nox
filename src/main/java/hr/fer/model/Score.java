package hr.fer.model;

import lombok.Value;

/**
 * @author lucija on 01/12/2019
 */
@Value
public class Score {
	private final String imdb;
	private final String rotten;
	private final String metascore;
}
