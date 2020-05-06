package hr.fer.repository;


import hr.fer.model.LikedUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LikedUsersRepository extends MongoRepository<LikedUser, String> {
    List<LikedUser> findAllByUserId(String userId);
}
