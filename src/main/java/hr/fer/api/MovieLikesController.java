package hr.fer.api;

import hr.fer.model.api.MovieShort;
import hr.fer.model.Like;
import hr.fer.repository.LikeRepository;
import hr.fer.security.CurrentUser;
import hr.fer.security.UserPrincipal;
import hr.fer.service.MovieProvider;
import java.util.List;
import java.util.Optional;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

/**
 * @author lucija on 01/12/2019
 */
@RestController
@RequestMapping("/like")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class MovieLikesController {

	private final LikeRepository likeRepository;
	private final MovieProvider movieProvider;

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
		likeRepository.deleteByUserIdAndMovieId(userId, movieId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/movies")
	public ResponseEntity<List<MovieShort>> getLikedMoviesByMe(@CurrentUser UserPrincipal userPrincipal) {
		String userId = userPrincipal.getId();
		List<MovieShort> movies = getImageInfos(userId);
		return ResponseEntity.ok(movies);
	}

	private List<MovieShort> getImageInfos(String userId) {
		List<Like> likedMovies = likeRepository.findAllByUserId(userId);
		return StreamEx.of(likedMovies)
			.map(Like::getMovieId)
			.map(movieProvider::getMovie)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.map(movie -> new MovieShort(movie.getMovieDBid(), movie.getTitle(), movie.getImageURL()))
			.toList();
	}

	@GetMapping("/movies/{userId}")
	public ResponseEntity<List<MovieShort>> getLikedMoviesByUser(@PathVariable String userId) {
		List<MovieShort> movies = getImageInfos(userId);
		return ResponseEntity.ok(movies);
	}
}
