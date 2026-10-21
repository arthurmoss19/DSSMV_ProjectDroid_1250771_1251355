package util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
public final class PermissionHelper {
    public static final int REQUEST_LOCATION = 101;
    public static final int REQUEST_CAMERA = 102;
    private PermissionHelper() {
    }
    public static boolean hasLocationPermission(Context context) {
        return has(context, Manifest.permission.ACCESS_FINE_LOCATION)
                || has(context, Manifest.permission.ACCESS_COARSE_LOCATION);
    }
    public static boolean hasCameraPermission(Context context) {
        return has(context, Manifest.permission.CAMERA);
    }
    private static boolean has(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }
    public static void requestLocation(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, REQUEST_LOCATION);
    }
    public static void requestCamera(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
    }
    public static String deniedMessage(String feature) {
        return "Permissão recusada.";
    }
}