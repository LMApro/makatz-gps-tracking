package example.makatz.gpstracking1;

import android.app.Activity;

import java.io.Serializable;


/**
 * Created by makatz on 3/9/2016.
 */
public class TrackingData implements Serializable {
    private static final String TAG = TrackingData.class.getSimpleName();

    private double latitude;
    private double longitude;
    private String timestamp;
    private String address;
    private float speed;
    private String userEmail;


    public TrackingData(double latitude, double longitude, String address, float speed, String timestamp, String userEmail) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed * 3.6f; // convert m/s to km/h
        this.timestamp = timestamp;
        this.userEmail = userEmail;
        this.address = address;
    }

    public TrackingData(String address, float speed, String timestamp) {
        this.address = address;
        this.speed = speed * 3.6f;
        this.timestamp = timestamp;
    }

    public String getAddress() {
        return address;
    }

    public float getSpeed() {
        return speed;
    }

    public String getTimestamp() {
        return timestamp;
    }


    @Override
    public String toString() {
        String result = "";
        result += (userEmail + "|"
                + String.valueOf(latitude) + "|"
                + String.valueOf(longitude) + "|"
                + address + "|"
                + String.valueOf(speed) + "|"
                + timestamp);
        return result;

    }



}
