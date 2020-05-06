package hr.fer.model;

import java.util.*;

public class Similarity {
	private String UserId;
	private String likedUserId;
	private List<Integer> union;
	private List<Integer> intersection;
	private List<Integer> likedUserMoviesWithoutIntersection;
	private double similarity;

	public Similarity(String userId, String likedUserId, List<Integer> myMovies, List<Integer> likedUserMovies) {
		this.UserId = userId;
		this.likedUserId = likedUserId;
		union = UnionOfTwoLists(myMovies, likedUserMovies);
		this.intersection = IntersectionOfTwoLists(myMovies, likedUserMovies);
		similarity = union.isEmpty() ? 0 : intersection.size() / (double) union.size();
	}

	public double getSimilarity() {
		return this.similarity;
	}

	public String getLikedUserId() {
		return this.likedUserId;
	}

	public List<Integer> getLikedUserMoviesWithoutIntersection() {
		return this.likedUserMoviesWithoutIntersection;
	}

	public void setLikedUserMoviesWithoutUnion(List<Integer> likedUserMovies) {
		likedUserMoviesWithoutIntersection = ListWithoutIntersection(likedUserMovies, this.intersection);
	}

	public List<Integer> UnionOfTwoLists(List<Integer> myList, List<Integer> likedUserList) {
		Set<Integer> set = new HashSet<>();
		set.addAll(myList);
		set.addAll(likedUserList);
		return new ArrayList<>(set);
	}

	public List<Integer> IntersectionOfTwoLists(List<Integer> myList, List<Integer> likedUserList) {
		List<Integer> list = new ArrayList<>();

		for (Integer movieId : myList) {
			if (likedUserList.contains(movieId)) {
				list.add(movieId);
			}
		}
		return list;
	}

	public List<Integer> ListWithoutIntersection(List<Integer> firstList, List<Integer> intersectList) {
		List<Integer> list = new ArrayList<>();

		for (Integer movieId : firstList) {
			if (!intersectList.contains(movieId)) {
				list.add(movieId);
			}
		}
		return list;
	}
}



