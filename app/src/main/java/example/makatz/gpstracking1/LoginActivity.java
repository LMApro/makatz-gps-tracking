package example.makatz.gpstracking1;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin;
    private EditText edtEmail;
    private EditText edtPassword;
    private TextView txtSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUp();
    }

    protected void setUp() {
        btnLogin = (Button) findViewById(R.id.login_btn);
        edtEmail = (EditText) findViewById(R.id.login_edt_email);
        edtPassword = (EditText) findViewById(R.id.login_edt_password);
        txtSignUp = (TextView) findViewById(R.id.login_txt_signup);
        btnLogin.setOnClickListener(this);
        txtSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_btn) {
            // Check valid input
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();
            boolean hasError = false;
            if (TextUtils.isEmpty(email)) {
                edtEmail.setError(getResources().getString(R.string.err_email_missing));
                hasError = true;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.setError(getResources().getString(R.string.err_email_invalid));
                hasError = true;
            } else if (TextUtils.isEmpty(password)) {
                edtPassword.setError(getResources().getString(R.string.login_err_password_missing));
                hasError = true;
            }

            if (!hasError) {
                logIn(edtEmail.getText().toString(), edtPassword.getText().toString());
            }

        } else if (v.getId() == R.id.login_txt_signup) {
            // redirect to SignUpActivity
            Intent goToSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(goToSignUp);
        }
    }

    private void logIn(String email, String password) {
        Firebase ref = new Firebase(GPSTracking.FIREBASE_URL);
        Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // Authenticated successfully with payload authData
                Log.d(TAG, authData.getUid());
                Log.d(TAG, authData.getToken());
                Intent returnToMain = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(returnToMain);
                finish();
            }
            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // Authenticated failed with error firebaseError
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage(firebaseError.getMessage())
                        .setTitle(R.string.login_err_dialog_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };

        ref.authWithPassword(email, password, authResultHandler);
    }
}
