package util;

import android.content.Context;
import android.content.SharedPreferences;

import model.User;

public final class SessionManager {
    private static final String PREFERENCES = "triptrail_session";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";

    private static SessionManager instance;

    private final SharedPreferences preferences;

    private SessionManager(Context context) {
        this.preferences = context.getApplicationContext()
                .getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void login(User user) {
        preferences.edit().putString(KEY_USER_ID, user.getId()).putString(KEY_USER_NAME, user.getName()).putString(KEY_USER_EMAIL, user.getEmail()).apply();
    }

    public void logout() {
        preferences.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return getUserId() != null;
    }

    public String getUserId() {
        return preferences.getString(KEY_USER_ID, null);
    }

    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return preferences.getString(KEY_USER_EMAIL, "");
    }
}
