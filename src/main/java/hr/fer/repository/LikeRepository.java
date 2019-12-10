package hr.fer.repository;

import hr.fer.model.Like;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author lucija on 01/12/2019
 */
public interface LikeRepository extends MongoRepository<Like, String> {
	List<Like> findAllByUserId(String userId);
}
