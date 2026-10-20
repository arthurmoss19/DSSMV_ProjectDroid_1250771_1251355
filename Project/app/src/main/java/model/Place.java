package model;

import com.google.gson.annotations.SerializedName;
import exceptions.InvalidDataException;
public class Place {
    public static final String NO_COUNTRY = "";

    @SerializedName("_id")
    private String id;
    private String tripId;
    private String categoryId;
    private String name;

    private int rating;
    private double latitude;
    private double longitude;
    private String address;
    private String country;

    private String countryCode;
    private String visitedAt;
    public Place() {
    }
    public Place(String tripId, String name, double latitude, double longitude) {
        setTripId(tripId);
        setName(name);
        setLatitude(latitude);
        setLongitude(longitude);
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTripId() {
        return tripId;
    }
    public void setTripId(String tripId) {
        if (tripId == null || tripId.trim().isEmpty()) {
            throw new InvalidDataException("Place: tripId = NULL");
        }
        this.tripId = tripId;
    }
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String categoryId) {
        if (categoryId == null || categoryId.trim().isEmpty()) {
            throw new InvalidDataException("Place: categoryId = NULL");
        }
        this.categoryId = categoryId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Place: nome vazio");
        }
        this.name = name.trim();
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new InvalidDataException("Place: rating = " + rating
                    + " (tem de estar entre 1 e 5)");
        }
        this.rating = rating;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        if (latitude < -90 || latitude > 90) {
            throw new InvalidDataException("Place: latitude = " + latitude);
        }
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        if (longitude < -180 || longitude > 180) {
            throw new InvalidDataException("Place: longitude = " + longitude);
        }
        this.longitude = longitude;
    }
    public String getAddress() {
        return address == null ? "" : address;
    }
    public void setAddress(String address) {
        this.address = address == null ? "" : address.trim();
    }
    public String getCountry() {
        return country == null ? NO_COUNTRY : country;
    }
    public void setCountry(String country) {
        this.country = country == null ? NO_COUNTRY : country.trim();
    }
    public String getCountryCode() {
        return countryCode == null ? "" : countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode == null ? "" : countryCode.trim();
    }
    public String getVisitedAt() {
        return visitedAt;
    }
    public void setVisitedAt(String visitedAt) {
        this.visitedAt = visitedAt;
    }
    public boolean hasId() {
        return id != null && !id.trim().isEmpty();
    }

    public boolean isEnriched() {
        return !getAddress().isEmpty() || !getCountryCode().isEmpty() || !getCountry().isEmpty();
    }

    public boolean isEqualTo(Place obj) {
        if (obj == null) {
            return false;
        }
        if (this.id != null && obj.id != null) {
            return this.id.equals(obj.id);
        }
        return name != null && name.equals(obj.name);
    }
    @Override
    public String toString() {
        return name + " (" + String.format(java.util.Locale.US, "%.4f, %.4f", latitude, longitude) + ")";
    }
}
