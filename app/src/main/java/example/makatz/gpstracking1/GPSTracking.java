package example.makatz.gpstracking1;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by makatz on 3/6/2016.
 */
public class GPSTracking extends Application {
    public static final String FIREBASE_URL = "https://bk-gpstracking.firebaseio.com";

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

    }





}
