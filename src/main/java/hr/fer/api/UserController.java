package hr.fer.api;

import hr.fer.exceptions.ResourceNotFoundException;
import hr.fer.model.User;
import hr.fer.repository.UserRepository;
import hr.fer.security.CurrentUser;
import hr.fer.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

/**
 * @author lucija on 01/10/2019
 */
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

  private final UserRepository userRepository;

  @GetMapping("/user/me")
  @PreAuthorize("hasRole('USER')")
  public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
    return userRepository.findById(userPrincipal.getId())
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
  }
}
