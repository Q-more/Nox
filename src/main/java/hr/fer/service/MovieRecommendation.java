package hr.fer.service;

import hr.fer.model.Like;
import hr.fer.model.Movie;
import hr.fer.model.Similarity;
import hr.fer.model.User;
import hr.fer.model.api.MovieDetails;
import hr.fer.model.api.MovieShort;
import hr.fer.repository.LikeRepository;
import hr.fer.repository.MovieRepository;
import hr.fer.repository.UserRepository;
import hr.fer.service.mapper.MovieDetailsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MovieRecommendation {

	private final UserRepository userRepository;
	private final LikeRepository likeRepository;
	private final MovieDetailsMapper movieDetailsMapper;
	private final MovieRepository movieRepository;

	public List<MovieShort> getRecommendation(String userId) {
		List<User> users = userRepository.findAll().stream()
			.filter(user -> !user.getId().equals(userId))
			.collect(Collectors.toList());

		List<Similarity> similarities = new ArrayList<>();
		List<Integer> myMovieList = likeRepository.findAllByUserId(userId)
			.stream()
			.map(Like::getMovieId)
			.collect(Collectors.toList());

		for (User user : users) {
			List<Integer> otherUserMovieList = likeRepository.findAllByUserId(user.getId())
				.stream()
				.map(Like::getMovieId)
				.collect(Collectors.toList());
			Similarity similarity = new Similarity(userId, user.getId(), myMovieList, otherUserMovieList);
			similarity.setLikedUserMoviesWithoutUnion(otherUserMovieList);
			similarities.add(similarity);

		}

		similarities = similarities.stream().sorted((sim1, sim2) -> -Double.compare(sim1.getSimilarity(), sim2.getSimilarity())).collect(Collectors.toList());

		List<Integer> moviesLikedByOtherUsers = similarities.stream()
			.map(Similarity::getLikedUserMoviesWithoutIntersection)
			.flatMap(Collection::stream)
			.distinct()
			.collect(Collectors.toList());

		return StreamEx.of(moviesLikedByOtherUsers)
			.map(movieRepository::findByMovieDBid)
			.nonNull()
			.map(movie -> new MovieShort(movie.getMovieDBid(), movie.getTitle(), movie.getImageURL()))
			.toList();

	}

	private List<MovieShort> extractImageInfo(List<Movie> movies) {
		return StreamEx.of(movies)
			.map(movie -> new MovieShort(movie.getMovieDBid(), movie.getTitle(), movie.getImageURL()))
			.toList();
	}
}
