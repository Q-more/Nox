package hr.fer.model.api;

import hr.fer.model.Crew;
import hr.fer.model.Crew.Cast;
import hr.fer.model.Crew.Director;
import java.util.List;
import lombok.Value;
import one.util.streamex.StreamEx;

/**
 * @author lucija on 05/01/2020
 */
@Value
public class CreditsDto {

	private final List<CastDto> cast;
	private final DirectorDto directorDto;

	public static CreditsDto map(Crew crew) {
		if (crew == null) {
			return null;
		}
		List<CastDto> cast = StreamEx.of(crew.getCast())
			.map(CastDto::map)
			.toList();
		return new CreditsDto(cast, DirectorDto.map(crew.getDirector()));
	}

	@Value
	public static class CastDto {
		private final int id;
		private final String characterName;
		private final String actor;
		private final String actorImagePath;

		public static CastDto map(Cast cast) {
			return cast == null ?
				null :
				new CastDto(cast.getId(), cast.getCharacterName(), cast.getActor(), cast.getActorImagePath());
		}
	}

	@Value
	public static class DirectorDto {
		private final int id;
		private final String job;
		private final String name;
		private final String actorImagePath;

		public static DirectorDto map(Director director) {
			return new DirectorDto(director.getId(), director.getJob(), director.getName(), director.getActorImagePath());
		}
	}
}
