package util;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DateUtilsTest {

    @Test
    public void acceptsValidIsoDate() {
        assertTrue(DateUtils.isValidDate("22-10-2026"));
    }

    @Test
    public void rejectsInvalidDates() {
        assertFalse(DateUtils.isValidDate("30-02-2026"));
        assertFalse(DateUtils.isValidDate("22/10/2026"));
        assertFalse(DateUtils.isValidDate(""));
        assertFalse(DateUtils.isValidDate(null));
    }

    @Test
    public void daysBetweenCountsCalendarDays() {
        assertEquals(4L, DateUtils.daysBetween("01-10-2026", "05-10-2026"));
        assertEquals(0L, DateUtils.daysBetween("01-10-2026", "01-10-2026"));
        assertEquals(0L, DateUtils.daysBetween(null, "01-10-2026"));
    }

    @Test
    public void formatsIsoDateForDisplay() {
        assertEquals("05/10/2026", DateUtils.formatDisplayDate("2026-10-05"));
    }

    @Test
    public void keepsUnknownValueWhenItCannotBeParsed() {
        assertEquals("data-invalida", DateUtils.formatDisplayDate("data-invalida"));
        assertEquals("", DateUtils.formatDisplayDate(null));
    }

    @Test
    public void formatsRangeWithHyphen() {
        assertEquals("01/10/2026 - 05/10/2026",
                DateUtils.formatRange("01-10-2026", "05-10-2026"));
    }

    @Test
    public void validatesRangeOrder() {
        assertTrue(DateUtils.isRangeValid("01-10-2026", "05-10-2026"));
        assertTrue(DateUtils.isRangeValid("05-10-2026", "05-10-2026"));
        assertFalse(DateUtils.isRangeValid("05-10-2026", "01-10-2026"));
        assertFalse(DateUtils.isRangeValid(null, "01-10-2026"));
    }

    @Test
    public void todayUsesIsoFormat() {
        assertTrue(DateUtils.today().matches("\\d{4}-\\d{2}-\\d{2}"));
    }
}

