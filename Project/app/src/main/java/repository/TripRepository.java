package repository;

import java.util.List;

import api.ApiCallback;
import api.ApiClient;
import model.Trip;
import retrofit2.Call;

public class TripRepository extends BaseRepository {
    public void getTrips(String ownerId, ApiCallback<List<Trip>> callback) {
        Call<List<Trip>> call = ApiClient.restDb()
                .getTrips(filter("ownerId", ownerId), orderBy("startDate", false));
        enqueue(call, callback);
    }

    public void getTrip(String tripId, ApiCallback<Trip> callback) {
        Call<Trip> call = ApiClient.restDb().getTrip(tripId);
        enqueue(call, callback);
    }

    public void create(Trip trip, ApiCallback<Trip> callback) {
        Call<Trip> call = ApiClient.restDb().createTrip(trip);
        enqueue(call, callback);
    }

    public void update(Trip trip, ApiCallback<Trip> callback) {
        Call<Trip> call = ApiClient.restDb().updateTrip(trip.getId(), trip);
        enqueue(call, callback);
    }

    public void delete(String tripId, ApiCallback<Void> callback) {
        Call<okhttp3.ResponseBody> call = ApiClient.restDb().deleteTrip(tripId);
        enqueueVoid(call, callback);
    }
}
