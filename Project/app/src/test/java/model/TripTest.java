package model;

import org.junit.Test;
import exceptions.InvalidDataException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class TripTest {

    @Test
    public void createsValidTrip() {
        Trip trip = new Trip("Porto e Lisboa", "Duas cidades", "2026-11-04", "2026-11-09", "user-1");
        assertEquals("Porto e Lisboa", trip.getTitle());
        assertEquals("Duas cidades", trip.getDescription());
        assertEquals("2026-11-04", trip.getStartDate());
        assertEquals("2026-11-09", trip.getEndDate());
        assertEquals("user-1", trip.getOwnerId());
        assertEquals(6, trip.getDurationInDays());
    }

    @Test
    public void oneDayTripCountsAsOneDay() {
        Trip trip = new Trip("Um dia", "", "2026-11-04", "2026-11-04", "user-1");
        assertEquals(1, trip.getDurationInDays());
    }

    @Test(expected = InvalidDataException.class)
    public void rejectsShortTitle() {
        new Trip("ab", "", "2026-11-04", "2026-11-09", "user-1");
    }

    @Test(expected = InvalidDataException.class)
    public void rejectsInvalidStartDate() {
        new Trip("Viagem", "", "2026-02-30", "2026-03-02", "user-1");
    }

    @Test(expected = InvalidDataException.class)
    public void rejectsEndDateBeforeStartDate() {
        new Trip("Viagem", "", "2026-11-09", "2026-11-04", "user-1");
    }

    @Test(expected = InvalidDataException.class)
    public void rejectsMissingOwner() {
        new Trip("Viagem", "", "2026-11-04", "2026-11-09", "");
    }

    @Test
    public void copyKeepsAllFieldsAndIsIndependent() {
        Trip original = new Trip("Viagem", "Descrição", "2026-11-04", "2026-11-09", "user-1");
        original.setId("trip-1");

        Trip copy = original.copy();

        assertNotSame(original, copy);
        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getTitle(), copy.getTitle());
        assertEquals(original.getDescription(), copy.getDescription());
        assertEquals(original.getStartDate(), copy.getStartDate());
        assertEquals(original.getEndDate(), copy.getEndDate());
        assertEquals(original.getOwnerId(), copy.getOwnerId());

        copy.setTitle("Outra viagem");
        assertEquals("Viagem", original.getTitle());
    }

    @Test
    public void hasIdOnlyWhenIdIsNotBlank() {
        Trip trip = new Trip("Viagem", "", "2026-11-04", "2026-11-09", "user-1");
        assertTrue(!trip.hasId());
        trip.setId("trip-1");
        assertTrue(trip.hasId());
    }
}
