package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class DateUtils {
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DISPLAY_DATE = "dd/MM/yyyy";
    private static final String DISPLAY_DATETIME = "dd/MM/yyyy HH:mm";

    private DateUtils() {}

    public static boolean isValidDate(String value) {
        return parse(value, DATE_PATTERN) != null;
    }

    public static boolean isRangeValid(String start, String end) {
        Date from = parse(start, DATE_PATTERN);
        Date to = parse(end, DATE_PATTERN);
        if (from == null || to == null) {
            return false;
        }
        return !to.before(from);
    }

    public static Date parseDate(String value) {
        return parse(value, DATE_PATTERN);
    }

    public static Date parseDateTime(String value) {
        Date parsed = parse(value, DATETIME_PATTERN);
        if (parsed == null) {
            // Aceita uma data sem hora (o RestDB pode devolver só o dia).
            parsed = parse(value, DATE_PATTERN);
        }
        return parsed;
    }

    private static Date parse(String value, String pattern) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
        format.setLenient(false);
        try {
            return format.parse(value.trim());
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(DISPLAY_DATE, Locale.getDefault()).format(date);
    }

    public static String today() {
        return new SimpleDateFormat(DATE_PATTERN, Locale.US).format(new Date());
    }

    public static String now() {
        return new SimpleDateFormat(DATETIME_PATTERN, Locale.US).format(new Date());
    }

    public static String formatDisplayDate(String iso) {
        Date date = parseDate(iso);
        return date == null ? (iso == null ? "" : iso) : formatDate(date);
    }

    public static String formatDisplayDateTime(String iso) {
        Date date = parseDateTime(iso);
        if (date == null) {
            return iso == null ? "" : iso;
        }
        return new SimpleDateFormat(DISPLAY_DATETIME, Locale.getDefault()).format(date);
    }

    public static String formatRange(String start, String end) {
        return formatDisplayDate(start) + " - " + formatDisplayDate(end);
    }

    public static long daysBetween(String start, String end) {
        Date from = parseDate(start);
        Date to = parseDate(end);
        if (from == null || to == null) {
            return 0;
        }
        long millis = to.getTime() - from.getTime();
        return TimeUnit.MILLISECONDS.toDays(millis);
    }

    public static String todayPlus(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return new SimpleDateFormat(DATE_PATTERN, Locale.US).format(calendar.getTime());
    }

    public static String nowPlus(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return new SimpleDateFormat(DATETIME_PATTERN, Locale.US).format(calendar.getTime());
    }
}
