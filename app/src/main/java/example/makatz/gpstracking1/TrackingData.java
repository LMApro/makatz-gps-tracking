package example.makatz.gpstracking1;

/**
 * Created by makatz on 3/9/2016.
 */
public class TrackingData {
    private double latitude;
    private double longitude;
    private String timestamp;
    private float speed;
    private String userEmail;

    public TrackingData(double latitude, double longitude, float speed, String timestamp, String userEmail) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed * (float)3.6; // convert m/s to km/h
        this.timestamp = timestamp;
        this.userEmail = userEmail;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getSpeed() {
        return speed;
    }

    public String getTimestamp() {
        return timestamp;
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
                + String.valueOf(speed) + "|"
                + timestamp);
        return result;

    }
}
