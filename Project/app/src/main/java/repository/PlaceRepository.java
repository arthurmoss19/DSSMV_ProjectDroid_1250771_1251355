package repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import api.ApiCallback;
import api.ApiClient;
import model.Place;
import retrofit2.Call;

public class PlaceRepository extends BaseRepository {
    private final PhotoRepository photos = new PhotoRepository();

    public void getPlaces(String tripId, ApiCallback<List<Place>> callback) {
        Call<List<Place>> call = ApiClient.restDb().getPlaces(filter("tripId", tripId), orderBy("visitedAt", true));
        enqueue(call, callback);
    }

    public void getPlace(String placeId, ApiCallback<Place> callback) {
        Call<Place> call = ApiClient.restDb().getPlace(placeId);
        enqueue(call, callback);
    }

    public void create(Place place, ApiCallback<Place> callback) {
        Call<Place> call = ApiClient.restDb().createPlace(place);
        enqueue(call, callback);
    }

    public void update(Place place, ApiCallback<Place> callback) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("name", place.getName());
        fields.put("categoryId", place.getCategoryId());
        fields.put("rating", place.getRating());
        fields.put("visitedAt", place.getVisitedAt());

        Call<Place> call = ApiClient.restDb().updatePlace(place.getId(), fields);
        enqueue(call, callback);
    }

    public void updateEnrichment(Place place, ApiCallback<Place> callback) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("address", place.getAddress());
        fields.put("country", place.getCountry());
        fields.put("countryCode", place.getCountryCode());

        Call<Place> call = ApiClient.restDb().updatePlace(place.getId(), fields);
        enqueue(call, callback);
    }

    public void delete(String placeId, ApiCallback<Void> callback) {
        photos.deleteByPlace(placeId, new ApiCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                deletePlaceOnly(placeId, callback);
            }

            @Override
            public void onError(String message) {
                deletePlaceOnly(placeId, callback);
            }
        });
    }

    private void deletePlaceOnly(String placeId, ApiCallback<Void> callback) {
        Call<ResponseBody> call = ApiClient.restDb().deletePlace(placeId);
        enqueueVoid(call, callback);
    }
}
