package hr.fer.model.api;

import hr.fer.model.Trailer;
import lombok.Builder;
import lombok.Value;

/**
 * @author lucija on 05/01/2020
 */
@Value
@Builder
public class VideoDto {
	private final String id;
	private final String key;
	private final String videoName;
	private final String site;
	private final String type;

	public static VideoDto map(Trailer video) {
		return video == null ? null : VideoDto.builder()
			.id(video.getId())
			.key(video.getKey())
			.site(video.getSite())
			.videoName(video.getVideoName())
			.type(video.getType())
			.build();
	}
}
