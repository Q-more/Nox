package hr.fer.service;

/**
 * @author lucija on 05/01/2020
 */
public class ImagePathCreator {
	public static String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/original";

	public static String getPath(String name) {
		return BASE_IMAGE_URL + name;
	}
}
