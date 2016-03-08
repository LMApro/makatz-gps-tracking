package example.makatz.gpstracking1;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static Firebase ref = new Firebase(GPSTracking.FIREBASE_URL);
    private TextView txtUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUp();

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

    protected void setUp() {
        txtUser = (TextView) findViewById(R.id.main_txt_active_user);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setLogo(R.mipmap.ic_my_location_white);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

    }

    private void goToLogin() {
        Intent goToLogin = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(goToLogin);
    }

    @Override
    public void onClick(View v) {

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
}
