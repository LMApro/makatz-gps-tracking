package example.makatz.gpstracking1;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * Created by makatz on 3/6/2016.
 */
public class LoginManager extends Application {
    public static final String SESSION_TOKEN = "sessionToken";
    public static final String USER_ID = "userId";

    private String sessionToken;
    private String userId;
    private SharedPreferences preferences;

    public LoginManager() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.sessionToken = preferences.getString(SESSION_TOKEN, null);
        this.userId = preferences.getString(USER_ID, null);
    }

    public boolean isLoggedIn() {
        return (!TextUtils.isEmpty(this.sessionToken)) && (!TextUtils.isEmpty(this.userId));
    }

    public void saveLoginCredential(String sessionToken, String userId) {
        this.sessionToken = sessionToken;
        this.userId = userId;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SESSION_TOKEN, sessionToken);
        editor.putString(USER_ID, userId);
        editor.apply();
    }

    public void logOut() {
        this.sessionToken = null;
        this.userId = null;
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(SESSION_TOKEN);
        editor.remove(USER_ID);
        editor.apply();
    }

}
