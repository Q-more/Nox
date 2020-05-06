package hr.fer.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author lucija on 05/01/2020
 */
@Data
@Builder
public class Trailer {
	private final String id;
	private final String key;
	private final String videoName;
	private final String site;
	private final String type;
}
