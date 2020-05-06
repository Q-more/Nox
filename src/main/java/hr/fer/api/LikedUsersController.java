package hr.fer.api;

import hr.fer.model.LikedUser;
import hr.fer.model.User;
import hr.fer.repository.LikedUsersRepository;
import hr.fer.repository.UserRepository;
import hr.fer.security.CurrentUser;
import hr.fer.security.UserPrincipal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("likedUsers")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class LikedUsersController {

	private final LikedUsersRepository likedUsersRepository;
	private final UserRepository userRepository;

	@GetMapping
	public ResponseEntity<List<User>> getLikedUsers(@CurrentUser UserPrincipal userPrincipal) {
		String userId = userPrincipal.getId();
		List<LikedUser> likedUsers = likedUsersRepository.findAllByUserId(userId);
		List<User> users = StreamEx.of(likedUsers)
			.map(LikedUser::getLikedUserId)
			.map(userRepository::findById)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.toList();
		return ResponseEntity.ok(users);
	}

	@PostMapping("/{likedUserId}")
	public ResponseEntity<String> like(
		@CurrentUser UserPrincipal userPrincipal,
		@PathVariable(name = "likedUserId") String likedUserId
	) {
		String userId = userPrincipal.getId();

		if (userId.equals(likedUserId)) {
			return ResponseEntity.badRequest().body("User can not like himself.");
		}

		LikedUser likedUser = new LikedUser(userId, likedUserId);
		try {
			likedUsersRepository.insert(likedUser);
			return ResponseEntity.ok().build();
		} catch (Exception ex) {
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping("/{likedUserId}")
	public ResponseEntity<Void> unlike(
		@CurrentUser UserPrincipal userPrincipal,
		@PathVariable(name = "likedUserId") String likedUserId
	) {
		String userId = userPrincipal.getId();
		likedUsersRepository.delete(new LikedUser(userId, likedUserId));
		return ResponseEntity.ok().build();
	}
}
