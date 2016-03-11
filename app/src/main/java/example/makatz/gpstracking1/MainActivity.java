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


public class MainActivity extends AppCompatActivity implements
        View.OnClickListener
//        GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener,
//        LocationListener
{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static Firebase ref = new Firebase(GPSTracking.FIREBASE_URL);
    private TextView txtUser;
    private Button btnToggleTracking;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUp();
    }

    private void setUp() {
        // binding views and set up properties
        txtUser = (TextView) findViewById(R.id.main_txt_active_user);
        btnToggleTracking = (Button) findViewById(R.id.main_btn_toggle_tracking);
        btnToggleTracking.setOnClickListener(this);

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
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mGoogleApiClient.connect();
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
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
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
            Intent goToMap = new Intent(MainActivity.this, MapsActivity.class);
            goToMap.putExtra(GPSTracking.TRACKING, true);
            startActivity(goToMap);
        }
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
                openMapOnly();
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    private void openMapOnly() {
        Intent openMapOnly = new Intent(MainActivity.this, MapsActivity.class);
        openMapOnly.putExtra(GPSTracking.MAP_ONLY, true);
        startActivity(openMapOnly);
    }


//    @Override
//    public void onConnected(Bundle bundle) {
//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(1000);
//        mLocationRequest.setFastestInterval(17);
//
//        if (bundle != null) {
//            Log.d(TAG, "On connected: " + bundle.toString());
//        }
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        Log.d(TAG, "GoogleApiClient connection has been suspend");
//        mGoogleApiClient.connect();
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Log.d(TAG, "GoogleApiClient connection has failed");
//    }

//    String oldAddress = "";
//    @Override
//    public void onLocationChanged(Location location) {
//
//        if (location != null) {
//            TrackingData data = new TrackingData(location.getLatitude(), location.getLongitude(), location.getSpeed(), String.valueOf(location.getTime()), ref.getAuth().getProviderData().get("email").toString());
//            Log.d(TAG, data.toString());
//            txtLocation.setText(data.toString());
//            if (!oldAddress.equals(getAddress(location))) {
//                Log.d(TAG, getAddress(location));
//                txtAddress.setText(getAddress(location));
//                oldAddress = getAddress(location);
//            }
//
//        }
//    }


}
