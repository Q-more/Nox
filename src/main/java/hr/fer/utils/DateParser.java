package hr.fer.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.experimental.UtilityClass;

/**
 * @author lucija on 01/12/2019
 */
@UtilityClass
public class DateParser {

	public static int getYear(String date) {
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy", Locale.ENGLISH);
		LocalDate localDate = LocalDate.parse(date, inputFormatter);
		return Integer.parseInt(outputFormatter.format(localDate));
	}
}
