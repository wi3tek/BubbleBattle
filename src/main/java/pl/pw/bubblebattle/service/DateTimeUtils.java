package pl.pw.bubblebattle.service;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateTimeUtils {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "hh:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
            String.format( "%s %s",
                    DATE_PATTERN,
                    TIME_PATTERN )
    );
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public static String getCurrentDateTime() {
        return LocalDateTime.now().format( DATE_TIME_FORMATTER );
    }

    public static String getCurrentDate() {
        return LocalDate.now().format( DATE_FORMATTER );
    }

    public static LocalDateTime getCurrentDateTime(String dateTime) {
        return LocalDateTime.parse( dateTime,DATE_TIME_FORMATTER );
    }

    public static LocalDate getCurrentDate(String dateTime) {
        return LocalDate.parse( dateTime,DATE_FORMATTER );
    }

}
