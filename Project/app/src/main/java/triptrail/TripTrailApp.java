package triptrail;

import android.app.Application;

import org.osmdroid.config.Configuration;

public class TripTrailApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Configuration.getInstance().setUserAgentValue(getPackageName());
        Configuration.getInstance().load(this,
                getSharedPreferences("osmdroid", MODE_PRIVATE));
    }
}
