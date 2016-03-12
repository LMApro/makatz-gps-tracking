package example.makatz.gpstracking1;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static Firebase ref = new Firebase(GPSTracking.FIREBASE_URL);


    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        setUp();
        getMap();

    }

    private void setUp() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean isTrackingLocation = extras.getBoolean(GPSTracking.TRACKING); //true
            boolean isMapOnly = extras.getBoolean(GPSTracking.MAP_ONLY);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            stopTrackingLocation();
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }


    private void getMap() {
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(17);
        startTrackingLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "GoogleApiClient connection has been suspend");
        mGoogleApiClient.connect();
    }

    String oldAddress = "";
    double oldLatitude = 0.0;
    double oldLongitude = 0.0;
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {

            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();
            String currentAddress = getAddress(currentLatitude, currentLongitude);

            // SEND DATA WHENEVER COORDINATES CHANGED
            if ((oldLatitude != currentLatitude || oldLongitude != currentLongitude) && !oldAddress.equals(currentAddress)) {
                // data send to OM2M server
                TrackingData dataToOM2M = new TrackingData(
                        currentLatitude,
                        currentLongitude,
                        currentAddress,
                        location.getSpeed(),
                        location.getTime(),
                        ref.getAuth().getProviderData().get("email").toString()
                );

                // data send to Firebase
                TrackingData dataToFirebase = new TrackingData(
                        currentAddress,
                        location.getSpeed(),
                        location.getTime()
                );

                sendDataToOM2M(dataToOM2M);
                sendDataToFirebase(dataToFirebase);

                // move camera to current location
                goToMyLocation(currentLatitude, currentLongitude, location.getBearing());

                oldLatitude = currentLatitude;
                oldLongitude = currentLongitude;
            }


            // show Toast whenever current address changed
            if (!oldAddress.equals(currentAddress)) {
                Log.d(TAG, currentAddress);
                Toast.makeText(MapsActivity.this, currentAddress, Toast.LENGTH_SHORT).show();
                oldAddress = currentAddress;
            }
        }
    }

    private void goToMyLocation(double latitude, double longitude, float bearing) {
        changeCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder().target(new LatLng(latitude, longitude))
                        .zoom(18)
                        .bearing(bearing)
                        .tilt(25)
                        .build()
        ));
    }

    private void sendDataToFirebase(TrackingData data) {
        String userEmailReformatted = ref.getAuth().getProviderData().get("email").toString().replace('.', '-').replace('@', '-');
        ref.child(userEmailReformatted).push().setValue(data);
    }

    private void sendDataToOM2M(TrackingData data) {
        // OM2M is a server platform for Internet Of Things
        if (BuildConfig.DEBUG) {
            Log.d(TAG, data.toString());
        }
    }

    private void changeCamera(CameraUpdate update) {
        mMap.moveCamera(update);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "GoogleApiClient connection has failed");
    }

    private void startTrackingLocation() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void stopTrackingLocation() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public String getAddress(double latitude, double longitude) {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(latitude, longitude,
                    // In this sample, we get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            Log.e(TAG, ioException.getMessage());
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            Log.e(TAG, illegalArgumentException.getMessage());
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            Log.e(TAG, "No address found");
            return "Unknown address";
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            return TextUtils.join(", ", addressFragments);
        }

    }
}









