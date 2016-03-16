package example.makatz.gpstracking1;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by makatz on 3/9/2016.
 */
public class TrackingData {
    private double latitude;
    private double longitude;
    private String timestamp;
    private String address;
    private float speed;
    private String userEmail;


    public TrackingData(double latitude, double longitude, String address, float speed, long timestamp, String userEmail) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed * 3.6f; // convert m/s to km/h
        this.timestamp = formatTime(timestamp);
        this.userEmail = userEmail;
        this.address = address;
    }

    public TrackingData(String address, float speed, long timestamp) {
        this.address = address;
        this.speed = speed * 3.6f;
        this.timestamp = formatTime(timestamp);
    }

    public static String formatTime(long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.US);
        return formatter.format(timestamp);
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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getUserEmail() {
        return userEmail;
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
