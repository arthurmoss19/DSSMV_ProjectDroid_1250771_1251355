package controllers;

import java.util.List;

import api.ApiCallback;
import model.Trip;
import repository.TripRepository;

public class TripController {
    private final TripRepository trips = new TripRepository();

    public void getTrips(String ownerId, ApiCallback<List<Trip>> callback) {
        trips.getTrips(ownerId, callback);
    }

    public void getTrip(String tripId, ApiCallback<Trip> callback) {
        trips.getTrip(tripId, callback);
    }

    public void delete(String tripId, ApiCallback<Void> callback) {
        trips.delete(tripId, callback);
    }

    public void save(Trip trip, ApiCallback<Trip> callback) {
        if (trip.hasId()) {
            trips.update(trip, callback);
        } else {
            trips.create(trip, callback);
        }
    }
}
