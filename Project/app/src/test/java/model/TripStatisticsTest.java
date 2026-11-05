package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;

public class TripStatisticsTest {

    @Test
    public void calculatesPlacesCountriesDistanceAndRating() {
        Trip trip = new Trip("Teste", "", "04-11-2026", "05-11-2026", "user-1");

        Place lisbon = new Place("trip-1", "Lisboa", 38.7223, -9.1393);
        lisbon.setCategoryId("cat-1");
        lisbon.setRating(5);
        lisbon.setCountryCode("PT");

        Place porto = new Place("trip-1", "Porto", 41.1579, -8.6291);
        porto.setCategoryId("cat-1");
        porto.setRating(3);
        porto.setCountryCode("PT");

        TripStatistics statistics = new TripStatistics(Arrays.asList(lisbon, porto), trip);

        assertEquals(2, statistics.getPlaceCount());
        assertEquals(1, statistics.getCountryCount());
        assertEquals(3, statistics.getDayCount());
        assertEquals(4.0, statistics.getAverageRating(), 0.01);
        assertTrue(statistics.getTotalDistanceKm() > 200);
        assertFalse(statistics.isEmpty());
    }

    @Test
    public void emptyTripHasZeroValues() {
        Trip trip = new Trip("Vazia", "", "04-11-2026", "05-11-2026", "user-1");
        TripStatistics statistics = new TripStatistics(Collections.emptyList(), trip);

        assertEquals(0, statistics.getPlaceCount());
        assertEquals(0, statistics.getCountryCount());
        assertEquals(0.0, statistics.getTotalDistanceKm(), 0.01);
        assertEquals(0.0, statistics.getAverageRating(), 0.01);
        assertEquals(1, statistics.getDayCount());
        assertTrue(statistics.isEmpty());
    }
}
