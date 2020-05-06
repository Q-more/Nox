package hr.fer.api;

import hr.fer.model.User;
import hr.fer.repository.UserRepository;
import static java.util.stream.Collectors.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserSearchController {
	private final UserRepository userRepository;

	@GetMapping("/search")
	public List<User> findUser(@RequestParam String query) {
		return userRepository.findAll()
			.stream()
			.filter(user -> matchesUser(query, user))
			.collect(toList());
	}

	private boolean matchesUser(@RequestParam String query, User user) {
		log.info("Query = {}, user = {}", query, user);
		return user.getEmail().contains(query) || user.getFirstName().contains(query)
			|| user.getLastName().contains(query);
	}
}
