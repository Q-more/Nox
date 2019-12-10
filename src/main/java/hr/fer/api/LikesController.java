package hr.fer.api;


import hr.fer.model.Like;
import hr.fer.repository.LikeRepository;
import hr.fer.repository.UserRepository;
import hr.fer.security.CurrentUser;
import hr.fer.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

/**
 * @author lucija on 01/12/2019
 */
@RestController
@RequestMapping("/likes")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class LikesController {

  private final LikeRepository likeRepository;
  private final UserRepository userRepository;

  @PostMapping("/{movieId}")
  public ResponseEntity<Void> save(
      @CurrentUser UserPrincipal userPrincipal,
      @PathVariable(name = "movieId") int movieId
  ) {
    String userId = userPrincipal.getId();
    Like like = new Like(userId, movieId);
    try {
      likeRepository.insert(like);
      return ResponseEntity.ok().build();
    } catch (Exception ex) {
      return ResponseEntity.badRequest().build();
    }
  }

  @DeleteMapping("/{movieId}")
  public ResponseEntity<Void> unlike(
      @CurrentUser UserPrincipal userPrincipal,
      @PathVariable(name = "movieId") int movieId
  ) {
    String userId = userPrincipal.getId();
    likeRepository.delete(new Like(userId, movieId));
    return ResponseEntity.ok().build();
  }
}
