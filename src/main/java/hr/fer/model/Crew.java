package hr.fer.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * @author lucija on 05/01/2020
 */
@Data
public class Crew {
	private final Director director;
	private final List<Cast> cast;

	@Data
	@Builder
	public static class Cast {
		private final int id;
		private final String characterName;
		private final String actor;
		private final String actorImagePath;
	}

	@Data
	@Builder
	public static class Director {
		private final int id;
		private final String job;
		private final String name;
		private final String actorImagePath;
	}
}
