package controllers;
import java.util.List;

import api.ApiCallback;
import model.Place;
import repository.EnrichmentRepository;
import repository.PlaceRepository;

public class PlaceController {
    private final PlaceRepository places = new PlaceRepository();
    private final EnrichmentRepository enrichments = new EnrichmentRepository();

    public void getPlaces(String tripId, ApiCallback<List<Place>> callback) {
        places.getPlaces(tripId, callback);
    }

    public void getPlace(String placeId, ApiCallback<Place> callback) {
        places.getPlace(placeId, callback);
    }

    public void update(Place place, ApiCallback<Place> callback) {
        places.update(place, callback);
    }

    public void delete(String placeId, ApiCallback<Void> callback) {
        places.delete(placeId, callback);
    }

    public void enrich(Place place, ApiCallback<Place> callback) {
        enrichments.enrich(place, callback);
    }

    public void createEnriched(Place place, ApiCallback<Place> callback) {
        enrich(place, new ApiCallback<Place>() {
            @Override
            public void onSuccess(Place data) {
                places.create(data, callback);
            }

            @Override
            public void onError(String message) {
                places.create(place, callback);
            }
        });
    }

    public void refreshEnrichment(Place place, ApiCallback<Place> callback) {
        enrich(place, new ApiCallback<Place>() {
            @Override
            public void onSuccess(Place data) {
                places.updateEnrichment(data, callback);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }
}
