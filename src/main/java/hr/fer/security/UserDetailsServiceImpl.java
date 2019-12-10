package hr.fer.security;

import hr.fer.exceptions.ResourceNotFoundException;
import hr.fer.model.User;
import hr.fer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

/**
 * @author lucija on 01/10/2019
 */
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email)
      throws UsernameNotFoundException {
    User user = userRepository
        .findByEmail(email)
        .orElseThrow(() ->  new UsernameNotFoundException("User not found with email : " + email));

    return UserPrincipal.create(user);
  }

  public UserDetails loadUserById(String id) {
    User user = userRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    return UserPrincipal.create(user);
  }
}
