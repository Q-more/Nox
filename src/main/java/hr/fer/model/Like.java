package hr.fer.model;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author lucija on 01/12/2019
 */
@Data
@Document(collection = "likes")
@CompoundIndex(
	def = "{'userId': 1, 'movieId':1}",
	name = "compound_index",
	unique = true
)
public class Like {

	@Id
	private String id;
	private final String userId;
	private final int movieId;
}
