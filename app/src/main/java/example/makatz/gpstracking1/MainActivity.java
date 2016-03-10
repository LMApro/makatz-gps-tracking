package example.makatz.gpstracking1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static Firebase ref = new Firebase(GPSTracking.FIREBASE_URL);
    private TextView txtUser;
    private TextView txtLocation;
    private TextView txtAddress;
    private boolean isTrackingLocation;
    private Button btnToggleTracking;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Address currentAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUp();
    }

    private void setUp() {
        // binding views and set up properties
        txtUser = (TextView) findViewById(R.id.main_txt_active_user);
        txtLocation = (TextView) findViewById(R.id.main_location);
        txtAddress = (TextView) findViewById(R.id.main_txt_address);
        btnToggleTracking = (Button) findViewById(R.id.main_btn_toggle_tracking);
        btnToggleTracking.setOnClickListener(this);
        isTrackingLocation = false;

        // set up the logo/icon for the app
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setLogo(R.mipmap.ic_my_location_white);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        // set up authentication with Firebase
        AuthData authData = ref.getAuth();

        if (authData != null) {
            txtUser.setText(getString(R.string.main_txt_active_user, authData.getProviderData().get("email").toString()));
        }

        ref.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData == null) {
                    // user is not logged in
                    goToLogin();
                    txtUser.setText("");
                }
            }
        });

        // create new instance of GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkPlayServicesAvailability(this)) {
            showError(getString(R.string.main_err_play_service));
        }
    }

    private void showError(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(errorMessage)
                .setTitle(R.string.main_err_dialog_title)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    public boolean checkPlayServicesAvailability(Context context) {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        return (resultCode == ConnectionResult.SUCCESS);
    }


    private void goToLogin() {
        Intent goToLogin = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(goToLogin);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.main_btn_toggle_tracking) {
            isTrackingLocation = !isTrackingLocation;
            if (isTrackingLocation) {
                startTrackingLocation();
                btnToggleTracking.setText("Stop tracking");
            } else {
                stopTrackingLocation();
                btnToggleTracking.setText("Start tracking");
            }
        }
    }

    private void startTrackingLocation() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void stopTrackingLocation() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.main_menu_logout:
                ref.unauth();
                break;
            case R.id.main_menu_open_map:
                openMap();
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    private void openMap() {
        Intent openMap = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(openMap);
    }


    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(17);

        if (bundle != null) {
            Log.d(TAG, "On connected: " + bundle.toString());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "GoogleApiClient connection has been suspend");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "GoogleApiClient connection has failed");
    }

    String oldAddress = "";
    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            TrackingData data = new TrackingData(location.getLatitude(), location.getLongitude(), location.getSpeed(), String.valueOf(location.getTime()), ref.getAuth().getProviderData().get("email").toString());
            Log.d(TAG, data.toString());
            txtLocation.setText(data.toString());
            if (!oldAddress.equals(getAddress(location))) {
                Log.d(TAG, getAddress(location));
                txtAddress.setText(getAddress(location));
                oldAddress = getAddress(location);
            }

        }
    }

    private String getAddress(Location location) {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
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
