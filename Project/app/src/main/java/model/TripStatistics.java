package model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import util.GeoUtils;
public class TripStatistics {
    private final int placeCount;
    private final int countryCount;
    private final double totalDistanceKm;
    private final double averageRating;
    private final int dayCount;

    public TripStatistics(List<Place> places, Trip trip) {
        this.placeCount = places == null ? 0 : places.size();
        this.totalDistanceKm = GeoUtils.totalDistanceKm(places);
        this.dayCount = trip == null ? 0 : trip.getDurationInDays();

        Set<String> countries = new HashSet<>();
        int ratingSum = 0;
        int ratingCount = 0;
        if (places != null) {
            for (Place place : places) {
                String code = place.getCountryCode();
                if (code != null && !code.trim().isEmpty()) {
                    countries.add(code.trim().toUpperCase());
                }
                if (place.getRating() >= 1) {
                    ratingSum += place.getRating();
                    ratingCount++;
                }
            }
        }
        this.countryCount = countries.size();
        this.averageRating = ratingCount == 0 ? 0 : (double) ratingSum / ratingCount;
    }

    public int getPlaceCount() {
        return placeCount;
    }
    public int getCountryCount() {
        return countryCount;
    }
    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }
    public double getAverageRating() {
        return averageRating;
    }
    public int getDayCount() {
        return dayCount;
    }

    public boolean isEmpty() {
        return placeCount == 0;
    }
}