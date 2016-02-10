package stecker.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Provides convenience methods for working with Dates and Times.
 */
public abstract class DateTimeSupport {

    private static final String AGO_PATTERN = "%d %s ago";
    private static final String DAY = "day";
    private static final String DAYS = "days";
    private static final String HOUR = "hour";
    private static final String HOURS = "hours";
    private static final String MINUTE = "minute";
    private static final String MINUTES = "minutes";
    private static final String SECOND = "second";
    private static final String SECONDS = "seconds";

    /**
     * Formats provided {@code Date} as follows:
     * 
     * <pre>
     * N day(s) N hour(s) N minute(s) N second(s) ago
     * </pre>
     * 
     * @param date date to be formatted
     * @return formatted result
     */
    public static String formatDateAsAgo(Date date) {

        try {
            long delta = new Date().getTime() - date.getTime();

            if (delta < 60000) {
                return doFormat(TimeUnit.MILLISECONDS.toSeconds(delta), SECOND, SECONDS);
            }

            if (delta < 3600000) {
                return doFormat(TimeUnit.MILLISECONDS.toMinutes(delta), MINUTE, MINUTES);
            }

            if (delta < 86400000) {
                return doFormat(TimeUnit.MILLISECONDS.toHours(delta), HOUR, HOURS);
            }

            return doFormat(TimeUnit.MILLISECONDS.toDays(delta), DAY, DAYS);

        }
        catch (Exception e) {
            throw new RuntimeException(
                    String.format(
                            "An error occurred when attempting to parse provided ISO8601 DateTime. Provided: \'%s\', iso8601DateTime",
                            e));
        }
    }

    private static String doFormat(long nbr, String singleLabel, String multipleLabel) {
        return (nbr == 1) ? String.format(AGO_PATTERN, nbr, singleLabel) : String.format(
                AGO_PATTERN, nbr, multipleLabel);
    }

}
