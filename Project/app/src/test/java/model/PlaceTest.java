package model;

import org.junit.Test;
import exceptions.InvalidDataException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PlaceTest {

    @Test
    public void createsValidPlace() {
        Place place = new Place("trip-1", "Praia da Rocha", 37.1167, -8.5333);

        assertEquals("trip-1", place.getTripId());
        assertEquals("Praia da Rocha", place.getName());
        assertEquals(37.1167, place.getLatitude(), 0.0001);
        assertEquals(-8.5333, place.getLongitude(), 0.0001);
        assertEquals("", place.getAddress());
        assertEquals("", place.getCountry());
        assertFalse(place.isEnriched());
    }

    @Test
    public void acceptsRatingBoundaries() {
        Place place = new Place("trip-1", "Local", 38.0, -9.0);
        place.setRating(1);
        assertEquals(1.0, place.getRating(), 0.0001);
        place.setRating(5);
        assertEquals(5.0, place.getRating(), 0.0001);
    }

    @Test(expected = InvalidDataException.class)
    public void rejectsRatingZero() {
        new Place("trip-1", "Local", 38.0, -9.0).setRating(0);
    }

    @Test(expected = InvalidDataException.class)
    public void rejectsRatingAboveFive() {
        new Place("trip-1", "Local", 38.0, -9.0).setRating(6);
    }

    @Test
    public void acceptsCoordinateBoundaries() {
        Place place = new Place("trip-1", "Local", 90.0, 180.0);
        assertEquals(90.0, place.getLatitude(), 0.0001);
        assertEquals(180.0, place.getLongitude(), 0.0001);

        place.setLatitude(-90.0);
        place.setLongitude(-180.0);
        assertEquals(-90.0, place.getLatitude(), 0.0001);
        assertEquals(-180.0, place.getLongitude(), 0.0001);
    }

    @Test(expected = InvalidDataException.class)
    public void rejectsLatitudeBelowMinusNinety() {
        new Place("trip-1", "Local", 38.0, -9.0).setLatitude(-91.0);
    }

    @Test(expected = InvalidDataException.class)
    public void rejectsLatitudeAboveNinety() {
        new Place("trip-1", "Local", 38.0, -9.0).setLatitude(91.0);
    }

    @Test(expected = InvalidDataException.class)
    public void rejectsLongitudeBelowMinusOneEighty() {
        new Place("trip-1", "Local", 38.0, -9.0).setLongitude(-181.0);
    }

    @Test(expected = InvalidDataException.class)
    public void rejectsLongitudeAboveOneEighty() {
        new Place("trip-1", "Local", 38.0, -9.0).setLongitude(181.0);
    }

    @Test(expected = InvalidDataException.class)
    public void rejectsEmptyTripId() {
        new Place("", "Local", 38.0, -9.0);
    }

    @Test(expected = InvalidDataException.class)
    public void rejectsEmptyName() {
        new Place("trip-1", "   ", 38.0, -9.0);
    }

    @Test(expected = InvalidDataException.class)
    public void rejectsEmptyCategoryId() {
        new Place("trip-1", "Local", 38.0, -9.0).setCategoryId("");
    }

    @Test
    public void isEnrichedAfterAddressOrCountry() {
        Place place = new Place("trip-1", "Local", 38.0, -9.0);
        place.setAddress("Rua de Exemplo, 1");
        assertTrue(place.isEnriched());
    }
}