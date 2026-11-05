package util;

import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;
import model.Place;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GeoUtilsTest {

    @Test
    public void distanceBetweenLisbonAndPortoIsAbout274Km() {
        double distance = GeoUtils.distanceKm(38.7223, -9.1393, 41.1579, -8.6291);
        assertEquals(274.0, distance, 5.0);
    }

    @Test
    public void distanceFromPointToItselfIsZero() {
        assertEquals(0.0, GeoUtils.distanceKm(38.7223, -9.1393, 38.7223, -9.1393), 0.0001);
    }

    @Test
    public void totalDistanceSumsConsecutivePlaces() {
        Place lisbon = new Place("trip-1", "Lisboa", 38.7223, -9.1393);
        Place porto = new Place("trip-1", "Porto", 41.1579, -8.6291);

        double total = GeoUtils.totalDistanceKm(Arrays.asList(lisbon, porto));

        assertEquals(GeoUtils.distanceKm(38.7223, -9.1393, 41.1579, -8.6291), total, 0.0001);
    }

    @Test
    public void totalDistanceIsZeroWithFewerThanTwoPlaces() {
        assertEquals(0.0, GeoUtils.totalDistanceKm(null), 0.0001);
        assertEquals(0.0, GeoUtils.totalDistanceKm(Collections.emptyList()), 0.0001);
        assertEquals(0.0, GeoUtils.totalDistanceKm(Collections.singletonList(
                new Place("trip-1", "Lisboa", 38.7223, -9.1393))), 0.0001);
    }

    @Test
    public void formatDistanceUsesMetersBelowOneKilometre() {
        String formatted = GeoUtils.formatDistance(0.5);
        assertTrue(formatted.endsWith(" m"));
    }

    @Test
    public void formatDistanceUsesKilometresFromOneKilometre() {
        String formatted = GeoUtils.formatDistance(2.5);
        assertTrue(formatted.endsWith(" km"));
    }

    @Test
    public void toApiQueryUsesDotAndFiveDecimals() {
        assertEquals("38.72230,-9.13930", GeoUtils.toApiQuery(38.7223, -9.1393));
    }
}