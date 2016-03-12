package example.makatz.gpstracking1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


public class MainActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static Firebase ref = new Firebase(GPSTracking.FIREBASE_URL);
    private TextView txtUser;
    private Button btnStartTracking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUp();
    }

    private void setUp() {
        // binding views and set up properties
        txtUser = (TextView) findViewById(R.id.main_txt_active_user);
        btnStartTracking = (Button) findViewById(R.id.main_btn_toggle_tracking);
        btnStartTracking.setOnClickListener(this);

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!checkLocationAvailability(this)) {
            showDialogCheckLocation();
        }
    }

    private void showDialogCheckLocation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(getString(R.string.err_location_service_not_enabled))
                .setTitle(R.string.main_err_dialog_title)
                .setPositiveButton(R.string.go_to_location_settings, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.quit_app, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
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
    }


    public boolean checkPlayServicesAvailability(Context context) {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        return (resultCode == ConnectionResult.SUCCESS);
    }

    public boolean checkLocationAvailability(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }


    private void goToLogin() {
        Intent goToLogin = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(goToLogin);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.main_btn_toggle_tracking) {
            if (checkLocationAvailability(this)) {
                Intent goToMap = new Intent(MainActivity.this, MapsActivity.class);
                goToMap.putExtra(GPSTracking.TRACKING, true);
                startActivity(goToMap);
            } else {
                showDialogCheckLocation();
            }
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
            case R.id.main_menu_about:
                goToAbout();
                break;
            case R.id.main_menu_change_password:
                goToChangePassword();
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    private void goToChangePassword() {
        Intent goToChangePassword = new Intent(MainActivity.this, ChangePasswordActivity.class);
        startActivity(goToChangePassword);
    }

    private void goToAbout() {
        Intent goToAbout = new Intent(MainActivity.this, InfoActivity.class);
        startActivity(goToAbout);
    }

}
