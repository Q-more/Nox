package hr.fer.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "likedUsers")
@CompoundIndex(
        def = "{'userId': 1, 'likedUserId':1}",
        name = "compound_index_2",
        unique = true
)
public class LikedUser {

    @Id
    private String id;
    private final String userId;
    private final String likedUserId;
}
