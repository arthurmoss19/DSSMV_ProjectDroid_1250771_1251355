package repository;

import com.triptrail.BuildConfig;
import api.ApiCallback;
import api.ApiClient;
import api.dto.GeocodingResponse;
import model.Place;
import util.GeoUtils;
import retrofit2.Call;

public class EnrichmentRepository extends BaseRepository {
    public void enrich(Place place, ApiCallback<Place> callback) {
        loadAddress(place, callback);
    }

    private void loadAddress(Place place, ApiCallback<Place> callback) {
        String coordinates = GeoUtils.toApiQuery(place.getLatitude(), place.getLongitude());
        Call<GeocodingResponse> call = ApiClient.geocoding()
                .reverse(BuildConfig.POSITIONSTACK_KEY, coordinates, 1);

        enqueue(call, new ApiCallback<GeocodingResponse>() {
            @Override
            public void onSuccess(GeocodingResponse response) {
                if (response != null && !response.hasError()) {
                    GeocodingResponse.Result result = response.first();
                    if (result != null) {
                        place.setAddress(result.toAddress());
                        place.setCountry(result.getCountry());
                        place.setCountryCode(result.getCountryCode());
                    }
                }
                callback.onSuccess(place);
            }

            @Override
            public void onError(String message) {
                callback.onSuccess(place);
            }
        });
    }
}
