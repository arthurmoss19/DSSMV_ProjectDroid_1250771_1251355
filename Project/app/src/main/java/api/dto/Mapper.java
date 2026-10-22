package api.dto;

import model.Photo;
import model.Place;

public final class Mapper {

    private Mapper() {
    }

    public static void applyAddress(Place place, GeocodingResponse.Result result) {
        if (place == null || result == null) {
            return;
        }
        place.setAddress(address(result));
        place.setCountry(result.getCountry());
        place.setCountryCode(result.getCountryCode());
    }

    public static String address(GeocodingResponse.Result result) {
        if (result == null) {
            return "";
        }
        if (result.getLabel() != null && !result.getLabel().isEmpty()) {
            return result.getLabel();
        }
        StringBuilder builder = new StringBuilder();
        if (result.getStreet() != null) {
            builder.append(result.getStreet());
        }
        if (result.getNumber() != null) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(result.getNumber());
        }
        if (result.getLocality() != null) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(result.getLocality());
        }
        return builder.toString();
    }

    public static Photo toPhoto(String placeId, String mediaId, String caption, String takenAt) {
        return new Photo(placeId, mediaId, caption, takenAt);
    }
}

