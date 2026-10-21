package util;

import java.util.List;
import java.util.Locale;

import model.Place;
public final class GeoUtils {
    private static final double EARTH_RADIUS_KM = 6371.0;
    private GeoUtils() {
    }
    public static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
    public static double totalDistanceKm(List<Place> places) {
        if (places == null || places.size() < 2) {
            return 0;
        }
        double total = 0;
        for (int i = 1; i < places.size(); i++) {
            Place previous = places.get(i - 1);
            Place current = places.get(i);
            total += distanceKm(previous.getLatitude(), previous.getLongitude(),
                    current.getLatitude(), current.getLongitude());
        }
        return total;
    }
    public static String formatDistance(double km) {
        if (km < 1) {
            return String.format(Locale.getDefault(), "%.0f m", km * 1000);
        }
        return String.format(Locale.getDefault(), "%.1f km", km);
    }
    public static String toApiQuery(double latitude, double longitude) {
        return String.format(Locale.US, "%.5f,%.5f", latitude, longitude);
    }
    public static String formatCoordinates(double latitude, double longitude) {
        return String.format(Locale.US, "%.5f, %.5f", latitude, longitude);
    }
}
